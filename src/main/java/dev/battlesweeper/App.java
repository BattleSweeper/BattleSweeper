package dev.battlesweeper;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class App extends Application {

    private Disposable eventListener;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Session.getInstance().rootStage = stage;

            var sceneRes = Objects.requireNonNull(App.class.getResource("SplashScene.fxml"));
            AnchorPane root = FXMLLoader.load(sceneRes);
            /*
            VBox root = new VBox();
            var mines = BoardGenerator.generateMines(new Position(16, 16), 5);
            var gameView = new GameView(700, 700, mines);
            eventListener = gameView.getEventHandler()
                    .listenFor(Event.class)
                    .subscribe(event -> {
                        log.info(event.toString());
                    });

            root.getChildren().add(gameView);
             */

            Scene scene = new Scene(root,1200,800);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("BattleSweeper Application");
            stage.show();
            stage.setOnCloseRequest(event -> {
                if (eventListener != null)
                    eventListener.dispose();
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}