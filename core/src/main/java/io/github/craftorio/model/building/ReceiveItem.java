package io.github.craftorio.model.building;

import io.github.craftorio.model.ItemType;

import java.awt.*;

public interface ReceiveItem {
    public boolean receiveItem(Building from, ItemType type);
    public boolean canReceiveFrom(Building from, Point point);
}
