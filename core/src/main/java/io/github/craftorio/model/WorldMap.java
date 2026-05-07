package io.github.craftorio.model;

public class WorldMap {
    private final Cell[][] map;
    private final int width;
    private final int height;

    WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
        map = new Cell[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[i][j] = new Cell(i, j, Cell.resourceType.NONE);
            }
        }
    }

    int getWidth() {
        return this.width;
    }

    int getHeight() {
        return this.height;
    }

    Cell getCell(int row, int col) {
        if (row < 0 || col < 0 || row >= height || col >= width) {
            throw new IndexOutOfBoundsException();
        }
        return map[row][col];
    }
}
