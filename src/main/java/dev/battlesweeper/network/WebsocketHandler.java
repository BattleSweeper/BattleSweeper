package dev.battlesweeper.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.battlesweeper.Env;
import dev.battlesweeper.event.Event;
import dev.battlesweeper.event.EventHandler;
import dev.battlesweeper.event.MutableEventHandler;
import dev.battlesweeper.objects.json.PacketHandlerModule;
import dev.battlesweeper.objects.packet.Packet;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WebsocketHandler extends WebSocketClient {

    private final ObjectMapper mapper;
    private final MutableEventHandler eventHandler;

    public WebsocketHandler(String path) throws URISyntaxException {
        this(path, new HashMap<>());
    }

    public WebsocketHandler(String path, Map<String, String> headers) throws URISyntaxException {
        super(new URI(Env.SERVER_WSS_ENDPOINT + path));

        eventHandler = new MutableEventHandler();
        mapper = new ObjectMapper();
        mapper.registerModule(new PacketHandlerModule());

        var tokenInfo = dev.battlesweeper.Session.getInstance().tokenInfo;
        addHeader("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken());

        headers.forEach(this::addHeader);
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        log.info("connected");
    }

    @Override
    public void onMessage(String message) {
        log.info("message: " + message);
        try {
            Packet packet = mapper.readValue(message, Packet.class);
            eventHandler.fireEvent(PacketEvent.builder().packet(packet).build());
        } catch (JsonProcessingException e) {
            log.info("failure");
            log.error("Failed to read packet", e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("closed, code: " + code + " reason: " + reason);
        eventHandler.fireEvent(CloseEvent.builder().code(code).reason(reason).build());
    }

    @Override
    public void onError(Exception ex) {
        log.error("Error on socket", ex);
    }

    @Getter @Builder @ToString
    public static class PacketEvent implements Event {
        private Packet packet;
    }

    @Getter @Builder @ToString
    public static class CloseEvent implements Event {
        private int code;
        private String reason;
    }
}
