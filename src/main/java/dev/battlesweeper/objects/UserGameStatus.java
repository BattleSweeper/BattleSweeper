package dev.battlesweeper.objects;

import lombok.NonNull;

public record UserGameStatus(
        UserInfo user, int flags, int rank
) implements Comparable<UserGameStatus> {

    @Override
    public int compareTo(@NonNull UserGameStatus other) {
        if (other.flags < flags)
            return 1;
        else if (other.flags == flags())
            return 0;
        return -1;
    }
}
