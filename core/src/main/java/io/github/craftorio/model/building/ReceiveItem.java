package io.github.craftorio.model.building;

import io.github.craftorio.model.item.ItemType;

import java.awt.*;

public interface ReceiveItem {
    public boolean receiveItem(Building from, ItemType type);
    public boolean canReceiveItemFrom(Building from, Point point);
}
