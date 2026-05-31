package io.github.craftorio.model.ui;

import io.github.craftorio.model.item.ItemType;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private final Map<ItemType, Integer> items;

    public Inventory() {
        this.items = new HashMap<>();
    }

    public void add(ItemType item, Integer quantity){
        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    public boolean isContains(ItemType item, Integer quantity){
        return items.getOrDefault(item, 0) >= quantity;
    }

    public boolean canTake(Map<ItemType, Integer> map) {
        for (Map.Entry<ItemType, Integer> entry : map.entrySet()) {
            if (items.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public void take(Map<ItemType, Integer> map) {
        for (Map.Entry<ItemType, Integer> entry : map.entrySet()) {
            items.put(entry.getKey(), items.getOrDefault(entry.getKey(), 0) - entry.getValue());
            if (items.getOrDefault(entry.getKey(), 0) == 0) {
                items.remove(entry.getKey());
            }
        }
    }

    public Map<ItemType, Integer> getItems(){
        return items;
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
