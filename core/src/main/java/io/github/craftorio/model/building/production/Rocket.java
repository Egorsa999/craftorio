package io.github.craftorio.model.building.production;

import io.github.craftorio.model.building.*;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.model.item.LiquidType;

import java.awt.Point;

public class Rocket extends DamageableBuilding implements ReceiveItem, ReceiveLiquid {

    public static final int REQUIRED_MICROCHIPS = 100;
    public static final int REQUIRED_STEEL = 200;
    public static final float REQUIRED_FUEL = 500f;

    private int currentMicrochips = 0;
    private int currentSteel = 0;
    private float currentFuel = 0f;

    private boolean hasLaunched = false;

    public Rocket(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.ROCKET);
    }

    @Override
    public boolean receiveItem(Building from, ItemType type) {
        if (type == ItemType.CHIP && currentMicrochips < REQUIRED_MICROCHIPS) {
            currentMicrochips++;
            return true;
        }
        if (type == ItemType.STEEL && currentSteel < REQUIRED_STEEL) {
            currentSteel++;
            return true;
        }
        return false;
    }

    @Override
    public boolean canReceiveItemFrom(Building from, Point point) {
        return true;
    }

    @Override
    public float receiveLiquid(Building from, LiquidType type, float amount) {
        if (type == LiquidType.ROCKET_FUEL && currentFuel < REQUIRED_FUEL) {
            float availableSpace = REQUIRED_FUEL - currentFuel;
            float accepted = Math.min(amount, availableSpace);
            currentFuel += accepted;
            return accepted;
        }
        return 0f;
    }

    @Override
    public boolean canReceiveLiquidFrom(Building from, Point point, LiquidType type) {
        return type == LiquidType.ROCKET_FUEL && currentFuel < REQUIRED_FUEL;
    }

    public int getCurrentMicrochips() { return currentMicrochips; }
    public int getCurrentSteel() { return currentSteel; }
    public float getCurrentFuel() { return currentFuel; }

    public float getTotalProgress() {
        float p1 = (float) currentMicrochips / REQUIRED_MICROCHIPS;
        float p2 = (float) currentSteel / REQUIRED_STEEL;
        float p3 = currentFuel / REQUIRED_FUEL;
        return (p1 + p2 + p3) / 3.0f;
    }

    public boolean isReadyToLaunch() {
        return currentMicrochips >= REQUIRED_MICROCHIPS &&
            currentSteel >= REQUIRED_STEEL &&
            currentFuel >= REQUIRED_FUEL;
    }

    public void launch() {
        if (isReadyToLaunch()) {
            currentMicrochips = 0;
            currentSteel = 0;
            currentFuel = 0f;
            hasLaunched = true;
        }
    }

    public boolean hasLaunched() {
        return hasLaunched;
    }
}
