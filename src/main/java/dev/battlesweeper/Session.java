package dev.battlesweeper;

import dev.battlesweeper.network.body.TokenInfo;
import javafx.stage.Stage;

public class Session {

    public Stage rootStage;
    public String userName;
    public TokenInfo tokenInfo;

    private Session() {}
    private static Session INSTANCE;
    public static Session getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Session();

        return INSTANCE;
    }
}
