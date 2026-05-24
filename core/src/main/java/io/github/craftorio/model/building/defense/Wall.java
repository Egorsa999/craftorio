package io.github.craftorio.model.building.defense;

import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.DamageableBuilding;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.core.BuildingRegistry;

import java.awt.*;

public class Wall extends DamageableBuilding {
    public Wall(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.WALL);
    }
}
