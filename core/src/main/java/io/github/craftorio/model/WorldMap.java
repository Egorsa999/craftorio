package io.github.craftorio.model;

import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.generator.Cell;
import io.github.craftorio.model.generator.MapGenerator;
import io.github.craftorio.model.generator.ResourceType;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

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
                map[row][col] = new Cell(col, row, ResourceType.NONE);
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

    private void checkBound(int col, int row) {
        if (row < 0 || col < 0 || row >= height || col >= width) {
            throw new IndexOutOfBoundsException();
        }
    }

    public Cell getCell(int col, int row) {
        checkBound(col, row);
        return map[row][col];
    }

    public void setCell(int col, int row, ResourceType resourseType) {
        checkBound(col, row);
        map[row][col].updateResourceType(resourseType);
    }

    public List<ResourceType> getResources (List<Point> tiles){
        List<ResourceType> resources = new ArrayList<>();
        for (Point tile : tiles){
            if (getCell(tile.x, tile.y).getResourceType() == ResourceType.COPPER){
                resources.add(ResourceType.COPPER);
            }
            if (getCell(tile.x, tile.y).getResourceType() == ResourceType.IRON){
                resources.add(ResourceType.IRON);
            }
        }
        return resources;
    }
}
