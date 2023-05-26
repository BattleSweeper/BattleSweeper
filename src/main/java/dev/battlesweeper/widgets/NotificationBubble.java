package dev.battlesweeper.widgets;

import dev.battlesweeper.Session;
import dev.battlesweeper.utils.FontUtils;
import dev.battlesweeper.utils.ResourceUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.Notifications;

@Slf4j
public class NotificationBubble {

    private static final int WINDOW_MARGIN = 24;
    private static final int WIDTH  = 100;
    private static final int HEIGHT = 50;

    private static final int TYPE_PRIMARY = 0;
    private static final int TYPE_WARN    = 1;
    private static final int TYPE_ERROR   = 2;

    private String title;
    private String message;

    public NotificationBubble title(String title) {
        this.title = title;
        return this;
    }

    public NotificationBubble message(String message) {
        this.message = message;
        return this;
    }

    private Popup create(int type) {
        var font       = FontUtils.loadFontFromResource("NanumSquareNeo-cBd.ttf", 16);
        var stylesheet = ResourceUtils.getResource("styles/bubble.css").toExternalForm();

        final var popup = new Popup();
        //popup.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_BOTTOM_LEFT);
        popup.setAutoFix(false);
        popup.setAutoHide(false);
        popup.setHideOnEscape(false);

        var vbox = new VBox();
        vbox.getStylesheets().add(stylesheet);
        vbox.getStyleClass().addAll("vbox", getStyleClassByType(type));
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setSpacing(4);

        var titleLabel = new Label(title);
        titleLabel.getStyleClass().addAll("title", "label");
        titleLabel.setFont(font);
        vbox.getChildren().add(titleLabel);

        var messageLabel = new Label(message);
        messageLabel.getStyleClass().add("label");
        messageLabel.setFont(font);
        vbox.getChildren().add(messageLabel);

        var pane = new NotificationPane();
        pane.setContent(vbox);
        pane.show();

        popup.getContent().add(vbox);

        return popup;
    }

    private void show(int type) {
        var stage = Session.getInstance().rootStage;
        var popup = create(type);
        popup.setOnShown(event -> {
            popup.setX(stage.getX() + stage.getWidth() - (popup.getWidth() + WINDOW_MARGIN));
            popup.setY(stage.getY() + stage.getHeight() - (popup.getHeight() + WINDOW_MARGIN));
        });
        //popup.show(stage);
    }

    public void showPrimary() {
        show(TYPE_PRIMARY);
    }

    public void showWarn() {
        show(TYPE_WARN);
    }

    public void showError() {
        show(TYPE_ERROR);
    }

    private String getStyleClassByType(int type) {
        switch (type) {
            case TYPE_PRIMARY -> {
                return "primary";
            }
            case TYPE_WARN -> {
                return "warn";
            }
            case TYPE_ERROR -> {
                return "error";
            }
        }
        return "primary";
    }
}
