package io.github.craftorio.model.building;

import io.github.craftorio.GameConfig;
import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.ItemType;
import io.github.craftorio.model.generator.Cell;
import io.github.craftorio.model.WorldMap;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class Belt extends Building implements ThroughItem, ReceiveItem {
    // x * TICK_TIME = throughput is x item / per second
    private static final float speed = 2f * GameConfig.TICK_TIME;

    private static final float ItemSize = 0.3f;
    private static float animationOffset = 0.0f;

    // TODO replace by RingBuffer
    private ArrayList<ItemType> itemId;
    private ArrayList<Float> itemProgress;

    public Belt(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, BuildingType.BELT.getWidth(), BuildingType.BELT.getHeight(), direction);
        this.direction = direction;
        this.itemId = new ArrayList<>();
        this.itemProgress = new ArrayList<>();
    }

    public static void updateAnimationOffset(float delta) {
        animationOffset += speed * delta;
        animationOffset %= 1;
    }

    private Building getNextBuilding() {
        int nextCol = getX();
        int nextRow = getY();

        switch (direction) {
            case RIGHT: nextCol++; break;
            case LEFT:  nextCol--; break;
            case UP:    nextRow++; break;
            case DOWN:  nextRow--; break;
        }

        return registry.getBuildingAt(new Point(nextCol, nextRow));
    }

    @Override
    public void update() {
        if (!itemId.isEmpty()) System.out.println("Items for belt on col: " + getX() + "; row: " + getY() + ";");
        for (int i = 0; i < itemId.size(); i++) {
            System.out.println("         id: " + itemId.get(i) + "; progress: " + itemProgress.get(i) + ";");
            float progressWill = itemProgress.get(i) + speed;
            if (i > 0) {
                float progressPrev = itemProgress.get(i - 1);
                if (progressPrev - ItemSize < progressWill) {
                    progressWill = progressPrev - ItemSize;
                }
            } else {
                Building nextBuilding = getNextBuilding();
                if (nextBuilding instanceof Belt nextBelt) {
                    if (!nextBelt.itemProgress.isEmpty() && progressWill + ItemSize - 1.0f > nextBelt.itemProgress.getLast()) {
                        progressWill = 1.0f + nextBelt.itemProgress.getLast() - ItemSize;
                    }
                }
            }
            if (progressWill >= 1.0f) {
                if (throughItem(itemId.get(i), progressWill - 1.0f)) {
                    itemId.remove(i);
                    itemProgress.remove(i);
                    i--;
                    continue;
                } else {
                    progressWill = 1.0f;
                }
            }
            itemProgress.set(i, progressWill);
        }
    }

    public boolean throughItem(ItemType id, Float progress) {
        Building nextBuilding = getNextBuilding();
        if (nextBuilding instanceof ReceiveItem building) {
            return building.receiveItem(id, progress);
        }
        return false;
    }

    public boolean receiveItem(ItemType id, Float progress) {
        System.out.println("BELT TRY RECEIVE ITEM");
        if (!itemId.isEmpty()) {
            if (itemProgress.getLast() < ItemSize) {
                return false;
            }
        }
        System.out.println("BELT RECEIVED ITEM!!!");
        itemId.add(id);
        itemProgress.add(progress);
        return true;
    }

    public static float getAnimationOffset() {
        return animationOffset;
    }

    public ArrayList<ItemType> getItemId() {
        return this.itemId;
    }

    public ArrayList<Float> getItemProgress() {
        return this.itemProgress;
    }
}
