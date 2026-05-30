package io.github.craftorio.model.building;

import io.github.craftorio.model.building.defense.Turret;
import io.github.craftorio.model.building.defense.Wall;
import io.github.craftorio.model.building.logistics.Belt;
import io.github.craftorio.model.building.logistics.Junction;
import io.github.craftorio.model.building.logistics.Pipe;
import io.github.craftorio.model.building.production.Pump;
import io.github.craftorio.model.building.logistics.Router;
import io.github.craftorio.model.building.production.Assembler;
import io.github.craftorio.model.building.production.HorizontalMiner;
import io.github.craftorio.model.building.production.Miner;
import io.github.craftorio.model.building.storage.Core;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.core.SimulationEngine;
import io.github.craftorio.model.core.WorldMap;
import io.github.craftorio.model.ui.Inventory;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class BuildingFactory {
    private final BuildingRegistry registry;
    private final Inventory inventory;
    private final WorldMap worldMap;
    private final SimulationEngine engine;

    public BuildingFactory(BuildingRegistry registry, Inventory inventory, WorldMap worldMap, SimulationEngine engine) {
        this.registry = registry;
        this.inventory = inventory;
        this.worldMap = worldMap;
        this.engine = engine;
    }

    public int calculateOccupiedWidth(BuildingType type, Direction rotation){
        boolean isRotated90 = (rotation == Direction.LEFT || rotation == Direction.RIGHT);
        return isRotated90 ? type.getCollisionHeight() : type.getCollisionWidth();
    }

    public int calculateOccupiedHeight(BuildingType type, Direction rotation){
        boolean isRotated90 = (rotation == Direction.LEFT || rotation == Direction.RIGHT);
        return isRotated90 ? type.getCollisionWidth() : type.getCollisionHeight();
    }

    public int calculateRenderWidth(BuildingType type, Direction rotation){
        boolean isRotated90 = (rotation == Direction.LEFT || rotation == Direction.RIGHT);
        return isRotated90 ? type.getHeight() : type.getWidth();
    }

    public int calculateRenderHeight(BuildingType type, Direction rotation){
        boolean isRotated90 = (rotation == Direction.LEFT || rotation == Direction.RIGHT);
        return isRotated90 ? type.getWidth() : type.getHeight();
    }

    public List<Point> calculateOccupiedTiles(BuildingType type, Point anchor, Direction direction) {
        List<Point> tiles = new ArrayList<>();
        for (int x = 0; x < type.getCollisionWidth(); x++) {
            for (int y = 0; y < type.getCollisionHeight(); y++) {
                tiles.add(getRealCoordinates(new Point(x, y), anchor, direction, type));
            }
        }
        return tiles;
    }

    public Point getRealCoordinates(Point relativePoint, Point anchor, Direction direction, BuildingType type) {
        int realX = anchor.x;
        int realY = anchor.y;
        int rx = relativePoint.x;
        int ry = relativePoint.y;
        int baseWidth = type.getWidth();
        int baseHeight = type.getHeight();

        switch (direction) {
            case UP: realX += rx; realY += ry; break;
            case RIGHT: realX += ry; realY += (baseWidth - 1) - rx; break;
            case DOWN: realX += (baseWidth - 1) - rx; realY += (baseHeight - 1) - ry; break;
            case LEFT: realX += (baseHeight - 1) - ry; realY += rx; break;
        }
        return new Point(realX, realY);
    }

    public Building createBuilding(BuildingType type, Point unSafeAnchor, Direction rotation) {
        Point anchor = new Point(unSafeAnchor);

        return switch (type) {
            case MINER -> new Miner(registry, anchor, rotation, worldMap);
            case BELT -> new Belt(registry, anchor, rotation);
            case HORIZONTAL_MINER -> new HorizontalMiner(registry, anchor, rotation);
            case CORE -> new Core(registry, anchor, rotation, inventory);
            case TURRET -> new Turret(registry, anchor, rotation, engine);
            case JUNCTION -> new Junction(registry, anchor, rotation);
            case ROUTER -> new Router(registry, anchor, rotation);
            case ASSEMBLER -> new Assembler(registry, anchor, rotation);
            case WALL -> new Wall(registry, anchor, rotation);
            case PIPE -> new Pipe(registry, anchor, rotation);
            case PUMP -> new Pump(registry, anchor, rotation, worldMap);
            default -> throw new IllegalArgumentException("Unknown Building Type : " + type);
        };
    }
}
