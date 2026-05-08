package io.github.craftorio.model;

import io.github.craftorio.model.building.Building;

import java.awt.Point;
import java.util.*;
import java.util.List;

public class BuildingRegistry {


    Map<Point, Building> spatialGrid = new HashMap<>();

    private final List<Building> activeBuildings = new ArrayList<>();

    private final List<Building> pendingAdds = new ArrayList<>();
    private final List<Building> pendingRemoves = new ArrayList<>();

    public void addBuilding(Building building) {
        for (Point p : building.getOccupiedTiles()) {
            spatialGrid.put(p, building);
        }
        pendingAdds.add(building);
    }

    public void removeBuilding(Building building) {
        for (Point p : building.getOccupiedTiles()) {
            spatialGrid.remove(p);
        }
        pendingRemoves.add(building);
    }

    public Building getBuildingAt(Point position) {
        return spatialGrid.get(position);
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
