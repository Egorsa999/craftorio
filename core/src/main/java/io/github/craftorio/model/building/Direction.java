package io.github.craftorio.model.building;

public enum Direction {
    UP, RIGHT, DOWN, LEFT;

    public Direction next() {
        Direction[] values = Direction.values();
        int nextIndex = (this.ordinal() + 1) % values.length;
        return values[nextIndex];
    }
}
