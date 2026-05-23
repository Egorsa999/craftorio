package io.github.craftorio.model.building.production;

import io.github.craftorio.model.building.*;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.ItemType;

import java.awt.*;

public class HorizontalMiner extends DamageableBuilding implements ThroughItem {

    private final int BASE_TICKS_PER_ITEM = 240;
    private final int MAX_BUFFER_SIZE = 10;

    private int bufferCounter;
    private int currentProgress;

    public HorizontalMiner(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.HORIZONTAL_MINER);
        bufferCounter = 0;
        currentProgress = 0;
    }

    private Building getNextBuilding() {
        int nextCol = rotatePoint(0, -1).x;
        int nextRow = rotatePoint(0, -1).y;

        return registry.getBuildingAt(nextCol, nextRow);
    }

    @Override
    public void update(){
        super.update();
        if (bufferCounter != 0) {
            if (throughItem(ItemType.IRON_ORE)) {
                System.out.println("NEW ITEM");
            }
            bufferCounter--;
        }
        if (bufferCounter >= MAX_BUFFER_SIZE) return;
        currentProgress++;
        if (currentProgress >= BASE_TICKS_PER_ITEM){
            currentProgress = 0;
            bufferCounter++;
        }
    }

    @Override
    public boolean throughItem(ItemType type) {
        Building nextBuilding = getNextBuilding();
        if (nextBuilding instanceof ReceiveItem building) {
            return building.receiveItem(this, type);
        }
        return false;
    }

    @Override
    public boolean canThroughIn(Point point) {
        return point.equals(rotatePoint(0, -1));
    }
}
