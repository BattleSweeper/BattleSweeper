package dev.battlesweeper.utils;

import dev.battlesweeper.App;
import javafx.scene.text.Font;

import java.util.Objects;

public class FontUtils {
    private FontUtils() {}

    public static Font loadFontFromResource(String name, int size) {
        var path = Objects.requireNonNull(App.class.getResource("fonts/" + name)).toExternalForm();
        return Font.loadFont(path, size);
    }
}
