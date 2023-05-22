package dev.battlesweeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
	
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage stage) {
    	GridPane pane = new GridPane();
            StackPane root;
            try {
                var sceneRes = Objects.requireNonNull(App.class.getResource("GameImage.fxml"));
                root = FXMLLoader.load(sceneRes);
                Scene scene = new Scene(root,800,800);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}