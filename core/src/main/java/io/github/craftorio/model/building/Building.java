package io.github.craftorio.model.building;

import io.github.craftorio.model.core.BuildingRegistry;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public abstract class Building {
    protected final BuildingRegistry registry;
    // bottom-left corner coordinates
    public Point anchor;
    public Direction direction;
    // size of object
    private List<Point> occupiedTiles;
    private List<Point> collisionTiles;
    private final boolean walkable;
    public final BuildingType type;

    public Building(BuildingRegistry registry, Point anchor, Direction direction, BuildingType type) {
        this.registry = registry;
        this.anchor = anchor;
        this.direction = direction;
        this.type = type;

        this.occupiedTiles = generateTiles(type.getWidth(), type.getHeight());
        this.collisionTiles = generateTiles(type.getCollisionWidth(), type.getCollisionHeight());
        this.walkable = type.getWalkable();
    }

    private List<Point> generateTiles(int baseW, int baseH) {
        List<Point> tiles = new ArrayList<>();
        for (int x = 0; x < baseW; x++) {
            for (int y = 0; y < baseH; y++) {
                tiles.add(rotatePoint(x, y));
            }
        }
        return tiles;
    }

    public void setAnchor(int x, int y){
        this.anchor.x = x;
        this.anchor.y = y;

        this.occupiedTiles = generateTiles(type.getWidth(), type.getHeight());
        this.collisionTiles = generateTiles(type.getCollisionWidth(), type.getCollisionHeight());
    }

    public Point rotatePoint(int rx, int ry) {
        int realX = anchor.x;
        int realY = anchor.y;

        int totalBaseW = type.getWidth();
        int totalBaseH = type.getHeight();

        switch (direction) {
            case UP: realX += rx; realY += ry; break;
            case RIGHT: realX += ry; realY += (totalBaseW - 1) - rx; break;
            case DOWN: realX += (totalBaseW - 1) - rx; realY += (totalBaseH - 1) - ry; break;
            case LEFT: realX += (totalBaseH - 1) - ry; realY += rx; break;
        }
        return new Point(realX, realY);
    }

    protected void removeSelf() {
        registry.removeBuilding(this);
    }

    public List<Point> getOccupiedTiles() { return occupiedTiles; }
    public List<Point> getCollisionTiles() { return collisionTiles; }

    public abstract void update();

    public int getX() { return this.anchor.x; }
    public int getY() { return this.anchor.y; }
    public Point getAnchor() { return this.anchor; }

    public int getHeight() {
        boolean isRotated90 = (direction == Direction.LEFT || direction == Direction.RIGHT);
        return isRotated90 ? type.getWidth() : type.getHeight();
    }

    public int getWidth() {
        boolean isRotated90 = (direction == Direction.LEFT || direction == Direction.RIGHT);
        return isRotated90 ? type.getHeight() : type.getWidth();
    }

    public boolean getWalkable() { return this.walkable; }
}
