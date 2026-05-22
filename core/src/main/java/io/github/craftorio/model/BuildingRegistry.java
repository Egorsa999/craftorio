package io.github.craftorio.model;

import io.github.craftorio.model.building.Building;

import java.awt.Point;
import java.util.*;
import java.util.List;

public class BuildingRegistry {


    Map<Point, Building> spatialGrid = new HashMap<>();
    Map<Point, Building> collisionGrid = new HashMap<>();

    private final List<Building> activeBuildings = new ArrayList<>();

    private final List<Building> pendingAdds = new ArrayList<>();
    private final List<Building> pendingRemoves = new ArrayList<>();

    public void addBuilding(Building building) {
        for (Point p : building.getOccupiedTiles()) {
            spatialGrid.put(p, building);
        }
        for (Point p : building.getCollisionTiles()) {
            collisionGrid.put(p, building);
        }
        pendingAdds.add(building);
    }

    public void removeBuilding(Building building) {
        for (Point p : building.getOccupiedTiles()) {
            spatialGrid.remove(p);
        }
        pendingRemoves.add(building);
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
}
