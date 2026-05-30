package io.github.craftorio.model.generator;

import io.github.craftorio.model.item.LiquidType;

public enum TerrainType {
    WALL(false),
    GRASS(true),
    SAND(true),
    WATER(false, LiquidType.WATER),
    OIL(false, LiquidType.OIL);

    private final boolean walkable;
    private final LiquidType liquidType;

    TerrainType(boolean walkable) {
        this.walkable = walkable;
        this.liquidType = null;
    }
    TerrainType(boolean walkable, LiquidType liquidType) {
        this.walkable = walkable;
        this.liquidType = liquidType;
    }
    public boolean getWalkability() {return this.walkable;}
    public LiquidType getLiquidType() {
        return this.liquidType;
    }
}
