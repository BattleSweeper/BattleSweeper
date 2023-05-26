package dev.battlesweeper.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.battlesweeper.network.body.ResultPacket;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class RESTRequestHandler {

    private final ObjectMapper mapper;
    private final URI targetUri;
    private HttpClient client;

    public RESTRequestHandler(String url) throws URISyntaxException {
        this.targetUri = new URI(url);
        this.mapper = new ObjectMapper();
    }

    public <T> Optional<T> post(String body, Class<T> tClass) {
        var request = HttpRequest
                .newBuilder(targetUri)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        var result = getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::unwrapResultPacket)
                .thenApply(str -> {
                    try {
                        return mapper.readValue(str, tClass);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .join();
        return Optional.ofNullable(result);
    }

    public <T> Optional<T> post(Map<String, String> params, Class<T> tClass) throws JsonProcessingException {
        var body = mapper.writeValueAsString(params);
        return post(body, tClass);
    }

    public <V, T> Optional<T> post(V params, Class<T> tClass) throws JsonProcessingException {
        var body = mapper.writeValueAsString(params);
        return post(body, tClass);
    }

    private HttpClient getClient() {
        if (client == null)
            client = HttpClient.newHttpClient();
        return client;
    }

    private String unwrapResultPacket(String result) {
        try {
            return mapper.readValue(result, ResultPacket.class).message;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
