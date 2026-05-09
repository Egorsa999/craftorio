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
            case UP -> 90f;
            case RIGHT -> 0f;
            case DOWN -> 270f;
            case LEFT -> 180f;
        };
    }
}
