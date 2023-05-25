package dev.battlesweeper;

import dev.battlesweeper.objects.Position;
import dev.battlesweeper.widgets.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            //var sceneRes = Objects.requireNonNull(App.class.getResource("GameImage.fxml"));
            //root = FXMLLoader.load(sceneRes);
            VBox root = new VBox();
            var mines = BoardGenerator.generateMines(new Position(16, 16), 5);
            var gameView = new GameView(700, 700, mines);
            root.getChildren().add(gameView);

            Scene scene = new Scene(root,800,800);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}