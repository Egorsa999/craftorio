package io.github.craftorio.model.building;

import io.github.craftorio.model.WorldMap;
import io.github.craftorio.model.BuildingRegistry;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class BuildingFactory {

    private final WorldMap worldMap;
    private final BuildingRegistry registry;

    public BuildingFactory(WorldMap worldMap, BuildingRegistry registry) {
        this.worldMap = worldMap;
        this.registry = registry;
    }

    public int calculateOccupiedWidth(BuildingType type, Direction rotation){
        boolean isRotated90 = (rotation == Direction.LEFT || rotation == Direction.RIGHT);
        return isRotated90 ? type.getHeight() : type.getWidth();
    }

    public int calculateOccupiedHeight(BuildingType type, Direction rotation){
        boolean isRotated90 = (rotation == Direction.LEFT || rotation == Direction.RIGHT);
        return isRotated90 ? type.getWidth() : type.getHeight();
    }

    public List<Point> calculateOccupiedTiles(BuildingType type, Point anchor, Direction rotation) {
        List<Point> tiles = new ArrayList<>();


        int currentWidth = calculateOccupiedWidth(type, rotation);
        int currentHeight = calculateOccupiedHeight(type, rotation);

        for (int x = 0; x < currentWidth; x++) {
            for (int y = 0; y < currentHeight; y++) {
                tiles.add(new Point(anchor.x + x, anchor.y + y));
            }
        }

        return tiles;
    }

    public Building createBuilding(BuildingType type, Point unSafeAnchor, Direction rotation) {

        Point anchor = new Point(unSafeAnchor);


        int width = calculateOccupiedWidth(type, rotation);
        int height = calculateOccupiedHeight(type, rotation);

        return switch (type) {
            case MINER -> new Miner(registry, worldMap, anchor,
                width, height, rotation);
            case BELT -> new Belt(registry, anchor, rotation);
            default -> throw new IllegalArgumentException("Unknown Building Type : " + type);
        };
    }
}
