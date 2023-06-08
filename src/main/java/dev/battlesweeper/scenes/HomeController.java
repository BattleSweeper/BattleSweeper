package dev.battlesweeper.scenes;

import dev.battlesweeper.utils.ResourceUtils;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class HomeController implements Initializable {

    private static final long MINUTE = 1000 * 60;

    @FXML protected AnchorPane container;
    @FXML private BorderPane   interactionContainer;
    @FXML private Pane         matchmakingDialog;
    @FXML private Button       buttonCancelMatch;
    @FXML private Label        labelElapsedTime;

    private long matchStartMillis;
    private Timer timeCountTimer;
    private Runnable onDialogDismiss;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            buttonCancelMatch.setOnAction(event -> {
                hideMatchDialog();
            });

            var loader = setFragment(ResourceUtils.getResource("WelcomeFragment.fxml"), 600);
            ((WelcomeFragmentController) loader.getController()).setParent(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void showMatchDialog(Runnable onDismiss) {
        if (onDialogDismiss != null)
            hideMatchDialog();
        onDialogDismiss = onDismiss;

        setupTimeCountTimer();
        var transition = new TranslateTransition(Duration.millis(700));
        transition.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                return (t == 1.0) ? 1.0 : 1 - Math.pow(2.0, -10 * t);
            }
        });
        transition.setNode(matchmakingDialog);
        transition.setFromY(matchmakingDialog.getHeight());
        transition.setToY(0);

        matchmakingDialog.setVisible(true);
        transition.play();
    }

    void hideMatchDialog() {
        var transition = new TranslateTransition(Duration.millis(800));
        transition.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                return (t == 1.0) ? 1.0 : 1 - Math.pow(2.0, -10 * t);
            }
        });
        transition.setNode(matchmakingDialog);
        transition.setToY(matchmakingDialog.getTranslateY() + matchmakingDialog.getHeight());
        transition.setFromY(matchmakingDialog.getTranslateY());
        transition.setOnFinished(event -> {
            matchmakingDialog.setVisible(false);
            cancelTimeCountTimer();
        });

        if (onDialogDismiss != null) {
            onDialogDismiss.run();
            onDialogDismiss = null;
        }

        transition.play();
    }

    private void setupTimeCountTimer() {
        cancelTimeCountTimer();

        matchStartMillis = System.currentTimeMillis();
        timeCountTimer = new Timer();
        timeCountTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                var elapsedTime = System.currentTimeMillis() - matchStartMillis;
                Platform.runLater(() ->
                        labelElapsedTime.setText(
                                (elapsedTime / MINUTE) + "분 " + ((elapsedTime % MINUTE) / 1000) + "초 대기중...")
                );
            }
        }, 0, 1000);
    }

    private void cancelTimeCountTimer() {
        if (timeCountTimer != null) {
            timeCountTimer.cancel();
            timeCountTimer = null;
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
