package io.github.craftorio.model.ui;

import io.github.craftorio.model.ItemType;

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

    public boolean take(ItemType item, Integer quantity){
        if (!isContains(item, quantity))return false;
        items.put(item, items.getOrDefault(item, 0) + quantity);
        return true;
    }

    public Map<ItemType, Integer> getItems(){
        return items;
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
