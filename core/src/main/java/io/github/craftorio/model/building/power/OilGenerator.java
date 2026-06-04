package io.github.craftorio.model.building.power;

import io.github.craftorio.BalanceConfig;
import io.github.craftorio.GameConfig;
import io.github.craftorio.model.building.*;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.LiquidType;

import java.awt.Point;

public class OilGenerator extends DamageableBuilding implements PowerProducer, ReceiveLiquid, PowerConnectable {

    private final PowerNode powerNode;

    private final float maxPowerPerTick = BalanceConfig.OIL_GENERATOR_POWER_PRODUCTION * GameConfig.TICK_TIME;
    private final float oilConsumptionPerTick = BalanceConfig.OIL_GENERATOR_OIL_PER_SECOND * GameConfig.TICK_TIME;

    private float oilInTank = 0f;
    private final float maxOilCapacity = 50f;

    private float currentLoadRatio = 0f;

    public OilGenerator(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.OIL_GENERATOR);
        this.powerNode = new PowerNode(this, registry);
    }

    @Override
    public void update() {
        super.update();

        if (oilInTank > 0 && currentLoadRatio > 0) {
            oilInTank -= (oilConsumptionPerTick * currentLoadRatio);

            if (oilInTank < 0.0001f) {
                oilInTank = 0f;
            }
        }
    }

    @Override
    public PowerNode getPowerNode() {
        return powerNode;
    }

    @Override
    public float receiveLiquid(Building from, LiquidType type, float amount) {
        if (type == LiquidType.OIL) {
            float availableSpace = maxOilCapacity - oilInTank;
            if (availableSpace <= 0f) return 0f;

            float accepted = Math.min(amount, availableSpace);
            oilInTank += accepted;
            return accepted;
        }
        return 0f;
    }

    @Override
    public boolean canReceiveLiquidFrom(Building from, Point point, LiquidType type) {
        return type == LiquidType.OIL && oilInTank < maxOilCapacity;
    }

    @Override
    public float getPotentialOutput() {
        if (oilInTank > 0) {
            return maxPowerPerTick;
        }
        return 0f;
    }

    @Override
    public void setLoadRatio(float ratio) {
        this.currentLoadRatio = Math.max(0f, Math.min(ratio, 1.0f));
    }
}
