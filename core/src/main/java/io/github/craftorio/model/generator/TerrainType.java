package io.github.craftorio.model.generator;

import io.github.craftorio.model.item.LiquidType;

public enum TerrainType {
    WALL(false),
    GRASS(true),
    SAND(true),
    WATER(false, LiquidType.WATER, 1.2f),
    OIL(false, LiquidType.OIL, 0.6f);

    private final boolean walkable;
    private final LiquidType liquidType;
    private final float ratio;

    TerrainType(boolean walkable) {
        this.walkable = walkable;
        this.liquidType = null;
        this.ratio = 0.0f;
    }
    TerrainType(boolean walkable, LiquidType liquidType, float ratio) {
        this.walkable = walkable;
        this.liquidType = liquidType;
        this.ratio = ratio;
    }
    public boolean getWalkability() {return this.walkable;}
    public LiquidType getLiquidType() {
        return this.liquidType;
    }

    public float getRatio() {
        return this.ratio;
    }
}
