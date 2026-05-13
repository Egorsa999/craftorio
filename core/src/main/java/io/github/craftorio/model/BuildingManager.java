package io.github.craftorio.model;

import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.BuildingFactory;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.generator.TerrainType;

import java.awt.Point;
import java.util.List;

public class BuildingManager {
    private final BuildingRegistry registry;
    private final WorldMap worldMap;
    private final BuildingFactory factory;
    private final Player player;

    public BuildingManager(BuildingRegistry registry, WorldMap worldMap, BuildingFactory factory, Player player) {
        this.registry = registry;
        this.worldMap = worldMap;
        this.factory = factory;
        this.player = player;
    }

    public boolean tryRemoveBuilding(Point pos){
        Building toDelete = registry.getBuildingAtRemove(pos);
        System.out.println(pos + " " + toDelete);
        if (toDelete == null || toDelete.type == BuildingType.CORE)return false;

        registry.removeBuilding(toDelete);
        return true;
    }

    public boolean tryPlaceBuilding(BuildingType type, Point anchor, Direction direction){
        if (type == BuildingType.CORE){
            Building newBuilding = factory.createBuilding(type, anchor, direction);
            registry.addBuilding(newBuilding);
            return true;
        }

        if (!isValidPlace(type, anchor, direction)){
            return false;
        }

        Building newBuilding = factory.createBuilding(type, anchor, direction);
        registry.addBuilding(newBuilding);
        return true;
    }

    public boolean isValidPlace(BuildingType type, Point anchor, Direction direction){
        List<Point> requiredTiles = factory.calculateOccupiedTiles(type, anchor, direction);
        if (!isAreaFree(requiredTiles) || !isValidTerrainFor(type, requiredTiles, anchor, direction) || !playerNotStuck(requiredTiles, type)) {
            return false;
        }
        return true;
    }

    private boolean playerNotStuck(List<Point> requiredTiles, BuildingType type){
        if(type.getWalkable() == true) return true;
        Point playerLocation = player.getLocation();
        for (Point p : requiredTiles){
            if(p.equals(playerLocation))
                return false;
        }
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

    private boolean isValidTerrainFor(BuildingType type, List<Point> tiles, Point anchor, Direction direction) {
        for (Point p : tiles) {
            TerrainType terrainType = worldMap.getCell(p.x, p.y).getTerrainType();
            if(terrainType == TerrainType.WALL || terrainType == TerrainType.WATER){
                return false;
            }
        }
        if (type == BuildingType.MINER){
            if (worldMap.getResources(tiles).isEmpty())return false;
        }
        if (type == BuildingType.HORIZONTAL_MINER){
            Point point = factory.getRealCoordinates(new Point(0, 2), anchor, direction, type);
            if (worldMap.getCell(point.x, point.y).getTerrainType() != TerrainType.WALL) return false;
        }
        return true;
    }

}
