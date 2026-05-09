package io.github.craftorio.model.building;

import io.github.craftorio.model.BuildingRegistry;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


public abstract class Building {
    protected final BuildingRegistry registry;
    // bottom-left corner coordinates
    public Point anchor;
    public Direction direction;
    // size of object
    private final int width;
    private final int height;
    private final List<Point> occupiedTiles;
    public final BuildingType type;

    public Building(BuildingRegistry registry, Point anchor, int width, int height, Direction direction, BuildingType type) {
        this.registry = registry;
        this.anchor = anchor;
        this.width = width;
        this.height = height;
        this.direction = direction;
        this.type = type;

        occupiedTiles = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                occupiedTiles.add(new Point(anchor.x + x, anchor.y + y));
            }
        }
    }

    public List<Point> getOccupiedTiles() {
        return occupiedTiles;
    }

    public abstract void update();

    public int getX() {
        return this.anchor.x;
    }

    public int getY() {
        return this.anchor.y;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }
}
