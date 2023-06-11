package dev.battlesweeper.scenes;


import dev.battlesweeper.BoardGenerator;
import dev.battlesweeper.Session;
import dev.battlesweeper.event.Event;
import dev.battlesweeper.objects.Position;
import dev.battlesweeper.objects.UserGameStatus;
import dev.battlesweeper.objects.UserInfo;
import dev.battlesweeper.objects.packet.Packet;
import dev.battlesweeper.widgets.GameView;
import dev.battlesweeper.widgets.RankCellFactory;
import javafx.application.Platform;
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
import java.net.URL;
import java.util.*;
@Slf4j
public class SoloSweeperSceneController implements Initializable{
    @FXML private AnchorPane container;
    @FXML private Pane       gameViewContainer;
    @FXML private ListView<UserGameStatus> listViewRank;

    @FXML private Button buttonPrev;
    @FXML private Button buttonReset;

    @FXML private GameView gameView;

    private final ObservableList<UserGameStatus> rank = FXCollections.observableArrayList();


    private int totalMines;

    private int score;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        listViewRank.setCellFactory(new RankCellFactory());
        listViewRank.setItems(rank);

        buttonReset.setOnAction(resetevent -> {
            var mines = BoardGenerator.generateMines(new Position(16, 16), 40);
            totalMines = mines.length;
            sortRank();

            log.info(Arrays.toString(mines));
            gameView = new GameView(700, 700, mines);

            gameView.getEventHandler()
                    .listenFor(Event.class)
                    .subscribe(event -> {
                        if (event instanceof GameView.GameWinEvent){
                            var username = Session.getInstance().userName;
                            score = 1000 - gameView.timerValue - gameView.explodedBomb*25;

                            rank.stream()
                                    .findFirst()
                                    .ifPresent(v -> {
                                        log.info("flags: " + (totalMines - gameView.explodedBomb));
                                        v.flags(score);
                                        sortRank();
                                    });

                            long rankid = 1;
                            UserGameStatus userGameStatus = new UserGameStatus(new UserInfo(rankid, username), rank.size() + 1, score);
                            userGameStatus.rank(rank.size() + 1);
                            userGameStatus.flags(score);
                            rank.add(userGameStatus);
                            sortRank();

                            rankid++;
                        }else if(event instanceof GameView.GameOverEvent){
                            gameView.setDisable(true);
                        }

                    });


            Platform.runLater(() -> {
                // 게임 뷰 초기화 코드
                gameViewContainer.getChildren().clear();
                gameViewContainer.getChildren().add(gameView);
            });

        });

        buttonReset.fire();


    }

    private void sortRank() {
        Platform.runLater(() -> {
            Collections.sort(rank);
            int idx = 0;
            for (var data : rank)
                data.rank(++idx);
        });
    }



}
