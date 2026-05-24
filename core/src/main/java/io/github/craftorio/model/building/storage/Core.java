package io.github.craftorio.model.building.storage;

import io.github.craftorio.model.building.*;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.core.GameContext;
import io.github.craftorio.model.item.ItemType;

import java.awt.*;

public class Core extends DamageableBuilding implements ReceiveItem {

    public Core(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.CORE);
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public boolean receiveItem(Building building, ItemType type) {
        // Достаем инвентарь напрямую из контекста!
        GameContext.current.inventory.add(type, 1);
        return true;
    }

    @Override
    public boolean canReceiveFrom(Building building, Point point) {
        return true;
    }
}
