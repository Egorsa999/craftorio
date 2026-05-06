package io.github.craftorio.model;

public class World {
    // Coordinates of our square
    public float playerX = 100;
    public float playerY = 100;
    public static final float SPEED = 300; // Movement speed

    // Method to update coordinates
    public void updatePosition(float delta, float dx, float dy) {
        playerX += dx * SPEED * delta;
        playerY += dy * SPEED * delta;
    }
}
