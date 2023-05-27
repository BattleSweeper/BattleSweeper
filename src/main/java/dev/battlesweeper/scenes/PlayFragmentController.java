package dev.battlesweeper.scenes;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.battlesweeper.BoardGenerator;
import dev.battlesweeper.Env;
import dev.battlesweeper.Session;
import dev.battlesweeper.network.Message;
import dev.battlesweeper.network.RESTRequestHandler;
import dev.battlesweeper.network.WebsocketTest;
import dev.battlesweeper.network.body.TokenInfo;
import dev.battlesweeper.objects.GameQueueCallback;
import dev.battlesweeper.objects.Position;
import dev.battlesweeper.objects.packet.GameFoundPacket;
import dev.battlesweeper.utils.ResourceUtils;
import dev.battlesweeper.utils.TimerUtils;
import dev.battlesweeper.utils.TransitionUtils;
import dev.battlesweeper.widgets.GameView;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
public class PlayFragmentController implements Initializable, FragmentUpdater {

    @FXML private Label labelWelcomeText;
    @FXML private Button buttonSolo;
    @FXML private Button buttonDuo;
    @FXML private Button buttonBattle;
    @FXML private Button buttonSettings;
    @FXML private Button buttonExit;

    private HomeController parentController;

    @Override
    public void setParent(Initializable parent) {
        this.parentController = (HomeController) parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var username = Session.getInstance().userName;
        labelWelcomeText.setText(labelWelcomeText.getText().replace("NAME", username));

        buttonSolo.setOnAction(event -> {
            var mines = BoardGenerator.generateMines(new Position(16, 16), 40);
            var gameScene = new GameView(700, 700, mines);
            gameScene.setOpacity(0);

            TransitionUtils.crossFade(
                    parentController.container.getScene().getRoot(),
                    gameScene,
                    Duration.millis(300),
                    v -> parentController.container.getScene().setRoot(gameScene),
                    null
            );
        });

        buttonDuo.setOnAction(event -> {
            parentController.showMatchDialog();
            TimerUtils.setTimer(700, () -> {
                handleGameQueue(true, new GameQueueCallback() {
                    @Override
                    public void onFound(UUID roomId) {
                        parentController.hideMatchDialog();
                        Session.getInstance().roomId = roomId;
                        log.info("Found");

                        try {
                            AnchorPane gameScene = FXMLLoader.load(ResourceUtils.getResource("MultiSweeperScene.fxml"));
                            parentController.container.getScene().setRoot(gameScene);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure() {
                        parentController.hideMatchDialog();
                        log.info("Failure");
                    }
                });
            });
        });

        buttonBattle.setOnAction(event -> {
            parentController.showMatchDialog();
            TimerUtils.setTimer(700, () -> {
                handleGameQueue(true, new GameQueueCallback() {
                    @Override
                    public void onFound(UUID roomId) {
                        parentController.hideMatchDialog();
                        Session.getInstance().roomId = roomId;
                        log.info("Found");

                        try {
                            AnchorPane gameScene = FXMLLoader.load(ResourceUtils.getResource("MultiSweeperScene.fxml"));
                            parentController.container.getScene().setRoot(gameScene);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure() {
                        parentController.hideMatchDialog();
                        log.info("Failure");
                    }
                });
            });
        });

        buttonSettings.setOnAction(event -> {

        });

        buttonExit.setOnAction(event -> {
            System.exit(0);
        });
    }

    private void handleGameQueue(boolean isDuo, GameQueueCallback callback) {
        try {
            List<Disposable> garbage = new ArrayList<>();

            var wsTest = new WebsocketTest("/queue/register");
            var evPacket = wsTest.getEventHandler()
                    .listenFor(WebsocketTest.PacketEvent.class)
                    .subscribe(packetEvent -> {
                        var packet = packetEvent.getPacket();
                        if (packet instanceof GameFoundPacket)
                            callback.onFound(((GameFoundPacket) packet).roomID);
                    });
            garbage.add(evPacket);

            var evClose = wsTest.getEventHandler()
                    .listenFor(WebsocketTest.CloseEvent.class)
                    .subscribe(closeEvent -> {
                        garbage.forEach(Disposable::dispose);
                        garbage.clear();
                        if (closeEvent.getReason().equals(Message.TOKEN_EXPIRED)) {
                            try {
                                var session = Session.getInstance();
                                var params = new HashMap<String, String>();
                                params.put("token", session.tokenInfo.getRefreshToken());
                                var token = new RESTRequestHandler(Env.SERVER_HTTP_ENDPOINT + "/auth/refresh")
                                        .postMessage(params, TokenInfo.class);
                                if (token.isEmpty())
                                    throw new RuntimeException("Token refresh failure");

                                log.info("Token refresh success");
                                session.tokenInfo = token.get();

                                handleGameQueue(isDuo, callback);
                            } catch (URISyntaxException | JsonProcessingException e) {
                                e.printStackTrace();
                                callback.onFailure();
                            }
                        }
                    });
            garbage.add(evClose);
            wsTest.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
