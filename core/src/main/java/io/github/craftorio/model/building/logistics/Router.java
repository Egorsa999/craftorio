package io.github.craftorio.model.building.logistics;

import io.github.craftorio.model.building.*;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.ItemType;

import java.awt.Point;

public class Router extends DamageableBuilding implements ReceiveItem, ThroughItem {
    private static final int PROCESS_TIME = 15;

    private ItemType currentItem = null;
    private int timer = 0;

    private int outputIndex = 0;

    private static final Direction[] DIRS = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};

    public Router(BuildingRegistry registry, Point anchor, Direction direction, BuildingType type) {
        super(registry, anchor, direction, type);
    }

    private Building getNeighbor(Direction dir) {
        int nextCol = getX();
        int nextRow = getY();

        switch (dir) {
            case RIGHT: nextCol++; break;
            case LEFT:  nextCol--; break;
            case UP:    nextRow++; break;
            case DOWN:  nextRow--; break;
        }
        return registry.getBuildingAt(nextCol, nextRow);
    }

    @Override
    public void update() {
        super.update();
        if (currentItem != null) {
            if (timer > 0) {
                timer--;
            } else {
                for (int i = 0; i < 4; i++) {
                    int checkIdx = (outputIndex + i) % 4;
                    Direction checkDir = DIRS[checkIdx];

                    if (pushToNeighbor(checkDir, currentItem)) {
                        currentItem = null;
                        outputIndex = (checkIdx + 1) % 4;
                        break;
                    }
                }
            }
        }
    }

    private boolean pushToNeighbor(Direction dir, ItemType id) {
        Building nextBuilding = getNeighbor(dir);

        if (nextBuilding instanceof ReceiveItem building) {
            return building.receiveItem(this, id);
        }
        return false;
    }

    @Override
    public boolean receiveItem(Building building, ItemType item) {
        if (currentItem == null) {
            currentItem = item;
            timer = PROCESS_TIME;
            return true;
        }
        return false;
    }

    @Override
    public boolean canReceiveFrom(Building building, Point point) {
        return true;
    }

    @Override
    public boolean throughItem(ItemType type) {
        return false;
    }

    @Override
    public boolean canThroughIn(Point point) {
        return true;
    }
}
