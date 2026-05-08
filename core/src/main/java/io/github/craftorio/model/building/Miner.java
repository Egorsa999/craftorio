package io.github.craftorio.model.building;

import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.ItemType;
import io.github.craftorio.model.generator.Cell;
import io.github.craftorio.model.WorldMap;
import io.github.craftorio.model.generator.ResourceType;

import java.awt.Point;
import java.util.*;

public class Miner extends Building  {

    private final WorldMap worldMap;

    private final Queue<ItemType> outputBuffer = new LinkedList<>();
    private final int MAX_BUFFER_SIZE = 10;

    private final Map<ResourceType, Integer> oreCoverage = new EnumMap<>(ResourceType.class);
    private final Map<ResourceType, Integer> oreProgress = new EnumMap<>(ResourceType.class);

    private final int BASE_TICKS_PER_ITEM = 120;

    public Miner(BuildingRegistry registry, WorldMap worldMap, Point anchor, int width, int height, Direction direction) {
        super(registry, anchor, width, height, direction);
        this.worldMap = worldMap;

        List<ResourceType> occupiedOres = worldMap.getResources(getOccupiedTiles());
        for (ResourceType resource : occupiedOres){
            oreCoverage.put(resource, oreCoverage.getOrDefault(resource, 0) + 1);
            oreProgress.putIfAbsent(resource, 0);
        }
    }

    @Override
    public void update() {
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
}
