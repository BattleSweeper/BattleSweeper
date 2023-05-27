package dev.battlesweeper.scenes;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.battlesweeper.Session;
import dev.battlesweeper.event.Event;
import dev.battlesweeper.network.WebsocketTest;
import dev.battlesweeper.objects.GameUpdateCallback;
import dev.battlesweeper.objects.Position;
import dev.battlesweeper.objects.UserGameStatus;
import dev.battlesweeper.objects.UserInfo;
import dev.battlesweeper.objects.json.PacketHandlerModule;
import dev.battlesweeper.objects.packet.*;
import dev.battlesweeper.widgets.GameView;
import dev.battlesweeper.widgets.RankCellFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

@Slf4j
public class MultiSweeperController implements Initializable, GameUpdateCallback {

    @FXML private AnchorPane container;
    @FXML private Pane       gameViewContainer;
    @FXML private ListView<UserGameStatus> listViewRank;

    private final List<UserGameStatus> rank = new ArrayList<>();
    private ObjectMapper mapper;
    private GameView gameView;
    private WebsocketTest socket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapper = new ObjectMapper();
        mapper.registerModule(new PacketHandlerModule());

        listViewRank.setCellFactory(new RankCellFactory());
        listViewRank.setItems(FXCollections.observableList(rank));

        setupConnection(Session.getInstance().roomId, this);
    }

    @Override
    public void onGameStart(List<UserInfo> users, Position boardSize, Position[] mines) {
        users.stream()
                .map(v -> new UserGameStatus(v, 0, 0))
                .forEach(rank::add);
        sortRank();
        listViewRank.setItems(FXCollections.observableList(rank));

        log.info(Arrays.toString(mines));
        gameView = new GameView(700, 700, mines);
        gameView.getEventHandler()
                .listenFor(Event.class)
                .subscribe(event -> {
                    Packet packet = null;
                    if (event instanceof GameView.TileUpdateEvent tileUpdateEvent) {
                        packet = TileUpdatePacket
                                .builder()
                                .bombLeft(tileUpdateEvent.getBombLeft())
                                .position(tileUpdateEvent.getPosition())
                                .action(tileUpdateEvent.getAction())
                                .build();
                    } else if (event instanceof GameView.GameOverEvent)
                        packet = new GameUpdatePacket(GameUpdatePacket.STATE_OVER);
                    else if (event instanceof GameView.GameWinEvent)
                        packet = new GameUpdatePacket(GameUpdatePacket.STATE_WIN);

                    if (packet != null) {
                        var msg = mapper.writeValueAsString(packet);
                        socket.send(msg);
                    }
                });
        Platform.runLater(() -> {
            gameViewContainer.getChildren().add(gameView);
        });
    }

    @Override
    public void onTileUpdate(UserInfo user, Position position, int action, int bombLeft) {

    }

    @Override
    public void onGameUpdate(UserInfo user, int state) {

    }

    @Override
    public void onError() {

    }

    private void sortRank() {
        Collections.sort(rank);
    }

    private void setupConnection(UUID roomId, GameUpdateCallback callback) {
        try {
            final var headers = new HashMap<String, String>();
            headers.put("room-id", roomId.toString());

            socket = new WebsocketTest("/room", headers);
            socket.getEventHandler()
                    .listenFor(WebsocketTest.PacketEvent.class)
                    .subscribe(packetEvent -> {
                        var packet = packetEvent.getPacket();
                        log.info(packet.toString());
                        if (packet instanceof GameStartPacket gameStartPacket)
                            callback.onGameStart(gameStartPacket.users, gameStartPacket.boardSize, gameStartPacket.mines);

                        if (!(packet instanceof PlayerUpdatePacket))
                            return;
                        var userInfo = ((PlayerUpdatePacket) packet).getUser();
                        packet = ((PlayerUpdatePacket) packet).getPacket();

                        if (packet instanceof TileUpdatePacket tilePacket) {
                            var pos      = tilePacket.getPosition();
                            var action   = tilePacket.getAction();
                            var bombLeft = tilePacket.getBombLeft();
                            callback.onTileUpdate(userInfo, pos, action, bombLeft);
                        } else if (packet instanceof GameUpdatePacket gamePacket) {
                            var state = gamePacket.getState();
                            callback.onGameUpdate(userInfo, state);
                        }
                    });

            socket.connect();
        } catch (URISyntaxException e) {
            callback.onError();
        }
    }
}
