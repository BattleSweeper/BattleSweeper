package dev.battlesweeper.utils;

import java.util.Timer;
import java.util.TimerTask;

public class TimerUtils {
    private TimerUtils() {}

    public static void setTimer(long delay, Runnable callback) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                callback.run();
            }
        }, delay);
    }
}
