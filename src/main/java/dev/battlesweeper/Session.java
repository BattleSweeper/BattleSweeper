package dev.battlesweeper;

import dev.battlesweeper.network.body.TokenInfo;
import javafx.stage.Stage;

import java.util.UUID;

public class Session {

    public Stage rootStage;
    public String userName;
    public TokenInfo tokenInfo;
    public UUID roomId;

    public String email;
    public String code;

    private Session() {}
    private static Session INSTANCE;
    public static Session getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Session();

        return INSTANCE;
    }
}
