package dev.battlesweeper.scenes;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class LoginFragmentController implements Initializable, FragmentUpdater {

    @FXML private TextField     inputEmail;
    @FXML private PasswordField inputPassword;
    @FXML private Button        buttonSubmit;
    @FXML private Button        buttonPrev;

    private HomeController parentController;
    private ObjectMapper mapper;

    @Override
    public void setParent(Initializable parent) {
        this.parentController = (HomeController) parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapper = new ObjectMapper();

        buttonPrev.setOnAction(event -> {
            try {
                parentController.setFragment(ResourceUtils.getResource("WelcomeFragment.fxml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        buttonSubmit.setOnAction(event -> {
            if (!checkInputSanity(inputEmail, inputPassword))
                return;

            var body = AuthRequest.builder()
                    .type(AuthRequest.TYPE_REGISTERED)
                    .info(AuthRequest.AuthInfo.builder()
                            .email(inputEmail.getText())
                            .password(inputPassword.getText())
                            .build())
                    .build();

            try {
                var response = new RESTRequestHandler(Env.SERVER_HTTP_ENDPOINT + "/auth")
                        .post(body);

                if (response.isPresent()) {
                    var resPacket = response.get();
                    String alertMessage = null;
                    switch (resPacket.result) {
                        case 404 -> {
                            alertMessage = "유저가 존재하지 않습니다.";
                        }
                        case 400 -> {
                            if (resPacket.message.equals("PASSWORD_MISMATCH"))
                                alertMessage = "비밀번호가 일치하지 않습니다.";
                            else
                                alertMessage = "잘못된 데이터 송신";
                        }
                    }
                    if (alertMessage != null) {
                        var alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText(alertMessage);
                        alert.show();
                        return;
                    }

                    TokenInfo tokenInfo = mapper.readValue(resPacket.message, TokenInfo.class);
                    var session = Session.getInstance();
                    session.tokenInfo = tokenInfo;
                    // TODO: Get username from server
                    //session.userName  = username;
                    log.info(tokenInfo.getAccessToken());
                    session.userName  = getUsernameFromToken(tokenInfo.getAccessToken());
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

    private boolean checkInputSanity(TextField...fields) {
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

    private String getUsernameFromToken(String token) {
        return JWT.decode(token)
                .getClaim("name")
                .asString();
    }
}
