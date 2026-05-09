package io.github.craftorio.model.building;

import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.ItemType;
import io.github.craftorio.model.generator.Cell;
import io.github.craftorio.model.WorldMap;
import io.github.craftorio.model.generator.ResourceType;

import java.awt.Point;
import java.util.*;

public class Miner extends Building implements ThroughItem, ReceiveItem {

    private final WorldMap worldMap;

    private final Queue<ItemType> outputBuffer = new LinkedList<>();
    private final int MAX_BUFFER_SIZE = 10;

    private final Map<ResourceType, Integer> oreCoverage = new EnumMap<>(ResourceType.class);
    private final Map<ResourceType, Integer> oreProgress = new EnumMap<>(ResourceType.class);

    private final int BASE_TICKS_PER_ITEM = 120;

    public Miner(BuildingRegistry registry, WorldMap worldMap, Point anchor, int width, int height, Direction direction) {
        super(registry, anchor, width, height, direction, BuildingType.MINER);
        this.worldMap = worldMap;

        List<ResourceType> occupiedOres = worldMap.getResources(getOccupiedTiles());
        for (ResourceType resource : occupiedOres){
            oreCoverage.put(resource, oreCoverage.getOrDefault(resource, 0) + 1);
            oreProgress.putIfAbsent(resource, 0);
        }
    }

    @Override
    public void update() {
        if (!outputBuffer.isEmpty()) {
//            System.out.println("TRY THROUGH " + outputBuffer.element());
            if (throughItem(outputBuffer.element(), 0.0f)) {
                System.out.println("NEW ITEM");
                outputBuffer.remove();
            }
        }
        if (outputBuffer.size() >= MAX_BUFFER_SIZE) {
            return;
        }
        for (var entry : oreCoverage.entrySet()) {
            ResourceType type = entry.getKey();
            int multiplier = entry.getValue();

            int currentProgress = oreProgress.get(type) + multiplier;

            float requiredTicks = BASE_TICKS_PER_ITEM * type.getMiningDifficulty();
            if (currentProgress >= requiredTicks) {
                ItemType extracted = type.getDrop();
                outputBuffer.add(extracted);
                currentProgress = 0;
            }
            oreProgress.put(type, currentProgress);
        }
    }

    @Override
    public boolean receiveItem(ItemType type, Float progress) {
        return false;
    }

    @Override
    public boolean canReceiveFromMe(Point point) {
        return true;
    }

    private Building getNextBuilding() {
        int nextCol = getX();
        int nextRow = getY() - 1;

        return registry.getBuildingAt(new Point(nextCol, nextRow));
    }

    @Override
    public boolean throughItem(ItemType type, Float progress) {
        Building nextBuilding = getNextBuilding();
        if (nextBuilding instanceof ReceiveItem building) {
//            System.out.println("try");
            return building.receiveItem(type, progress);
        }
        return false;
    }

    @Override
    public boolean canThroughInMe(Point point) {
        return true;
    }
}
