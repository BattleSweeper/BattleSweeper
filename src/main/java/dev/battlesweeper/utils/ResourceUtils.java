package dev.battlesweeper.utils;

import dev.battlesweeper.App;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.Objects;

public class ResourceUtils {
    private ResourceUtils() {}

    private static final String IMG_PATH = "images/";

    public static URL getResource(String name) {
        return App.class.getResource(name);
    }

    public static String getIconPath(String res) {
        return Objects.requireNonNull(App.class.getResource(IMG_PATH + res)).toExternalForm();
    }

    public static Image getIconImage(String res) {
        return new Image(getIconPath(res));
    }
}
