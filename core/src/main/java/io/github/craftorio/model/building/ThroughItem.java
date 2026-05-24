package io.github.craftorio.model.building;

import io.github.craftorio.model.item.ItemType;

import java.awt.*;

public interface ThroughItem {
    public boolean throughItem(ItemType type);
    public boolean canThroughIn(Point point);
}
