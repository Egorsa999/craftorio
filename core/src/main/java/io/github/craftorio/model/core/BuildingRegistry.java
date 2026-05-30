package io.github.craftorio.model.core;

import io.github.craftorio.model.building.Building;
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
        if (building instanceof Pipe) {
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
        if (building instanceof Pipe) {
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
}
