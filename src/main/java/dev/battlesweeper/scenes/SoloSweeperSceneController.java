package dev.battlesweeper.scenes;


import dev.battlesweeper.BoardGenerator;
import dev.battlesweeper.objects.Position;
import dev.battlesweeper.objects.UserGameStatus;
import dev.battlesweeper.widgets.GameView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
@Slf4j
public class SoloSweeperSceneController implements Initializable{
    @FXML private AnchorPane container;
    @FXML private Pane       gameViewContainer;
    @FXML private ListView<UserGameStatus> listViewRank;

    @FXML private Button buttonPrev;
    @FXML private Button buttonPause;

    private final ObservableList<UserGameStatus> rank = FXCollections.observableArrayList();

    private GameView gameView;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonPrev.setOnAction(event -> {

        });

        var mines = BoardGenerator.generateMines(new Position(16, 16), 40);

        gameView = new GameView(700, 700, mines);

        gameViewContainer.getChildren().add(gameView);

    }


}
