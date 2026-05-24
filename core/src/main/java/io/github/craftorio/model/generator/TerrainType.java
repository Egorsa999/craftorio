package io.github.craftorio.model.generator;

public enum TerrainType {
    WALL(false),
    GRASS(true),
    SAND(true),
    WATER(false);
    private final boolean walkable;
    TerrainType(boolean walkable) {
        this.walkable = walkable;
    }
    public boolean getWalkability() {return this.walkable;}

}
