package dev.battlesweeper.scenes;

import dev.battlesweeper.Env;
import dev.battlesweeper.network.RESTRequestHandler;
import dev.battlesweeper.network.body.AuthRequest;
import dev.battlesweeper.utils.ResourceUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterFragmentController implements Initializable, FragmentUpdater {

    @FXML private TextField inputUsername;
    @FXML private TextField inputEmail;
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
                parentController.setFragment(ResourceUtils.getResource("WelcomeFragment.fxml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        buttonSubmit.setOnAction(event -> {
            if (!checkInputSanity(inputUsername, inputEmail, inputPassword))
                return;

            var body = AuthRequest.builder()
                    .type(AuthRequest.TYPE_REGISTERED)
                    .info(AuthRequest.AuthInfo.builder()
                            .email(inputEmail.getText())
                            .password(inputPassword.getText())
                            .build())
                    .build();

            try {
                var response = new RESTRequestHandler(Env.SERVER_HOST_URL + "/auth");
                //        .post(body, TokenResponse.class);
            } catch (Exception e) {
                e.printStackTrace();
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
