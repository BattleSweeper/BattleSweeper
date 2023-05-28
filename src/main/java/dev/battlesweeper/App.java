package dev.battlesweeper;

import dev.battlesweeper.network.WebsocketTest;
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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Session.getInstance().rootStage = stage;

            var sceneRes = Objects.requireNonNull(App.class.getResource("SplashScene.fxml"));
            AnchorPane root = FXMLLoader.load(sceneRes);

            Scene scene = new Scene(root,1200,800);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("BattleSweeper Application");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}