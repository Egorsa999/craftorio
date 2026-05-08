package io.github.craftorio.model.building;

public class Belt extends Building {
    public Direction direction;

    private final float speed = 1.0f;
    private float animationOffset = 0.0f;

    public Belt(int row, int col, Direction direction) {
        super(row, col, 1, 1);
        this.direction = direction;
    }

    @Override
    public void update(float delta) {
        animationOffset += speed * delta;
        if (animationOffset >= 1.0f) {
            animationOffset -= 1.0f;
        }
    }

    public float getAnimationOffset() {
        return this.animationOffset;
    }
}
