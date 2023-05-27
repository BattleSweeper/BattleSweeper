package dev.battlesweeper.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.battlesweeper.objects.json.PacketHandlerModule;
import dev.battlesweeper.objects.packet.ResultPacket;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
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
        this.mapper.registerModule(new PacketHandlerModule());
    }

    public Optional<ResultPacket> post(String body) {
        var request = HttpRequest
                .newBuilder(targetUri)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        var result = getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(str -> {
                    try {
                        return mapper.readValue(str, ResultPacket.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                })
                .join();
        return Optional.ofNullable(result);
    }

    public Optional<ResultPacket> post(Map<String, String> params) throws JsonProcessingException {
        var body = mapper.writeValueAsString(params);
        return post(body);
    }

    public <V> Optional<ResultPacket> post(V params) throws JsonProcessingException {
        var body = mapper.writeValueAsString(params);
        return post(body);
    }

    public <T> Optional<T> postMessage(String body, Class<T> tClass) {
        var result = post(body);
        if (result.isEmpty())
            return Optional.empty();

        if (result.get().result != ResultPacket.RESULT_OK)
            return Optional.empty();

        T value;
        try {
            value = mapper.readValue(result.get().message, tClass);
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
        return Optional.ofNullable(value);
    }

    public <T> Optional<T> postMessage(Map<String, String> params, Class<T> tClass) throws JsonProcessingException {
        var body = mapper.writeValueAsString(params);
        return postMessage(body, tClass);
    }

    public <V, T> Optional<T> postMessage(V params, Class<T> tClass) throws JsonProcessingException {
        var body = mapper.writeValueAsString(params);
        return postMessage(body, tClass);
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
