package dev.battlesweeper.scenes;

import dev.battlesweeper.App;
import dev.battlesweeper.BoardGenerator;
import dev.battlesweeper.Session;
import dev.battlesweeper.objects.Position;
import dev.battlesweeper.utils.ResourceUtils;
import dev.battlesweeper.utils.TransitionUtils;
import dev.battlesweeper.widgets.GameView;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class PlayFragmentController implements Initializable, FragmentUpdater {

    @FXML private Label labelWelcomeText;
    @FXML private Button buttonSolo;
    @FXML private Button buttonDuo;
    @FXML private Button buttonBattle;

    private HomeController parentController;

    @Override
    public void setParent(Initializable parent) {
        this.parentController = (HomeController) parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var username = Session.getInstance().userName;
        labelWelcomeText.setText(labelWelcomeText.getText().replace("NAME", username));

        buttonSolo.setOnAction(event -> {
            var mines = BoardGenerator.generateMines(new Position(16, 16), 40);
            var gameScene = new GameView(700, 700, mines);
            gameScene.setOpacity(0);

            TransitionUtils.crossFade(
                    parentController.container.getScene().getRoot(),
                    gameScene,
                    Duration.millis(300),
                    v -> parentController.container.getScene().setRoot(gameScene),
                    null
            );
        });

        buttonDuo.setOnAction(event -> {
            //openMatchmakingScene();
            parentController.showMatchDialog();
        });
    }

    private MatchmakingController openMatchmakingScene() {
        try {
            var loader = new FXMLLoader(ResourceUtils.getResource("MatchmakingScene.fxml"));
            AnchorPane matchRes = loader.load();
            for (var child : matchRes.getChildren()) {
                child.setOpacity(0);
            }

            for (var child : parentController.container.getChildren().filtered(v -> !Objects.equals(v.getId(), "container"))) {
                TransitionUtils.fadeOut(child, Duration.millis(300));
            }
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Session.getInstance().rootStage.getScene().setRoot(matchRes);
                    for (var child : matchRes.getChildren().filtered(v -> !Objects.equals(v.getId(), "container"))) {
                        TransitionUtils.fadeIn(child, Duration.millis(300));
                    }
                }
            }, 295);

            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
