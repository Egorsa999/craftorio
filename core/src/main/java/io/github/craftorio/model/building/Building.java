package io.github.craftorio.model.building;

public abstract class Building {
    // bottom-left corner coordinates
    private final int row;
    private final int col;
    // size of object
    private final int width;
    private final int height;

    public Building(int row, int col, int width, int height) {
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
