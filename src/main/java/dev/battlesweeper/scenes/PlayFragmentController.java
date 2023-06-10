package dev.battlesweeper.scenes;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.battlesweeper.BoardGenerator;
import dev.battlesweeper.Env;
import dev.battlesweeper.Session;
import dev.battlesweeper.network.Message;
import dev.battlesweeper.network.RESTRequestHandler;
import dev.battlesweeper.network.WebsocketHandler;
import dev.battlesweeper.network.body.TokenInfo;
import dev.battlesweeper.objects.GameQueueCallback;
import dev.battlesweeper.objects.Position;
import dev.battlesweeper.objects.packet.GameFoundPacket;
import dev.battlesweeper.utils.ResourceUtils;
import dev.battlesweeper.utils.TimerUtils;
import dev.battlesweeper.utils.TransitionUtils;
import dev.battlesweeper.widgets.GameView;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

@Slf4j
public class PlayFragmentController implements Initializable, FragmentUpdater, EventHandler<ActionEvent> {

    @FXML private Label labelWelcomeText;
    @FXML private Button buttonSolo;
    @FXML private Button buttonDuo;
    @FXML private Button buttonBattle;
    @FXML private Button buttonSettings;
    @FXML private Button buttonExit;

    // True 시 게임 대기열 인원 수 무시 (무조건 바로 매치매이킹)
    private static final boolean BYPASS_QUEUE_SIZE = false;

    private HomeController parentController;
    private WebsocketHandler socketHandler;

    @Override
    public void setParent(Initializable parent) {
        this.parentController = (HomeController) parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var username = Session.getInstance().userName;
        labelWelcomeText.setText(labelWelcomeText.getText().replace("NAME", username));

        buttonSolo.setOnAction(event -> {
            try {
                AnchorPane gameScene = FXMLLoader.load(ResourceUtils.getResource("SoloSweeperScene.fxml"));
                var scene = parentController.container.getScene();
                if (scene != null)
                    scene.setRoot(gameScene);
                else
                    log.error("Scene == null");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        buttonDuo.setOnAction(this);
        buttonBattle.setOnAction(this);

        buttonSettings.setOnAction(event -> {
            setFragment(ResourceUtils.getResource("SettingFragment.fxml"));
        });

        buttonExit.setOnAction(event -> {
            System.exit(0);
        });
    }

    @Override
    public void handle(ActionEvent event) {
        parentController.showMatchDialog(() -> {
            if (socketHandler != null)
                socketHandler.close(1001);
        });

        boolean isDuo = event.getSource() == buttonDuo;
        TimerUtils.setTimer(700, () -> {
            socketHandler = handleGameQueue(isDuo, new GameQueueCallback() {
                @Override
                public void onFound(UUID roomId) {
                    parentController.hideMatchDialog();
                    Session.getInstance().roomId = roomId;
                    log.info("Found");

                    try {
                        AnchorPane gameScene = FXMLLoader.load(ResourceUtils.getResource("MultiSweeperScene.fxml"));
                        var scene = parentController.container.getScene();
                        if (scene != null)
                            scene.setRoot(gameScene);
                        else
                            log.error("Scene == null");
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
    }

    private WebsocketHandler handleGameQueue(boolean isDuo, GameQueueCallback callback) {
        try {
            List<Disposable> garbage = new ArrayList<>();
            String endpoint;
            if (isDuo)
                endpoint = "/queue/register/duo";
            else
                endpoint = "/queue/register/battle";

            Map<String, String> headers = new HashMap<>();
            if (BYPASS_QUEUE_SIZE) {
                headers.put("BYPASS_SIZE", "true");
                log.info("BYPASS_SIZE = true");
            }

            var wsTest = new WebsocketHandler(endpoint, headers);
            var evPacket = wsTest.getEventHandler()
                    .listenFor(WebsocketHandler.PacketEvent.class)
                    .subscribe(packetEvent -> {
                        var packet = packetEvent.getPacket();
                        if (packet instanceof GameFoundPacket)
                            callback.onFound(((GameFoundPacket) packet).roomID);
                    });
            garbage.add(evPacket);

            var evClose = wsTest.getEventHandler()
                    .listenFor(WebsocketHandler.CloseEvent.class)
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
            return wsTest;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    private void setFragment(URL url) {
        try {
            parentController.setFragment(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
