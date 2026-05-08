package io.github.craftorio.model.building;

import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.generator.Cell;
import io.github.craftorio.model.WorldMap;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Miner extends Building  {

    private final WorldMap worldMap;

    private final Queue<Cell.resourceType> outputBuffer = new LinkedList<>();
    private final int MAX_BUFFER_SIZE = 5;


    private final List<Cell.resourceType> occupiedOres;
    private final

    public Miner(BuildingRegistry registry, WorldMap worldMap, Point anchor, int width, int height) {
        super(registry, anchor, width, height);
        this.worldMap = worldMap;
        this.tickCounter = 0;

        occupiedOres = worldMap.getResources(getOccupiedTiles());
    }

    @Override
    public void update() {

    }
}
