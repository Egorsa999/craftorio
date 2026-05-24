package io.github.craftorio.model.building.defense;

import com.badlogic.gdx.math.MathUtils;
import io.github.craftorio.model.building.*;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.core.SimulationEngine;
import io.github.craftorio.model.entity.Bullet;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.model.enemy.Enemy;

import java.awt.Point;
import java.util.List;

public class Turret extends DamageableBuilding implements ReceiveItem {
    private final ItemType ammoType = ItemType.BULLET;
    private int ammoAmount = 0;

    private float rotationDeg = 0f;
    private float range = 8f;
    private int fireCooldown = 15;
    private int currentCooldown = 0;
    private int damage = 10;

    private final SimulationEngine engine;

    public Turret(BuildingRegistry registry, Point anchor, Direction direction, SimulationEngine engine) {
        super(registry, anchor, direction, BuildingType.TURRET);
        this.engine = engine;
    }

    @Override
    public void update() {
        super.update();
        if (currentCooldown > 0) currentCooldown--;

        Enemy target = findNearestEnemy();

        if (target != null) {
            aimAt(target.getX(), target.getY());

            if (currentCooldown == 0 && ammoAmount > 0) {
                float myX = getX() + 0.5f;
                float myY = getY() + 0.5f;

                Bullet bullet = new Bullet(myX, myY, target.getX(), target.getY(), 0.4f, damage);
                engine.spawnBullet(bullet);

                currentCooldown = fireCooldown;
                ammoAmount--;
            }
        }
    }

    private Enemy findNearestEnemy() {
        Enemy nearest = null;
        float minDist = Float.MAX_VALUE;
        float myX = getX() + 0.5f;
        float myY = getY() + 0.5f;

        List<Enemy> enemies = engine.getEnemies();
        for (Enemy e : enemies) {
            if (e.isDead()) continue;

            float dist = (float) Math.hypot(e.getX() - myX, e.getY() - myY);
            if (dist < minDist && dist <= range) {
                minDist = dist;
                nearest = e;
            }
        }
        return nearest;
    }

    private float getDistanceTo(Enemy e) {
        float myX = getX() + 0.5f;
        float myY = getY() + 0.5f;
        return (float) Math.hypot(e.getX() - myX, e.getY() - myY);
    }

    private void aimAt(float targetX, float targetY) {
        float myX = getX() + 0.5f;
        float myY = getY() + 0.5f;
        float dx = targetX - myX;
        float dy = targetY - myY;

        float angleRad = (float) Math.atan2(dy, dx);
        this.rotationDeg = angleRad * MathUtils.radiansToDegrees - 90f;
    }

    public float getRotationDeg() { return rotationDeg; }

    @Override
    public boolean receiveItem(Building building, ItemType type) {
        if (type != ammoType) return false;
        ammoAmount++;
        return true;
    }

    @Override
    public boolean canReceiveFrom(Building building, Point point) {
        return true;
    }
}
