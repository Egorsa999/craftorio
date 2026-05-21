package io.github.craftorio.model.building;

import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.ItemType;

import java.awt.*;

public class Assembler extends Building implements ThroughItem, ReceiveItem {
    public Assembler(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.ASSEMBLER);
    }

    @Override
    public void update() {
        return;
    }

    @Override
    public boolean receiveItem(ItemType type, Float progress) {
        return false;
    }

    @Override
    public boolean canReceiveFrom(Point point) {
        return false;
    }

    @Override
    public boolean throughItem(ItemType type, Float progress) {
        return false;
    }

    @Override
    public boolean canThroughIn(Point point) {
        return false;
    }
}
