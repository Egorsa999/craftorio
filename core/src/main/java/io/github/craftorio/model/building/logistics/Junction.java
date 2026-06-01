package io.github.craftorio.model.building.logistics;

import io.github.craftorio.BalanceConfig;
import io.github.craftorio.GameConfig;
import io.github.craftorio.model.building.*;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.ItemType;

import java.awt.Point;
import java.util.ArrayList;

public class Junction extends DamageableBuilding implements ReceiveItem, ThroughItem {
    private static final int CAPACITY = 3;
    private static final int TRAVEL_TIME = 60;

    private final ArrayList<ItemType>[] buffers;
    private final ArrayList<Integer>[] timers;

    public Junction(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.JUNCTION);

        buffers = new ArrayList[4];
        timers = new ArrayList[4];
        for (int i = 0; i < 4; i++) {
            buffers[i] = new ArrayList<>();
            timers[i] = new ArrayList<>();
        }
    }

    private int dirToIndex(Direction dir) {
        switch (dir) {
            case UP: return 0;
            case RIGHT: return 1;
            case DOWN: return 2;
            case LEFT: return 3;
        }
        return 0;
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

    private Direction getDirectionFrom(Building sender) {
        if (sender == null) return Direction.UP;

        if (registry.getBuildingAt(getX() - 1, getY()) == sender) return Direction.RIGHT;
        if (registry.getBuildingAt(getX() + 1, getY()) == sender) return Direction.LEFT;
        if (registry.getBuildingAt(getX(), getY() - 1) == sender) return Direction.UP;
        if (registry.getBuildingAt(getX(), getY() + 1) == sender) return Direction.DOWN;

        return Direction.UP;
    }

    @Override
    public void update() {
        super.update();
        for (Direction dir : Direction.values()) {
            int index = dirToIndex(dir);
            ArrayList<ItemType> buffer = buffers[index];
            ArrayList<Integer> timerList = timers[index];

            for (int i = 0; i < timerList.size(); i++) {
                int t = timerList.get(i);
                if (t > 0) {
                    timerList.set(i, t - 1);
                }
            }

            if (!buffer.isEmpty() && timerList.get(0) <= 0) {
                ItemType item = buffer.get(0);

                if (pushToNeighbor(dir, item)) {
                    buffer.remove(0);
                    timerList.remove(0);
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
        if (building == null) return false;

        Direction travelingDir = getDirectionFrom(building);
        int index = dirToIndex(travelingDir);

        ArrayList<ItemType> buffer = buffers[index];
        ArrayList<Integer> timerList = timers[index];

        if (buffer.size() < CAPACITY) {
            buffer.add(item);
            timerList.add(TRAVEL_TIME);
            return true;
        }
        return false;
    }

    @Override
    public boolean canReceiveItemFrom(Building building, Point point) {
        return true;
    }

    @Override
    public boolean throughItem(ItemType type) {
        return false;
    }

    @Override
    public boolean canThroughItemIn(Point point) {
        return true;
    }
}
