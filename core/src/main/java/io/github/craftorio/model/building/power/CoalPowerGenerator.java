package io.github.craftorio.model.building.power;

import io.github.craftorio.model.building.*;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.ItemType;

import java.awt.*;

public class CoalPowerGenerator extends DamageableBuilding implements PowerProducer, ReceiveItem, PowerConnectable {

    private final PowerNode powerNode;

    private final float maxPowerPerTick = 100.0f / 60.0f;

    private int coalInInventory = 0;
    private final int maxCoalCapacity = 10;

    private float fuelFrames = 0;
    private final float FRAMES_PER_COAL = 300f;

    private float currentLoadRatio = 0f;

    public CoalPowerGenerator(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.COAL_POWER_GENERATOR);
        this.powerNode = new PowerNode(this, registry);
    }

    @Override
    public void update() {
        super.update();

        if (fuelFrames <= 0 && coalInInventory > 0 && currentLoadRatio > 0) {
            coalInInventory--;
            fuelFrames += FRAMES_PER_COAL;
        }

        if (fuelFrames > 0) {
            fuelFrames -= currentLoadRatio;
        }

    }

    @Override
    public PowerNode getPowerNode() {
        return powerNode;
    }

    @Override
    public boolean receiveItem(Building from, ItemType type) {
        if (type == ItemType.COAL && coalInInventory < maxCoalCapacity) {
            coalInInventory++;
            return true;
        }
        return false;
    }

    @Override
    public boolean canReceiveItemFrom(Building from, Point point) {
        return true;
    }


    @Override
    public float getPotentialOutput() {
        if (fuelFrames > 0 || coalInInventory > 0) {
            return maxPowerPerTick;
        }
        return 0f;
    }

    @Override
    public void setLoadRatio(float ratio) {
        this.currentLoadRatio = Math.max(0f, Math.min(ratio, 1.0f));
    }
}
