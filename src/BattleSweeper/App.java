package BattleSweeper;

import java.io.FileInputStream;
// 배경 테스트용
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

 
public class App extends Application {
	
    public static void main(String[] args) {
        launch(args);
    }
    
    //
 
    @Override
    public void start(Stage stage) {
    	GridPane pane = new GridPane();
            StackPane root;
            try {
                root = FXMLLoader.load(getClass().getResource("GameImage.fxml"));
                Scene scene = new Scene(root,800,800);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}