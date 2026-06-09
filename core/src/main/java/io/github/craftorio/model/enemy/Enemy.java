package io.github.craftorio.model.enemy;

import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.core.WorldMap;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.DamageableBuilding;
import io.github.craftorio.model.building.Direction;

import java.awt.Point;
import java.util.List;

public class Enemy {

    private float x, y;
    private final float speed;
    private final int coolDown;
    private final int damage;
    private int attackTimer;
    private int hp;

    public EnemyType getType() {
        return type;
    }

    private final EnemyType type;

    private int directionChangeCounter = 0;

    public Direction getDirection() {
        return direction;
    }

    private Direction direction = Direction.LEFT;


    private PathFinder pathFinder;
    private BuildingRegistry registry;
    private WorldMap worldMap;
    private List<Enemy> allEnemies;

    private final float hitboxSize;

    public Enemy(float x, float y, EnemyType type, PathFinder pathFinder, BuildingRegistry registry, List<Enemy> allEnemies,
                 WorldMap worldMap) {
        this.x = x;
        this.y = y;
        this.speed = type.getSpeed();
        this.coolDown = type.getCoolDown();
        this.damage = type.getDamage();
        this.hp = type.getHp();
        this.hitboxSize = type.getHitbox();

        this.type = type;

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

        float[] flowDir = getInterpolatedFlowDirection();
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

            float maxDist = (this.hitboxSize + other.hitboxSize) / 2f;
            float maxDistSq = maxDist * maxDist;

            if (distSq == 0) {
                pushX += (float) (Math.random() - 0.5) * 0.1f;
                pushY += (float) (Math.random() - 0.5) * 0.1f;
                continue;
            }

            if (distSq < maxDistSq) {
                float dist = (float) Math.sqrt(distSq);
                float overlap = maxDist - dist;

                float forceX = (distX / dist) * overlap * 0.2f;
                float forceY = (distY / dist) * overlap * 0.2f;

                float jitterX = (float) (Math.random() - 0.5) * 0.02f;
                float jitterY = (float) (Math.random() - 0.5) * 0.02f;

                pushX += forceX + jitterX;
                pushY += forceY + jitterY;
            }
        }

        return new float[]{pushX, pushY};
    }

    private float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    private float[] getInterpolatedFlowDirection() {
        PathFinder.SizeClass sizeClass = (this.hitboxSize > 1.5f) ?
            PathFinder.SizeClass.HEAVY : PathFinder.SizeClass.SMALL;

        float px = this.x - 0.5f;
        float py = this.y - 0.5f;

        int gx = (int) Math.floor(px);
        int gy = (int) Math.floor(py);

        float tx = px - gx;
        float ty = py - gy;

        Point v00 = pathFinder.getFlowDirection(gx, gy, sizeClass);
        Point v10 = pathFinder.getFlowDirection(gx + 1, gy, sizeClass);
        Point v01 = pathFinder.getFlowDirection(gx, gy + 1, sizeClass);
        Point v11 = pathFinder.getFlowDirection(gx + 1, gy + 1, sizeClass);

        float bottomX = lerp(v00.x, v10.x, tx);
        float topX = lerp(v01.x, v11.x, tx);
        float finalDx = lerp(bottomX, topX, ty);

        float bottomY = lerp(v00.y, v10.y, tx);
        float topY = lerp(v01.y, v11.y, tx);
        float finalDy = lerp(bottomY, topY, ty);

        return new float[]{finalDx, finalDy};
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
        float halfHitbox = hitboxSize / 2f;

        float left = testX - halfHitbox;
        float right = testX + halfHitbox;
        float bottom = testY - halfHitbox;
        float top = testY + halfHitbox;

        int minX = (int) Math.floor(left);
        int maxX = (int) Math.floor(right);
        int minY = (int) Math.floor(bottom);
        int maxY = (int) Math.floor(top);

        for (int gx = minX; gx <= maxX; gx++) {
            for (int gy = minY; gy <= maxY; gy++) {
                if (gx + 1 <= left || gx >= right || gy + 1 <= bottom || gy >= top) {
                    continue;
                }

                if (!isTileWalkable(gx, gy)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isTileWalkable(int gx, int gy) {
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

        float half = hitboxSize / 2f;
        float attackReach = 0.35f;

        float attackLeft = (this.x - half) + (normX * attackReach);
        float attackRight = (this.x + half) + (normX * attackReach);
        float attackBottom = (this.y - half) + (normY * attackReach);
        float attackTop = (this.y + half) + (normY * attackReach);


        int minX = (int) Math.floor(attackLeft);
        int maxX = (int) Math.floor(attackRight);
        int minY = (int) Math.floor(attackBottom);
        int maxY = (int) Math.floor(attackTop);


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

    public void receiveDamage(int damage) {
        this.hp -= damage;
    }

    public boolean isDead() {
        return this.hp <= 0;
    }

    public float getHitRadius() {
        return type.getHitbox();
    }
}
