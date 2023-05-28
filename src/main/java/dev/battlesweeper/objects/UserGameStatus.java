package dev.battlesweeper.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Accessors(fluent = true)
@Getter @Setter
public class UserGameStatus implements Comparable<UserGameStatus> {

    private UserInfo user;
    private int flags;
    private int rank;

    @Override
    public int compareTo(@NonNull UserGameStatus other) {
        if (other.flags < flags)
            return 1;
        else if (other.flags == flags)
            return 0;
        return -1;
    }
}
