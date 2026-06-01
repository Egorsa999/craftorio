package io.github.craftorio.model.building.defense;

import com.badlogic.gdx.math.MathUtils;
import io.github.craftorio.BalanceConfig;
import io.github.craftorio.model.building.*;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.core.SimulationEngine;
import io.github.craftorio.model.entity.Bullet;
import io.github.craftorio.model.entity.BulletType;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.model.enemy.Enemy;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Turret extends DamageableBuilding implements ReceiveItem {

    private static final List<BulletType> acceptedAmmo = List.of(
        BulletType.COPPER,
        BulletType.STANDARD,
        BulletType.PIERCING
    );

    private final Queue<BulletType> ammoBuffer = new LinkedList<>();

    private float rotationDeg = 0f;
    private float range = BalanceConfig.TURRET_RANGE;
    private int fireCooldown = BalanceConfig.TURRET_FIRE_COOLDOWN;
    private int currentCooldown = 0;

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

            if (currentCooldown == 0 && !ammoBuffer.isEmpty()) {
                float myX = getX() + 0.5f;
                float myY = getY() + 0.5f;

                BulletType ammoToFire = ammoBuffer.poll();
                Bullet bullet = ammoToFire.create(myX, myY, target.getX(), target.getY(), range);

                engine.spawnBullet(bullet);
                currentCooldown = fireCooldown;
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
        BulletType incomingBulletType = BulletType.fromItemType(type);

        if (incomingBulletType != null
            && acceptedAmmo.contains(incomingBulletType)
            && ammoBuffer.size() < BalanceConfig.TURRET_AMMO_CAPACITY) {

            ammoBuffer.add(incomingBulletType);
            return true;
        }
        return false;
    }

    @Override
    public boolean canReceiveItemFrom(Building building, Point point) {
        return true;
    }

    static public List<BulletType> getAcceptedAmmo() {
        return acceptedAmmo;
    }
}
