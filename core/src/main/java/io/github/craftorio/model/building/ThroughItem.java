package io.github.craftorio.model.building;

import io.github.craftorio.model.ItemType;

import java.awt.*;

public interface ThroughItem {
    public boolean throughItem(ItemType type, Float progress);
    public boolean canThroughIn(Point point);
}
