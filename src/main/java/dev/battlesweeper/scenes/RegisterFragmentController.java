package dev.battlesweeper.scenes;

import dev.battlesweeper.Env;
import dev.battlesweeper.Session;
import dev.battlesweeper.network.RESTRequestHandler;
import dev.battlesweeper.network.body.AuthRequest;
import dev.battlesweeper.network.body.RegisterRequestBody;
import dev.battlesweeper.objects.packet.ResultPacket;
import dev.battlesweeper.utils.ResourceUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class RegisterFragmentController implements Initializable, FragmentUpdater {

    @FXML private TextField inputUsername;
    @FXML private TextField inputPassword;
    @FXML private Button    buttonSubmit;
    @FXML private Button    buttonPrev;

    private HomeController parentController;

    @Override
    public void setParent(Initializable parent) {
        this.parentController = (HomeController) parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        buttonPrev.setOnAction(event -> {
            try {
                parentController.setFragment(ResourceUtils.getResource("EmailCheckFragment.fxml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        buttonSubmit.setOnAction(event -> {
            if (!checkInputSanity(inputUsername, inputPassword))
                return;

            var body = RegisterRequestBody.builder()
                    .code(Session.getInstance().code)
                    .email(Session.getInstance().email)
                    .name(inputUsername.getText())
                    .password(inputPassword.getText())
                    .build();
            ResultPacket result;
            try {
                var res = new RESTRequestHandler(Env.SERVER_HTTP_ENDPOINT + "/register/verify")
                        .post(body);
                if (res.isEmpty()) {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("서버와의 통신에 실패했습니다. 나중에 다시 시도해주세요.");
                    alert.show();
                    return;
                }
                result = res.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            log.info(result.result + " " + result.message);
            if (result.result != 200) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("서버와의 통신 중 에러가 발생했습니다. 나중에 다시 시도해주세요.");
                alert.show();
                return;
            }

            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("가입에 성공했습니다. 가입한 계정으로 로그인해주세요.");
            alert.show();

            try {
                parentController.setFragment(ResourceUtils.getResource("WelcomeFragment.fxml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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
