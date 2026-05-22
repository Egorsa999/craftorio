package io.github.craftorio.model.building;

import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.ItemType;

import java.awt.*;

public class Turret extends Building implements ReceiveItem {
    private final ItemType ammoType = ItemType.COPPER_ORE;
    private int ammoAmount = 0;


    public Turret(BuildingRegistry registry, Point anchor, Direction direction, BuildingType type) {
        super(registry, anchor, direction, type);
    }

    @Override
    public void update() {

    }

    @Override
    public boolean receiveItem(ItemType type, Float progress) {
        if (type != ammoType) return false;
        ammoAmount++;
        return true;
    }

    @Override
    public boolean canReceiveFrom(Point point) {
        return true;
    }
}
