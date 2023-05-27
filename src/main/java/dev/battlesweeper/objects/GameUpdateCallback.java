package dev.battlesweeper.objects;

import java.util.List;

public interface GameUpdateCallback {

    void onGameStart(List<UserInfo> users, Position boardSize, Position[] mines);

    void onTileUpdate(UserInfo user, Position position, int action, int bombLeft);

    void onGameUpdate(UserInfo user, int state);

    void onError();
}
