package dev.battlesweeper;

import dev.battlesweeper.event.Event;
import dev.battlesweeper.objects.Position;
import dev.battlesweeper.widgets.GameView;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {

    private Disposable eventListener;

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
            eventListener = gameView.getEventHandler()
                    .listenFor(Event.class)
                    .subscribe(event -> {
                        log.info(event.toString());
                    });

            root.getChildren().add(gameView);

            Scene scene = new Scene(root,800,800);
            stage.setScene(scene);
            stage.setResizable(false);
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