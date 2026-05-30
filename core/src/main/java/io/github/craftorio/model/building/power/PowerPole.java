package io.github.craftorio.model.building.power;

import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.DamageableBuilding;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.core.BuildingRegistry;

import java.awt.*;

public class PowerPole extends DamageableBuilding implements PowerConnectable {
    private PowerNode powerNode;

    public PowerPole(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.POWER_POLE);
        powerNode = new PowerNode(this, registry);
    }

    @Override
    public PowerNode getPowerNode() {
        return powerNode;
    }

}
