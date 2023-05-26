package dev.battlesweeper.scenes;

import dev.battlesweeper.utils.ResourceUtils;
import dev.battlesweeper.widgets.NotificationBubble;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML protected AnchorPane container;
    @FXML private BorderPane interactionContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var blur = new GaussianBlur();
        blur.setRadius(6);
        try {
            var loader = setFragment(ResourceUtils.getResource("WelcomeFragment.fxml"), 600);
            ((WelcomeFragmentController) loader.getController()).setParent(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected FXMLLoader setFragment(URL url) throws IOException {
        return setFragment(url, 0);
    }

    private FXMLLoader setFragment(URL url, long delay) throws IOException {
        var fade = new FadeTransition();
        fade.setDuration(Duration.millis(300));
        fade.setFromValue(0);
        fade.setToValue(10);
        fade.setDelay(Duration.millis(delay));

        var loader = new FXMLLoader(url);
        Node fragment = loader.load();
        fragment.setOpacity(0);
        fade.setNode(fragment);

        var controller = loader.getController();
        if (controller instanceof FragmentUpdater)
            ((FragmentUpdater) controller).setParent(this);

        interactionContainer.setCenter(fragment);
        fade.play();
        return loader;
    }
}
