package io.github.craftorio.model;

import com.badlogic.gdx.math.MathUtils;

public class WorldMap {
    private final Cell[][] map;
    private final int width;
    private final int height;

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
        map = new Cell[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rng = MathUtils.random(2);
                Cell.resourceType cellType = null;
                if (rng == 0) cellType = Cell.resourceType.NONE;
                if (rng == 1) cellType = Cell.resourceType.IRON;
                if (rng == 2) cellType = Cell.resourceType.COPPER;
                map[i][j] = new Cell(i, j, cellType);
            }
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private void checkBound(int row, int col) {
        if (row < 0 || col < 0 || row >= height || col >= width) {
            throw new IndexOutOfBoundsException();
        }
    }

    public Cell getCell(int row, int col) {
        checkBound(row, col);
        return map[row][col];
    }

    public void setCell(int row, int col, Cell.resourceType resourseType) {
        checkBound(row, col);
        map[row][col].update(resourseType);
    }
}
