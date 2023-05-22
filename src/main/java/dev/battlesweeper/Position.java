package dev.battlesweeper;

public record Position(int x, int y) {

    boolean isNearby(Position other, int radius) {
        return Math.abs(other.x - x) <= radius && Math.abs(other.y - y) <= radius;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Position pos))
            return false;

        return pos.x == x() && pos.y == y();
    }
}
