package dev.battlesweeper.scenes;

import dev.battlesweeper.utils.FontUtils;
import dev.battlesweeper.utils.ResourceUtils;
import dev.battlesweeper.widgets.NotificationBubble;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeFragmentController implements Initializable, FragmentUpdater {

    @FXML private AnchorPane loginContainer;

    @FXML private Button buttonLogin;
    @FXML private Button buttonRegister;
    @FXML private Button buttonAnonymous;

    private HomeController parentController;

    @Override
    public void setParent(Initializable parent) {
        this.parentController = (HomeController) parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var font = FontUtils.loadFontFromResource("NanumSquareNeo-cBd.ttf", 20);
        loginContainer.getChildren()
                .stream()
                .filter(v -> v instanceof Button)
                .forEach(v -> {
                    ((Button) v).setFont(font);
                });

        buttonLogin.setOnAction(event -> {
            setFragment(ResourceUtils.getResource("LoginFragment.fxml"));
        });

        buttonRegister.setOnAction(event -> {
            setFragment(ResourceUtils.getResource("EmailCheckFragment.fxml"));
        });

        buttonAnonymous.setOnAction(event -> {
            setFragment(ResourceUtils.getResource("AnonLoginFragment.fxml"));
        });
    }

    private void setFragment(URL url) {
        try {
            parentController.setFragment(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
