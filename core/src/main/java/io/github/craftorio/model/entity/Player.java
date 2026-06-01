package io.github.craftorio.model.entity;


import com.badlogic.gdx.math.MathUtils;
import io.github.craftorio.GameConfig;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.core.WorldMap;
import io.github.craftorio.model.generator.ResourceType;
import io.github.craftorio.model.generator.TerrainType;
import io.github.craftorio.ui.Inventory;

import java.awt.*;

public class Player {

    WorldMap worldMap;
    BuildingRegistry registry;
    Inventory inventory;

    public float playerX;
    public float playerY;

    private boolean isMoving = false;
    private Direction direction = Direction.DOWN;

    private int digTimer = 0;
    private boolean isDigging = false;


    public static final float speed = GameConfig.PLAYER_SPEED;

    private static final float HITBOX_RADIUS = 0.35f;


    public Point getLocation() {
        return new Point((int)playerX, (int)playerY);
    }



    private boolean isWalkable(int x, int y){
        if (x < 0 || y < 0 || x >= worldMap.getWidth() || y >= worldMap.getHeight()) return false;

        TerrainType terrainType = worldMap.getCell(x, y).getTerrainType();
        if(terrainType != null && !terrainType.getWalkability()) return false;

        Building current = registry.getBuildingAt(x, y);
        if(current != null && !current.getWalkable()) return false;

        return true;
    }
    private boolean canMoveTo(float nextX, float nextY) {
        int left = MathUtils.floor(nextX - HITBOX_RADIUS);
        int right = MathUtils.floor(nextX + HITBOX_RADIUS);
        int bottom = MathUtils.floor(nextY - HITBOX_RADIUS);
        int top = MathUtils.floor(nextY + HITBOX_RADIUS);

        for (int x = left; x <= right; x++) {
            for (int y = bottom; y <= top; y++) {
                if (!isWalkable(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Player(WorldMap worldMap, BuildingRegistry registry, Point spawnPoint, Inventory inventory){
        this.worldMap = worldMap;
        this.registry = registry;
        this.playerX = spawnPoint.x;
        this.playerY = spawnPoint.y;

        this.inventory = inventory;
    }
    public void updatePosition(float delta, float dx, float dy){
        isMoving = true;

        float newPlayerX = playerX + (float) (dx * speed * delta / (!(dx == 0 || dy == 0) ? Math.sqrt(2) : 1));
        float newPlayerY = playerY + (float) (dy * speed * delta / (!(dx == 0 || dy == 0) ? Math.sqrt(2) : 1));

        if (canMoveTo(playerX, newPlayerY)) {
            playerY = newPlayerY;
        }
        if (canMoveTo(newPlayerX, playerY)) {
            playerX = newPlayerX;
        }

        if (dx > 0) {
            direction = Direction.RIGHT;
        } else if (dx < 0) {
            direction = Direction.LEFT;
        } else if (dy > 0) {
            direction = Direction.UP;
        } else if (dy < 0) {
            direction = Direction.DOWN;
        }

        playerX = MathUtils.clamp(playerX, HITBOX_RADIUS, worldMap.getWidth() - HITBOX_RADIUS);
        playerY = MathUtils.clamp(playerY, HITBOX_RADIUS, worldMap.getHeight() - HITBOX_RADIUS);
    }


    public void tryDig() {
        if (isMoving) {
            stopDigging();
            return;
        }

        int gridX = (int) playerX;
        int gridY = (int) playerY;

        ResourceType resource = worldMap.getCell(gridX, gridY).getResourceType();

        if (resource != null && resource != ResourceType.NONE) {
            digTimer++;
            isDigging = true;

            if (digTimer >= 60) {
                digTimer = 0;
                inventory.add(resource.getDrop(), 1);
            }
        } else {
            stopDigging();
        }
        System.out.println(digTimer);
    }

    public void stopDigging() {
        isDigging = false;
        digTimer = 0;
    }

    public void stop(){
        isMoving = false;
    }

    public boolean isMoving(){
        return isMoving;
    }

    public boolean isDigging() {
        return isDigging;
    }

    public int getDigTimer() {
        return digTimer;
    }

    public Direction getDirection(){
        return direction;
    }
}
