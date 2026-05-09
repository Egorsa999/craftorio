package io.github.craftorio.model.building;

import io.github.craftorio.model.ItemType;

public interface ThroughItem {
    public boolean throughItem(ItemType type, Float progress);
}
