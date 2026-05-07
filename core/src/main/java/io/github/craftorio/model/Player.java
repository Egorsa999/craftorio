package io.github.craftorio.model;

public class Player {
    public float playerX = 0;
    public float playerY = 0;

    public static final float speed = 8;

    public void updatePosition(float delta, float dx, float dy){
        playerX += dx * speed * delta;
        playerY += dy * speed * delta;
    }
}
