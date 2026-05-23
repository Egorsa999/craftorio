package io.github.craftorio.model.core;

import com.badlogic.gdx.math.MathUtils;
import io.github.craftorio.model.generator.Cell;
import io.github.craftorio.model.generator.MapGenerator;
import io.github.craftorio.model.generator.ResourceType;
import io.github.craftorio.model.generator.TerrainType;

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
                map[row][col] = new Cell(col, row, ResourceType.NONE, TerrainType.GRASS);
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

    public void setResourceType(int col, int row, ResourceType resourceType) {
        checkBound(col, row);
        map[row][col].updateResourceType(resourceType);
    }

    public void setTerrainType(int col, int row, TerrainType terrainType) {
        checkBound(col, row);
        map[row][col].updateTerrainType(terrainType);
    }

    public Point findSpawnPoint(){
        int counter = 0;
        while (true){
            int x = MathUtils.random(width / 2 - 200, width / 2 + 200);
            int y = MathUtils.random(height / 2 - 200, height / 2 + 200);
            boolean isGood = true;
            for (int dx = 0; dx <= 7; dx++){
                for (int dy = 0; dy <= 7; dy++){
                    Cell current = getCell(x + dx, y + dy);
                    isGood &= current.getTerrainType() == TerrainType.GRASS ||
                        current.getTerrainType() == TerrainType.SAND;
                    isGood &= current.getResourceType() == ResourceType.NONE;
                }
            }
            if (counter++ == 10000)break;
            if (isGood)return new Point(x + 1, y + 3);
            }
        System.err.println("Can't find spawnPoint!");
        return new Point(10, 10);
    }

    public List<ResourceType> getResources (List<Point> tiles){
        List<ResourceType> resources = new ArrayList<>();
        for (Point tile : tiles){
            var resource = getCell(tile.x, tile.y).getResourceType();
            if (resource != ResourceType.NONE)resources.add(resource);
        }
        return resources;
    }
}
