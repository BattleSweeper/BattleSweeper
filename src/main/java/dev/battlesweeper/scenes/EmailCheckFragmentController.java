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

public class EmailCheckFragmentController implements Initializable, FragmentUpdater {

    @FXML private TextField inputEmail;
    @FXML private TextField inputAuthCode;
    @FXML private Button    buttonEmailAuth;
    @FXML private Button    buttonAuthCodeCheck;
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
        buttonAuthCodeCheck.setOnAction(event -> {
            if (!checkInputSanity(inputEmail, inputAuthCode))
                return;
            try {
                parentController.setFragment(ResourceUtils.getResource("RegisterFragment.fxml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        buttonEmailAuth.setOnAction(event -> {
            if (!checkInputSanity(inputEmail))
                return;
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