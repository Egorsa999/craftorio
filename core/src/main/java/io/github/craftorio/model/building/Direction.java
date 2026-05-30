package io.github.craftorio.model.building;

public enum Direction {
    UP, RIGHT, DOWN, LEFT;

    public Direction next() {
        Direction[] values = Direction.values();
        int nextIndex = (this.ordinal() + 1) % values.length;
        return values[nextIndex];
    }

    public float to_degrees() {
        return switch (this) {
            case UP -> 0f;
            case RIGHT -> -90f;
            case DOWN -> -180f;
            case LEFT -> -270f;
        };
    }

    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}
