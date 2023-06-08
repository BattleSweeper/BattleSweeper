package dev.battlesweeper.scenes;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.battlesweeper.Session;
import dev.battlesweeper.event.Event;
import dev.battlesweeper.network.WebsocketHandler;
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
import javafx.collections.ObservableList;
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

    private final ObservableList<UserGameStatus> rank = FXCollections.observableArrayList();
    private UserInfo      selfInfo;
    private int           totalMines;
    private ObjectMapper  mapper;
    private GameView      gameView;
    private WebsocketHandler socket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var claims = JWT.decode(Session.getInstance().tokenInfo.getAccessToken())
                .getClaims();
        selfInfo = new UserInfo(claims.get("uid").asLong(), claims.get("name").asString());

        mapper = new ObjectMapper();
        mapper.registerModule(new PacketHandlerModule());

        listViewRank.setCellFactory(new RankCellFactory());
        listViewRank.setItems(rank);

        setupConnection(Session.getInstance().roomId, this);
    }

    @Override
    public void onGameStart(List<UserInfo> users, Position boardSize, Position[] mines) {
        totalMines = mines.length;
        users.stream()
                .map(v -> new UserGameStatus(v, 0, 0))
                .forEach(rank::add);
        sortRank();

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
        //if (Objects.equals(user.id(), selfInfo.id()))
        //    return;
        rank.stream()
                .filter(v -> v.user().id().equals(user.id()))
                .findFirst()
                .ifPresent(v -> {
                    log.info("flags: " + (totalMines - bombLeft));
                    v.flags(bombLeft);
                });
        sortRank();
    }

    @Override
    public void onGameUpdate(UserInfo user, int state) {

    }

    @Override
    public void onError() {

    }

    private void sortRank() {
        Platform.runLater(() -> {
            Collections.sort(rank);
            int idx = 0;
            for (var data : rank)
                data.rank(++idx);
        });
    }

    private void setupConnection(UUID roomId, GameUpdateCallback callback) {
        try {
            final var headers = new HashMap<String, String>();
            headers.put("room-id", roomId.toString());

            socket = new WebsocketHandler("/room", headers);
            socket.getEventHandler()
                    .listenFor(WebsocketHandler.PacketEvent.class)
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
