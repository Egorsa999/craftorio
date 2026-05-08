package io.github.craftorio.model;

import io.github.craftorio.model.building.Belt;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.Direction;
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
        placeBuilding(new Belt(50, 50, Direction.UP));
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
        map[row][col].updateResourceType(resourseType);
    }

    public void placeBuilding(Building building) {
        int row = building.getRow();
        int col = building.getCol();
        boolean canPlace = true;
        for (int i = row; i < row + building.getHeight(); i++) {
            for (int j = col; j < col + building.getWidth(); j++) {
                checkBound(i, j);
                if (map[i][j].isOccupied()) {
                    canPlace = false;
                }
            }
        }
        if (canPlace) {
            for (int i = row; i < row + building.getHeight(); i++) {
                for (int j = col; j < col + building.getWidth(); j++) {
                    map[i][j].updateOccupiedBuilding(building);
                }
            }
        }
    }

    public void update(float delta) {
        // TODO optimise update for each building not in stupid way like this
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (map[i][j].isOccupied()
                    && i == map[i][j].getOccupiedBuilding().getRow()
                    && j == map[i][j].getOccupiedBuilding().getCol()) {
                    map[i][j].getOccupiedBuilding().update(delta);
                }
            }
        }
    }
}
