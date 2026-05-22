package io.github.craftorio.model.building;

import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.ItemType;

import java.awt.Point;
import java.util.ArrayList;

public class Junction extends Building implements ReceiveItem, ThroughItem {
    private static final int CAPACITY = 3;
    private static final int TRAVEL_TIME = 60;

    private final ArrayList<ItemType>[] buffers;
    private final ArrayList<Integer>[] timers;

    public Junction(BuildingRegistry registry, Point anchor, Direction direction, BuildingType type) {
        super(registry, anchor, direction, type);

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
        return registry.getBuildingAt(new Point(nextCol, nextRow));
    }

    @Override
    public void update() {
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

        if (nextBuilding instanceof Belt nextBelt) {
            return nextBelt.receiveItem(id, 0.0f, dir);
        } else if (nextBuilding instanceof Junction nextJunc) {
            return nextJunc.receiveItem(id, 0.0f, dir);
        } else if (nextBuilding instanceof ReceiveItem building) {
            return building.receiveItem(id, 0.0f);
        }
        return false;
    }

    public boolean receiveItem(ItemType id, Float progress, Direction travelingDir) {
        int index = dirToIndex(travelingDir);
        ArrayList<ItemType> buffer = buffers[index];
        ArrayList<Integer> timerList = timers[index];

        if (buffer.size() < CAPACITY) {
            buffer.add(id);
            timerList.add(TRAVEL_TIME);
            return true;
        }
        return false;
    }

    // TODO fix it for all buildings
    @Override
    public boolean receiveItem(ItemType id, Float progress) {
        return false;
    }

    @Override
    public boolean canReceiveFrom(Point point) {
        return true;
    }

    @Override
    public boolean throughItem(ItemType type, Float progress) {
        return false;
    }

    @Override
    public boolean canThroughIn(Point point) {
        return true;
    }
}
