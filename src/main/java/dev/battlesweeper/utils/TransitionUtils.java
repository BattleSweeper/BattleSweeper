package dev.battlesweeper.utils;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

public class TransitionUtils {
    private TransitionUtils() {}

    public static void fadeIn(Node node, Duration duration) {
        fadeIn(node, duration, null);
    }

    public static void fadeIn(Node node, Duration duration, EventHandler<ActionEvent> onFinished) {
        var fade = new FadeTransition();
        fade.setFromValue(0);
        fade.setToValue(10);
        fade.setNode(node);
        fade.setDuration(duration);
        fade.setOnFinished(onFinished);
        fade.play();
    }

    public static void fadeOut(Node node, Duration duration) {
        fadeOut(node, duration, null);
    }

    public static void fadeOut(Node node, Duration duration, EventHandler<ActionEvent> onFinished) {
        var fade = new FadeTransition();
        fade.setFromValue(10);
        fade.setToValue(0);
        fade.setNode(node);
        fade.setDuration(duration);
        fade.setOnFinished(onFinished);
        fade.play();
    }

    public static void crossFade(Node out, Node in, Duration duration) {
        crossFade(out, in, duration, null, null);
    }

    public static void crossFade(Node out, Node in, Duration duration, EventHandler<ActionEvent> afterOut, EventHandler<ActionEvent> onFinished) {
        fadeOut(out, duration, v -> {
            afterOut.handle(v);
            fadeIn(in, duration, onFinished);
        });
    }
}
