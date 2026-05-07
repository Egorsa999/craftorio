package io.github.craftorio.model;

import com.badlogic.gdx.math.MathUtils;
import io.github.craftorio.model.generator.MapGenerator;

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
                map[i][j] = new Cell(i, j, Cell.resourceType.NONE);
            }
        }

        MapGenerator.generateMap(this);
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
