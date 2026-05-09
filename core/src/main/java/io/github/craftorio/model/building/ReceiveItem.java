package io.github.craftorio.model.building;

import io.github.craftorio.model.ItemType;

import java.awt.*;

public interface ReceiveItem {
    public boolean receiveItem(ItemType type, Float progress);
    public boolean canReceiveFromMe(Point point);
}
