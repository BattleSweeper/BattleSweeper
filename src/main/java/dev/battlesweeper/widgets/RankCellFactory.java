package dev.battlesweeper.widgets;

import dev.battlesweeper.objects.UserGameStatus;
import dev.battlesweeper.utils.FontUtils;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class RankCellFactory implements Callback<ListView<UserGameStatus>, ListCell<UserGameStatus>> {

    private final Font font = FontUtils.loadFontFromResource("NanumSquareRoundB.ttf", 16);

    @Override
    public ListCell<UserGameStatus> call(ListView<UserGameStatus> param) {
        return new ListCell<>() {
            @Override
            protected void updateItem(UserGameStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    return;

                var view = new Pane();
                view.getStyleClass().add("cell");
                view.setPrefHeight(70);

                var nameLabel = new Label(item.user().name());
                nameLabel.getStyleClass().add("h3");
                nameLabel.setFont(font);
                nameLabel.setLayoutX(12);
                nameLabel.setLayoutY(10);

                var rankLabel = new Label("\uD83D\uDD30 " + item.rank() + "위");
                rankLabel.getStyleClass().add("h3");
                rankLabel.setFont(font);
                rankLabel.setLayoutX(250);
                rankLabel.setLayoutY(5);

                var flagLabel = new Label("\uD83D\uDEA9 " + item.flags());
                flagLabel.getStyleClass().add("h3");
                flagLabel.setFont(font);
                flagLabel.setLayoutX(250);
                flagLabel.setLayoutY(25);

                view.getChildren().addAll(nameLabel, rankLabel, flagLabel);

                var separatorLine = new Line();
                separatorLine.setStartX(10);
                separatorLine.setStartY(58);
                separatorLine.setEndX(310);
                separatorLine.setEndY(58);
                separatorLine.setStrokeWidth(0.3);
                separatorLine.setFill(Paint.valueOf("#E1E1E1"));
                view.getChildren().add(separatorLine);

                setGraphic(view);
            }
        };
    }
}
