package io.github.craftorio.model.building;

public class Belt extends Building {
    public Direction direction;

    private static final float speed = 1.0f;
    private static float animationOffset = 0.0f;

    public Belt(int row, int col, Direction direction) {
        super(row, col, 1, 1);
        this.direction = direction;
    }

    public static void updateAnimationOffset(float delta) {
        animationOffset += speed * delta;
        animationOffset %= 1;
    }

    @Override
    public void update(float delta) {
        // TODO update item progression
    }

    public static float getAnimationOffset() {
        return animationOffset;
    }
}
