package dev.battlesweeper.scenes;

import dev.battlesweeper.App;
import dev.battlesweeper.utils.TransitionUtils;
import dev.battlesweeper.widgets.NotificationBubble;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class SplashController implements Initializable {

    @FXML private GridPane container;
    @FXML private ImageView typo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typo.setSmooth(true);

        var enterTransition = new FadeTransition();
        enterTransition.setDuration(Duration.millis(800));
        enterTransition.setFromValue(0);
        enterTransition.setToValue(10);
        enterTransition.setDelay(Duration.millis(300));
        enterTransition.setNode(container);

        enterTransition.setOnFinished(event -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                TransitionUtils.fadeOut(container, Duration.millis(800), event -> {
                    try {
                        AnchorPane homeRes = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("HomeScene.fxml")));
                        homeRes.setOpacity(0);
                        enterTransition.setNode(homeRes);
                        enterTransition.setDuration(Duration.millis(300));
                        enterTransition.setOnFinished(null);
                        enterTransition.play();
                        container.getScene().setRoot(homeRes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }, 1000));

        enterTransition.play();
    }
}
