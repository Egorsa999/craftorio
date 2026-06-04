package io.github.craftorio.model.building.power;

import io.github.craftorio.BalanceConfig;
import io.github.craftorio.GameConfig;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.DamageableBuilding;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.core.BuildingRegistry;

import java.awt.*;

public class Accumulator extends DamageableBuilding implements Battery, PowerConnectable {

    private final PowerNode powerNode;

    private float energyStored = 0f;
    private final float capacity = BalanceConfig.ACCUMULATOR_CAPACITY;

    private final float maxChargePerTick = BalanceConfig.ACCUMULATOR_MAX_CHARGE_RATE * GameConfig.TICK_TIME;
    private final float maxDischargePerTick = BalanceConfig.ACCUMULATOR_MAX_DISCHARGE_RATE * GameConfig.TICK_TIME;

    private boolean isChargingThisTick = false;
    private boolean isDischargingThisTick = false;

    public Accumulator(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.ACCUMULATOR);
        this.powerNode = new PowerNode(this, registry);
    }

    @Override
    public void update() {
        super.update();

        isChargingThisTick = false;
        isDischargingThisTick = false;
    }

    @Override
    public float getEnergyStored() {
        return energyStored;
    }

    @Override
    public float getCapacity() {
        return capacity;
    }

    @Override
    public float getMaxChargeRate() {
        return maxChargePerTick;
    }

    @Override
    public float getMaxDischargeRate() {
        return maxDischargePerTick;
    }

    @Override
    public float charge(float amount) {
        if (amount <= 0) return 0f;

        float spaceLeft = capacity - energyStored;

        float actualCharge = Math.min(amount, spaceLeft);

        if (actualCharge > 0) {
            energyStored += actualCharge;
            isChargingThisTick = true;
        }

        return actualCharge;
    }

    @Override
    public float discharge(float requestedAmount) {
        if (requestedAmount <= 0) return 0f;

        float actualDischarge = Math.min(requestedAmount, energyStored);

        if (actualDischarge > 0) {
            energyStored -= actualDischarge;
            isDischargingThisTick = true;
        }

        return actualDischarge;
    }

    @Override
    public PowerNode getPowerNode() {
        return powerNode;
    }

    public float getChargeRatio() {
        return energyStored / capacity;
    }

    public boolean isChargingThisTick() {
        return isChargingThisTick;
    }

    public boolean isDischargingThisTick() {
        return isDischargingThisTick;
    }
}
