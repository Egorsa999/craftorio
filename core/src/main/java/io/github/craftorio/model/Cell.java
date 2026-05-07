package io.github.craftorio.model;

public class Cell {
    public enum resourceType {NONE, IRON, COPPER};

    private final int row;
    private final int col;
    private final resourceType resourceType;

    public Cell(int row, int col, resourceType resourceType) {
        this.row = row;
        this.col = col;
        this.resourceType = resourceType;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public resourceType getResourseType() {
        return this.resourceType;
    }
}
