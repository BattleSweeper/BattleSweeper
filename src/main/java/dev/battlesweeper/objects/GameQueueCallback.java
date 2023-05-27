package dev.battlesweeper.objects;

import java.util.UUID;

public interface GameQueueCallback {

    public void onFound(UUID roomId);

    public void onFailure();
}
