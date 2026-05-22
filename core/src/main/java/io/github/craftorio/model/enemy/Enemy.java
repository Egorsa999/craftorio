package io.github.craftorio.model.enemy;

import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.WorldMap;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.DamageableBuilding;
import io.github.craftorio.model.building.Direction;

import java.awt.Point;
import java.util.List;

public class Enemy {

    private float x, y;
    private float speed;
    private int coolDown = 60;
    private int damage = 30;
    private int attackTimer = 0;

    private int directionChangeCounter = 0;

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

        float[] flowDir = getAverageFlowDirection();
        float fdx = flowDir[0];
        float fdy = flowDir[1];

        float moveX = 0;
        float moveY = 0;
        float flowLen = (float) Math.sqrt(fdx * fdx + fdy * fdy);
        if (flowLen > 0) {
            moveX = (fdx / flowLen) * speed;
            moveY = (fdy / flowLen) * speed;
        }

        float[] sep = getSeparationVector();

        float finalX = moveX + sep[0];
        float finalY = moveY + sep[1];


        if (moveX > 0 && directionChangeCounter >= 30){
            direction = Direction.RIGHT;
            directionChangeCounter = 0;
        }
        else if (moveX < 0 && directionChangeCounter >= 30) {
            direction = Direction.LEFT;
            directionChangeCounter = 0;
        }

        directionChangeCounter++;

        if (finalX == 0 && finalY == 0) {
            return;
        }

        boolean moved = tryMove(finalX, finalY);

        if (!moved) {
            attackObstacle(finalX, finalY);

            boolean movedX = tryMove(finalX, 0);

            if (!movedX) {
                tryMove(0, finalY);
            }
        }
    }


    private float[] getSeparationVector() {
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

                pushX += (distX / dist) * overlap * 0.2f;
                pushY += (distY / dist) * overlap * 0.2f;
            }
        }

        return new float[]{pushX, pushY};
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
        if (dirX == 0 && dirY == 0) return;


        float len = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        float normX = dirX / len;
        float normY = dirY / len;


        float attackReach = 0.35f;
        float attackCenterX = this.x + (normX * attackReach);
        float attackCenterY = this.y + (normY * attackReach);


        float half = HITBOX_SIZE / 2f;

        int minX = (int) Math.floor(attackCenterX - half);
        int maxX = (int) Math.floor(attackCenterX + half);
        int minY = (int) Math.floor(attackCenterY - half);
        int maxY = (int) Math.floor(attackCenterY + half);


        for (int gx = minX; gx <= maxX; gx++) {
            for (int gy = minY; gy <= maxY; gy++) {

                if (gx >= 0 && gx < worldMap.getWidth() && gy >= 0 && gy < worldMap.getHeight()) {
                    Building targetBuilding = registry.getBuildingAt(gx, gy);


                    if (targetBuilding instanceof DamageableBuilding building) {
                        building.receiveDamage(damage);
                        attackTimer = coolDown;
                        return;
                    }
                }
            }
        }
    }
}
