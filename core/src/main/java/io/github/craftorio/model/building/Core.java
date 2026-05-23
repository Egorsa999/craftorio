package io.github.craftorio.model.building;

import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.ItemType;
import io.github.craftorio.model.ui.Inventory;

import java.awt.*;

public class Core extends DamageableBuilding implements ReceiveItem {

    private final Inventory inventory;

    public Core(Inventory inventory, BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.CORE);
        this.inventory = inventory;
    }


    @Override
    public void update() {
        super.update();
    }

    @Override
    public boolean receiveItem(Building building, ItemType type) {
        inventory.add(type, 1);
        return true;
    }

    @Override
    public boolean canReceiveFrom(Building building, Point point) {
        return true;
    }
}
