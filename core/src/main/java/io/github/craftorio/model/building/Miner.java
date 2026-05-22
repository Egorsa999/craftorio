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

    private final ArrayList<Point> throughDelta = new ArrayList<>();
    private int lastThrough = 0;

    public Miner(BuildingRegistry registry, WorldMap worldMap, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.MINER);
        this.worldMap = worldMap;
        List<ResourceType> occupiedOres = worldMap.getResources(getOccupiedTiles());
        for (ResourceType resource : occupiedOres){
            if (resource == ResourceType.NONE || resource == null) continue;
            oreCoverage.put(resource, oreCoverage.getOrDefault(resource, 0) + 1);
            oreProgress.putIfAbsent(resource, 0);
        }
        throughDelta.add(new Point(+0, +2));
        throughDelta.add(new Point(+1, +2));
        throughDelta.add(new Point(+2, +1));
        throughDelta.add(new Point(+2, +0));
        throughDelta.add(new Point(+1, -1));
        throughDelta.add(new Point(+0, -1));
        throughDelta.add(new Point(-1, +0));
        throughDelta.add(new Point(-1, +1));
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
    public boolean canReceiveFrom(Point point) {
        return true;
    }

    private Building getNextBuilding() {
        int nextCol = getX();
        int nextRow = getY() - 1;

        return registry.getBuildingAt(nextCol, nextRow);
    }

    @Override
    public boolean throughItem(ItemType type, Float progress) {
        for (int iterate = 0; iterate <= throughDelta.size(); iterate++) {
            lastThrough++;
            lastThrough %= throughDelta.size();
            int x = getX() + throughDelta.get(lastThrough).x;
            int y = getY() + throughDelta.get(lastThrough).y;
            Building nextBuilding = registry.getBuildingAt(x, y);
            if (nextBuilding instanceof ReceiveItem building) {
                if (building.receiveItem(type, progress)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canThroughIn(Point point) {
        return true;
    }
}
