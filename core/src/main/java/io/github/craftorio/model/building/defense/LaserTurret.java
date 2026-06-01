package io.github.craftorio.model.building.defense;

import com.badlogic.gdx.math.MathUtils;
import io.github.craftorio.BalanceConfig;
import io.github.craftorio.GameConfig;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.DamageableBuilding;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.building.power.PowerConnectable;
import io.github.craftorio.model.building.power.PowerConsumer;
import io.github.craftorio.model.building.power.PowerNode;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.core.SimulationEngine;
import io.github.craftorio.model.enemy.Enemy;

import java.awt.Point;
import java.util.List;

public class LaserTurret extends DamageableBuilding implements PowerConsumer, PowerConnectable {
    private final SimulationEngine engine;
    private final PowerNode powerNode;

    private float rotationDeg = 0f;
    private Enemy currentTarget = null;
    private boolean isFiring = false;
    private float satisfactionRatio = 0f;

    private float damageAccumulator = 0f;

    private final float range = BalanceConfig.LASER_TURRET_RANGE;
    private final float maxDamagePerTick = BalanceConfig.LASER_TURRET_DAMAGE * GameConfig.TICK_TIME;
    private final float maxPowerPerTick = BalanceConfig.LASER_TURRET_POWER_CONSUMPTION * GameConfig.TICK_TIME;

    public LaserTurret(BuildingRegistry registry, Point anchor, Direction direction, SimulationEngine engine) {
        super(registry, anchor, direction, BuildingType.LASER_TURRET);
        this.engine = engine;
        this.powerNode = new PowerNode(this, registry);
    }

    @Override
    public void update() {
        super.update();

        currentTarget = findNearestEnemy();

        if (currentTarget != null) {
            isFiring = true;
            aimAt(currentTarget.getX(), currentTarget.getY());

            float actualDamage = maxDamagePerTick * satisfactionRatio;
            damageAccumulator += actualDamage;

            if (damageAccumulator >= 1f) {
                int damageToDeal = (int) damageAccumulator;
                currentTarget.receiveDamage(damageToDeal);
                damageAccumulator -= damageToDeal;
            }
        } else {
            isFiring = false;
            damageAccumulator = 0f;
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
    public boolean isFiring() { return isFiring; }
    public Enemy getCurrentTarget() { return currentTarget; }
    public float getSatisfactionRatio() { return satisfactionRatio; }
    public float getRange() { return range; }

    @Override
    public PowerNode getPowerNode() {
        return this.powerNode;
    }

    @Override
    public float getRequiredPower() {
        return isFiring ? maxPowerPerTick : 0f;
    }

    @Override
    public void setSatisfactionRatio(float ratio) {
        this.satisfactionRatio = ratio;
    }
}
