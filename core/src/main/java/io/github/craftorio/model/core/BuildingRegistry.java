package io.github.craftorio.model.core;

import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.liquid.LiquidNetworkNode;
import io.github.craftorio.model.building.logistics.Pipe;

import java.awt.Point;
import java.util.*;
import java.util.List;

public class BuildingRegistry {


    Map<Point, Building> spatialGrid = new HashMap<>();
    Map<Point, Building> collisionGrid = new HashMap<>();

    private final List<Building> activeBuildings = new ArrayList<>();

    private final List<Building> pendingAdds = new ArrayList<>();
    private final List<Building> pendingRemoves = new ArrayList<>();

    private boolean liquidNetworksDirty = false;

    public void addBuilding(Building building) {
        for (Point p : building.getOccupiedTiles()) {
            spatialGrid.put(p, building);
        }
        for (Point p : building.getCollisionTiles()) {
            collisionGrid.put(p, building);
        }
        pendingAdds.add(building);
        if (building instanceof LiquidNetworkNode) {
            liquidNetworksDirty = true;
        }
    }

    public void removeBuilding(Building building) {
        for (Point p : building.getOccupiedTiles()) {
            spatialGrid.remove(p);
        }
        for (Point p : building.getCollisionTiles()){
            collisionGrid.remove(p);
        }
        pendingRemoves.add(building);
        if (building instanceof LiquidNetworkNode) {
            liquidNetworksDirty = true;
        }
    }

    public boolean consumeLiquidNetworksDirty() {
        if (!liquidNetworksDirty) {
            return false;
        }
        liquidNetworksDirty = false;
        return true;
    }

    public Building getBuildingAt(int x, int y) {
        return spatialGrid.get(new Point(x, y));
    }

    public Building getBuildingAtRemove(Point position) {
        return collisionGrid.get(position);
    }

    //call before and after ticks
    public void applyPendingChanges() {
        if (!pendingAdds.isEmpty()) {
            activeBuildings.addAll(pendingAdds);
            pendingAdds.clear();
        }
        if (!pendingRemoves.isEmpty()) {
            activeBuildings.removeAll(pendingRemoves);
            pendingRemoves.clear();
        }
    }

    public List<Building> getBuildingsForTick() {
        return Collections.unmodifiableList(activeBuildings);
    }

    public float getMaxX() {
        if (spatialGrid.isEmpty()) return 0;

        int maxX = Integer.MIN_VALUE;
        for (Point p : spatialGrid.keySet()) {
            if (p.x > maxX) {
                maxX = p.x;
            }
        }
        return maxX;
    }

    public float getMaxY() {
        if (spatialGrid.isEmpty()) return 0;

        int maxY = Integer.MIN_VALUE;
        for (Point p : spatialGrid.keySet()) {
            if (p.y > maxY) {
                maxY = p.y;
            }
        }
        return maxY;
    }

    public float getMinX() {
        if (spatialGrid.isEmpty()) return 0;

        int minX = Integer.MAX_VALUE;
        for (Point p : spatialGrid.keySet()) {
            if (p.x < minX) {
                minX = p.x;
            }
        }
        return minX;
    }

    public float getMinY() {
        if (spatialGrid.isEmpty()) return 0;

        int minY = Integer.MAX_VALUE;
        for (Point p : spatialGrid.keySet()) {
            if (p.y < minY) {
                minY = p.y;
            }
        }
        return minY;
    }
}
