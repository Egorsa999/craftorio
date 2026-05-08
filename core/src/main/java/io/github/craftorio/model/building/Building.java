package io.github.craftorio.model.building;

import io.github.craftorio.model.WorldMap;

public abstract class Building {
    protected final WorldMap worldMap;
    // bottom-left corner coordinates
    private final int row;
    private final int col;
    // size of object
    private final int width;
    private final int height;

    public Building(WorldMap worldMap, int col, int row, int width, int height) {
        this.worldMap = worldMap;
        this.row = row;
        this.col = col;
        this.width = width;
        this.height = height;
    }

    public abstract void update(float delta);

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }
}
