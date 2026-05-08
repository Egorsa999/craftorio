package io.github.craftorio.model.generator;

import io.github.craftorio.model.ItemType;

public enum OreType {
    IRON(ItemType.IRON_ORE, 1.2f),   // Выдает предмет IRON_ORE
    COPPER(ItemType.COPPER_ORE, 1.2f),
    COAL(ItemType.COAL, 0.8f);


    private final ItemType drop;
    private final float miningDifficulty;

    OreType(ItemType drop, float difficulty) {
        this.drop = drop;
        this.miningDifficulty = difficulty;
    }

    public ItemType getDrop() { return drop; }
}
