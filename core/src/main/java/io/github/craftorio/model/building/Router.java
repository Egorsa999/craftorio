package io.github.craftorio.model.building;

import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.ItemType;

import java.awt.*;

public class Router extends Building implements ReceiveItem, ThroughItem {
    public Router(BuildingRegistry registry, Point anchor, Direction direction, BuildingType type) {
        super(registry, anchor, direction, type);
    }

    @Override
    public void update() {

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
