package io.github.craftorio.model.generator;

import io.github.craftorio.model.item.ItemType;

public enum ResourceType {
    IRON(ItemType.IRON_ORE, 1.2f),   // Выдает предмет IRON_ORE
    COPPER(ItemType.COPPER_ORE, 1.2f),
    COAL(ItemType.COAL, 0.8f),
    NONE(null, 0);


    private final ItemType drop;
    private final float miningDifficulty;

    ResourceType(ItemType drop, float difficulty) {
        this.drop = drop;
        this.miningDifficulty = difficulty;
    }

    public ItemType getDrop() { return drop; }
    public float getMiningDifficulty() {return miningDifficulty;}
}
