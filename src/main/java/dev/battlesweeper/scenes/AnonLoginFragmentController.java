package dev.battlesweeper.scenes;

import dev.battlesweeper.Env;
import dev.battlesweeper.Session;
import dev.battlesweeper.network.RESTRequestHandler;
import dev.battlesweeper.network.body.AuthRequest;
import dev.battlesweeper.network.body.TokenInfo;
import dev.battlesweeper.utils.ResourceUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

@Slf4j
public class AnonLoginFragmentController implements Initializable, FragmentUpdater {

    private static final char[] alphanumericPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();

    @FXML private TextField inputUsername;
    @FXML private Button buttonPrev;
    @FXML private Button buttonSubmit;

    private HomeController parentController;

    @Override
    public void setParent(Initializable parent) {
        this.parentController = (HomeController) parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonPrev.setOnAction(event -> {
            openFragment("WelcomeFragment");
        });

        buttonSubmit.setOnAction(event -> {
            if (!checkInputSanity(inputUsername))
                return;

            var username = inputUsername.getText() + "#" + randomTag();
            log.info("name= " + username);

            var body = AuthRequest.builder()
                    .type(AuthRequest.TYPE_ANONYMOUS)
                    .info(AuthRequest.AuthInfo.builder()
                            .username(username)
                            .build())
                    .build();

            try {
                var response = new RESTRequestHandler(Env.SERVER_HTTP_ENDPOINT + "/auth")
                        .postMessage(body, TokenInfo.class);

                if (response.isPresent()) {
                    var session = Session.getInstance();
                    session.tokenInfo = response.get();
                    session.userName  = username;
                    log.info("auth success");

                    openFragment("PlayFragment");
                } else {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("서버와의 통신에 실패했습니다. 잠시 후 다시 시도해주세요.");
                    alert.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("서버와의 통신에 실패했습니다. 잠시 후 다시 시도해주세요.");
                alert.show();
            }
        });
    }

    private void openFragment(String name) {
        try {
            parentController.setFragment(ResourceUtils.getResource(name + ".fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String randomTag() {
        var rand = new Random();
        final var buffer = new StringBuilder();
        for (var i = 0; i < 4; ++i)
            buffer.append(alphanumericPool[rand.nextInt(alphanumericPool.length)]);
        return buffer.toString();
    }

    private boolean checkInputSanity(TextField ...fields) {
        boolean result = true;
        for (var field : fields) {
            if (field.getText().isBlank()) {
                field.getStyleClass().add("error");
                result = false;
            } else {
                field.getStyleClass().remove("error");
            }
        }
        return result;
    }
}
