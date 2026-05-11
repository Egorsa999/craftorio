package io.github.craftorio.model;


import com.badlogic.gdx.math.MathUtils;
import io.github.craftorio.GameConfig;
import io.github.craftorio.model.building.Direction;

public class Player {

    WorldMap worldMap;

    public float playerX;
    public float playerY;

    private boolean isMoving = false;
    public Direction direction = Direction.DOWN;

    public static final float speed = GameConfig.PLAYER_SPEED;

    public Player(WorldMap worldMap){
        this.worldMap = worldMap;
        playerX = worldMap.getWidth() / 2f;
        playerY = worldMap.getHeight() / 2f;
    }

    public void updatePosition(float delta, float dx, float dy){
        isMoving = true;
        playerX += (float) (dx * speed * delta / (!(dx == 0 || dy == 0) ? Math.sqrt(2) : 1));
        playerY += (float) (dy * speed * delta / (!(dx == 0 || dy == 0) ? Math.sqrt(2) : 1));



        if (dx > 0) {
            direction = Direction.RIGHT;
        } else if (dx < 0) {
            direction = Direction.LEFT;
        } else if (dy > 0) {
            direction = Direction.UP;
        } else if (dy < 0) {
            direction = Direction.DOWN;
        }

        playerX = MathUtils.clamp(playerX, 0.5f, worldMap.getWidth() - 0.5f);
        playerY = MathUtils.clamp(playerY, 0.5f, worldMap.getHeight() - 0.5f);
        //System.out.println(playerX + " " + playerY);
    }

    public void stop(){
        isMoving = false;
    }

    public boolean isMoving(){
        return isMoving;
    }

    public Direction getDirection(){
        return direction;
    }
}
