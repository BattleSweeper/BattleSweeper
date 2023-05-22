package dev.battlesweeper;

import java.util.HashSet;
import java.util.Set;

public class BoardGenerator {
    private BoardGenerator() {}

    public static Position[] generateMines(Position boardSize, int mines) {
        Set<Position> positions = new HashSet<>();

        for (var i = 0; i < mines; ++i) {
            Position nPos;
            do {
                nPos = getRandomPosition(boardSize.x(), boardSize.y());
            } while (positions.contains(nPos) && countNearbyMines(positions, nPos) > 4);
            positions.add(nPos);
        }

        return positions.toArray(new Position[0]);
    }

    private static int countNearbyMines(Set<Position> mines, Position mine) {
        int c = 0;
        for (var pos : mines) {
            if (pos.isNearby(mine, 1))
                c++;
        }
        return c;
    }

    private static Position getRandomPosition(int limX, int limY) {
        return new Position(randomIn(limX), randomIn(limY));
    }

    private static int randomIn(int max) {
        return (int) Math.floor(Math.random() * max);
    }
}