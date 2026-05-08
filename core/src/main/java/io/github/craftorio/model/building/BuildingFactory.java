package io.github.craftorio.model.building;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class BuildingFactory {
    public List<Point> calculateOccupiedTiles(BuildingType type, Point anchor, Direction rotation) {
        List<Point> tiles = new ArrayList<>();


        boolean isRotated90 = (rotation == Direction.LEFT || rotation == Direction.RIGHT);

        int currentWidth = isRotated90 ? type.getHeight() : type.getWidth();
        int currentHeight = isRotated90 ? type.getWidth() : type.getHeight();

        for (int x = 0; x < currentWidth; x++) {
            for (int y = 0; y < currentHeight; y++) {
                tiles.add(new Point(anchor.x + x, anchor.y + y));
            }
        }

        return tiles;
    }

    public Building createBuilding(BuildingType type, Point anchor, Direction rotation) {
        List<Point> footprint = calculateOccupiedTiles(type, anchor, rotation);

//        return switch (type) {
//            case BELT -> new Belt(anchor, footprint, rotation);
//            default -> throw new IllegalArgumentException("Unknown Building Type : " + type);
//        };
    }
}
