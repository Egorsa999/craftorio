package io.github.craftorio.model;


import com.badlogic.gdx.math.MathUtils;
import io.github.craftorio.GameConfig;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.generator.TerrainType;

import java.awt.*;

public class Player {

    WorldMap worldMap;
    BuildingRegistry registry;

    public float playerX;
    public float playerY;

    private boolean isMoving = false;
    private Direction direction = Direction.DOWN;


    public static final float speed = GameConfig.PLAYER_SPEED;

    Point getLocation() {
        return new Point((int)playerX, (int)playerY);
    }
    private boolean isWalkable(int x, int y){
        TerrainType terrainType = worldMap.getCell(x, y).getTerrainType();
        if(terrainType != null && !terrainType.getWalkability()) return false;

        Building current = registry.getBuildingAt(x, y);
        if(current != null && !current.getWalkable()) return false;

        return true;
    }
    public Player(WorldMap worldMap, BuildingRegistry registry, Point spawnPoint){
        this.worldMap = worldMap;
        this.registry = registry;
        this.playerX = spawnPoint.x;
        this.playerY = spawnPoint.y;
    }
    public void updatePosition(float delta, float dx, float dy){
        isMoving = true;
        float newPlayerX = playerX + (float) (dx * speed * delta / (!(dx == 0 || dy == 0) ? Math.sqrt(2) : 1));
        float newPlayerY = playerY + (float) (dy * speed * delta / (!(dx == 0 || dy == 0) ? Math.sqrt(2) : 1));

        if (isWalkable((int)playerX, (int)newPlayerY)) playerY = newPlayerY;
        if (isWalkable((int)newPlayerX, (int)playerY)) playerX = newPlayerX;
        //System.out.println(playerX + " " + playerY);


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
