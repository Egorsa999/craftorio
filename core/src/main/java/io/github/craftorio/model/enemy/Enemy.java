package io.github.craftorio.model.enemy;

import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.WorldMap;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.Direction;

import java.awt.Point;
import java.util.List;

public class Enemy {

    private float x, y;
    private float speed;
    private int coolDown = 60;
    private int attackTimer = 0;

    public Direction getDirection() {
        return direction;
    }

    private Direction direction = Direction.LEFT;

    private static final float HITBOX_SIZE = 0.7f;

    private PathFinder pathFinder;
    private BuildingRegistry registry;
    private WorldMap worldMap;
    private List<Enemy> allEnemies;

    public Enemy(float x, float y, float speed, PathFinder pathFinder, BuildingRegistry registry, List<Enemy> allEnemies,
                 WorldMap worldMap) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.pathFinder = pathFinder;
        this.registry = registry;
        this.allEnemies = allEnemies;
        this.worldMap = worldMap;
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public void update() {
        if (attackTimer > 0) {
            attackTimer--;
        }

        applySeparation();

        float[] dir = getAverageFlowDirection();
        float dx = dir[0];
        float dy = dir[1];

        if (dx > 0)direction = Direction.RIGHT;
        else direction = Direction.LEFT;

        if (dx == 0 && dy == 0) {
            return;
        }

        float length = (float) Math.sqrt(dx * dx + dy * dy);
        float moveX = (dx / length) * speed;
        float moveY = (dy / length) * speed;

        boolean moved = tryMove(moveX, moveY);

        if (!moved) {
            boolean movedX = tryMove(moveX, 0);
            boolean movedY = tryMove(0, moveY);

            if (movedX){
               attackObstacle(0, dy);
            }
            else if (movedY){
                attackObstacle(dx, 0);
            }
            else attackObstacle(dx, dy);
        }
    }

    private float[] getAverageFlowDirection() {
        float half = HITBOX_SIZE / 2f;

        float[][] points = {
            {x, y},
            {x - half, y - half},
            {x + half, y - half},
            {x - half, y + half},
            {x + half, y + half}
        };

        float sumX = 0;
        float sumY = 0;

        for (float[] p : points) {
            int gx = (int) p[0];
            int gy = (int) p[1];
            Point pointDir = pathFinder.getFlowDirection(gx, gy);

            sumX += pointDir.x;
            sumY += pointDir.y;
        }

        return new float[]{sumX, sumY};
    }

    private void applySeparation() {
        float pushX = 0;
        float pushY = 0;

        for (Enemy other : allEnemies) {
            if (other == this) continue;

            float distX = this.x - other.x;
            float distY = this.y - other.y;
            float distSq = distX * distX + distY * distY;

            if (distSq > 0 && distSq < 1.0f) {
                float dist = (float) Math.sqrt(distSq);
                float overlap = 1.0f - dist;

                pushX += (distX / dist) * overlap * 0.1f;
                pushY += (distY / dist) * overlap * 0.1f;
            }
        }

        tryMove(pushX, pushY);
    }

    private boolean tryMove(float mx, float my) {
        if (mx == 0 && my == 0) return false;

        float nextX = this.x + mx;
        float nextY = this.y + my;

        if (isAreaFree(nextX, nextY)) {
            this.x = nextX;
            this.y = nextY;
            return true;
        }
        return false;
    }

    private boolean isAreaFree(float testX, float testY) {
        float halfHitbox = HITBOX_SIZE / 2f;

        float left = testX - halfHitbox;
        float right = testX + halfHitbox;
        float bottom = testY - halfHitbox;
        float top = testY + halfHitbox;

        return isWalkable(left, bottom) &&
            isWalkable(right, bottom) &&
            isWalkable(left, top) &&
            isWalkable(right, top);
    }

    private boolean isWalkable(float checkX, float checkY) {
        int gx = (int) checkX;
        int gy = (int) checkY;

        if (gx < 0 || gx >= worldMap.getWidth() || gy < 0 || gy >= worldMap.getHeight()) {
            return false;
        }

        if (registry.getBuildingAt(gx, gy) != null) {
            return false;
        }
        return worldMap.getCell(gx, gy).getTerrainType().getWalkability();
    }

    private void attackObstacle(float dirX, float dirY) {
        if (attackTimer > 0) return;

        int targetX = (int) (this.x + Math.signum(dirX));
        int targetY = (int) (this.y + Math.signum(dirY));

        Building targetBuilding = registry.getBuildingAt(targetX, targetY);

        if (targetBuilding != null) {
            System.out.println("Enemy attacks building at: " + targetX + ", " + targetY + " " + targetBuilding.type);
            attackTimer = coolDown;
        }
    }
}
