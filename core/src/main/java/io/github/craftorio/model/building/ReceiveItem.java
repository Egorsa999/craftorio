package io.github.craftorio.model.building;

import io.github.craftorio.model.ItemType;

public interface ReceiveItem {
    public boolean receiveItem(ItemType type, Float progress);
}
