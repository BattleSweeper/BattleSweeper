package dev.battlesweeper.scenes;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.battlesweeper.Env;
import dev.battlesweeper.Session;
import dev.battlesweeper.network.RESTRequestHandler;
import dev.battlesweeper.objects.packet.ResultPacket;
import dev.battlesweeper.utils.ResourceUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class EmailCheckFragmentController implements Initializable, FragmentUpdater {

    @FXML private TextField inputEmail;
    @FXML private TextField inputAuthCode;
    @FXML private Button    buttonEmailAuth;
    @FXML private Button    buttonAuthCodeCheck;
    @FXML private Button    buttonPrev;
    @FXML private ProgressIndicator progress;

    private HomeController parentController;

    @Override
    public void setParent(Initializable parent) {
        this.parentController = (HomeController) parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonPrev.setOnAction(event -> {
            try {
                parentController.setFragment(ResourceUtils.getResource("WelcomeFragment.fxml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        buttonAuthCodeCheck.setOnAction(event -> {
            if (!checkInputSanity(inputEmail, inputAuthCode))
                return;
            if (inputAuthCode.isDisabled())
                return;

            String code  = inputAuthCode.getText();
            String email = inputEmail.getText();
            ResultPacket result;
            try {
                var res = new RESTRequestHandler(Env.SERVER_HTTP_ENDPOINT + "/register/checkCode?email=" + email + "&code=" + code)
                        .post(new HashMap<>());
                if (res.isEmpty()) {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("서버와의 통신에 실패했습니다. 나중에 다시 시도해주세요.");
                    alert.show();
                    return;
                }
                result = res.get();
            } catch (JsonProcessingException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
            if (result.result != 200) {
                String msg;
                if (result.message.equals("NO_REQUEST"))
                    msg = "이 메일 주소로 발송된 코드가 없습니다. 이것은 의도되지 않은 상황일 수 있습니다.";
                else if (result.message.equals("INVALID_CODE"))
                    msg = "인증 코드가 일치하지 않습니다.";
                else
                    msg = "알 수 없는 에러가 발생했습니다. 잠시 후 다시 시도해주세요.";
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(msg);
                alert.show();
                return;
            }

            try {
                Session.getInstance().email = email;
                Session.getInstance().code  = code;
                parentController.setFragment(ResourceUtils.getResource("RegisterFragment.fxml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        buttonEmailAuth.setOnAction(event -> {
            if (!checkInputSanity(inputEmail))
                return;

            String email = inputEmail.getText();
            var isEmailValid = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
                    .matcher(email)
                    .matches();

            if (!isEmailValid) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("유효하지 않은 이메일 형식입니다.\n확인 후 다시 시도해주세요.");
                alert.show();
                return;
            }

            progress.setOpacity(1);
            ResultPacket result;
            try {
                var res = new RESTRequestHandler(Env.SERVER_HTTP_ENDPOINT + "/register?email=" + email)
                        .post(new HashMap<>());
                if (res.isEmpty()) {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("서버와의 통신에 실패했습니다. 나중에 다시 시도해주세요.");
                    alert.show();
                    return;
                }
                result = res.get();
            } catch (JsonProcessingException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
            progress.setOpacity(0);

            if (result.result == 400) {
                String msg;
                if (result.message.equals("ALREADY_SENT"))
                    msg = "이미 이 주소로 인증 메일이 전송되었습니다. 메일함을 확인해주세요.";
                else if (result.message.equals("ACCOUNT_EXISTS"))
                    msg = "이미 이 이메일로 가입된 계정이 있습니다. 이메일을 확인하고 다시 시도해주세요.";
                else
                    msg = "알 수 없는 에러가 발생했습니다. 잠시 후 다시 시도해주세요.";
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(msg);
                alert.show();
                return;
            }

            if (result.result == 200 && result.message.equals(email)) {
                inputEmail.setDisable(true);
                buttonEmailAuth.setDisable(true);
                inputAuthCode.setDisable(false);
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