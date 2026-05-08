package io.github.craftorio.model;

import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.BuildingFactory;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.Direction;

import java.awt.Point;
import java.util.List;

public class BuildingManager {
    private final BuildingRegistry registry;
    private final WorldMap worldMap;
    private final BuildingFactory factory;


    public BuildingManager(BuildingRegistry registry, WorldMap worldMap, BuildingFactory factory) {
        this.registry = registry;
        this.worldMap = worldMap;
        this.factory = factory;
    }

    public boolean tryPlaceBuilding(BuildingType type, Point anchor, Direction rotation){
        List<Point> requiredTiles = factory.calculateOccupiedTiles(type, anchor, rotation);

        if (!isAreaFree(requiredTiles)) {
            return false;
        }

        if (!isValidTerrainFor(type, requiredTiles)) {
            return false;
        }

        Building newBuilding = factory.createBuilding(type, anchor, rotation);
        registry.addBuilding(newBuilding);

        return true;
    }


    private boolean isAreaFree(List<Point> tiles) {
        for (Point p : tiles) {
            if (registry.getBuildingAt(p) != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidTerrainFor(BuildingType type, List<Point> tiles) {
        //TODO something with worldMap
        return true;
    }

}
