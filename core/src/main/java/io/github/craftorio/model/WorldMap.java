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

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                map[row][col] = new Cell(col, row, Cell.resourceType.NONE);
            }
        }

        MapGenerator.generateMap(this);
        placeBuilding(new Belt(this, 50, 50, Direction.UP));
        placeBuilding(new Belt(this, 50, 51, Direction.UP));
        placeBuilding(new Belt(this, 50, 52, Direction.UP));
        placeBuilding(new Belt(this, 50, 53, Direction.UP));

        Belt belt = (Belt) map[50][50].getOccupiedBuilding();
        belt.acceptItem(3, 0.9f);
        belt.acceptItem(2, 0.5f);
        belt.acceptItem(1, 0.0f);
        belt = (Belt) map[51][50].getOccupiedBuilding();
        belt.acceptItem(1, 0.9f);
        belt.acceptItem(1, 0.0f);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private void checkBound(int col, int row) {
        if (row < 0 || col < 0 || row >= height || col >= width) {
            throw new IndexOutOfBoundsException();
        }
    }

    public Cell getCell(int col, int row) {
        checkBound(col, row);
        return map[row][col];
    }

    public void setCell(int col, int row, Cell.resourceType resourseType) {
        checkBound(col, row);
        map[row][col].updateResourceType(resourseType);
    }

    public void placeBuilding(Building building) {
        int row = building.getRow();
        int col = building.getCol();
        boolean canPlace = true;
        for (int i = row; i < row + building.getHeight(); i++) {
            for (int j = col; j < col + building.getWidth(); j++) {
                checkBound(j, i);
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
        Belt.updateAnimationOffset(delta);
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
