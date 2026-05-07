package io.github.craftorio.model;


import com.badlogic.gdx.math.MathUtils;

public class Player {

    WorldMap worldMap;

    public float playerX;
    public float playerY;

    public static final float speed = 8;

    public Player(WorldMap worldMap){
        this.worldMap = worldMap;
        playerX = worldMap.getWidth() / 2f;
        playerY = worldMap.getHeight() / 2f;
    }

    public void updatePosition(float delta, float dx, float dy){
        playerX += dx * speed * delta;
        playerY += dy * speed * delta;

        playerX = MathUtils.clamp(playerX, 0.5f, worldMap.getWidth() - 0.5f);
        playerY = MathUtils.clamp(playerY, 0.5f, worldMap.getHeight() - 0.5f);
    }
}
