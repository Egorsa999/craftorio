package io.github.craftorio.model.building;

import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.ItemType;
import io.github.craftorio.model.generator.Cell;
import io.github.craftorio.model.WorldMap;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class Belt extends Building {
    private static final float speed = 1f / 60f;
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

    private Belt getNextBelt() {
        int nextCol = getX();
        int nextRow = getY();

        switch (direction) {
            case RIGHT: nextCol++; break;
            case LEFT:  nextCol--; break;
            case UP:    nextRow++; break;
            case DOWN:  nextRow--; break;
        }

        try {
            Building nextBuilding = registry.getBuildingAt(new Point(nextCol, nextRow));
            if (nextBuilding instanceof Belt nextBelt) {
                return nextBelt;
            }
            return null;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
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
                Belt nextBelt = getNextBelt();
                if (nextBelt != null && !nextBelt.itemProgress.isEmpty() && progressWill + ItemSize - 1.0f > nextBelt.itemProgress.getLast()) {
                    progressWill = 1.0f + nextBelt.itemProgress.getLast() - ItemSize;
                }
            }
            if (progressWill >= 1.0f) {
                if (tryTransferItem(itemId.get(i), progressWill - 1.0f)) {
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

    private boolean tryTransferItem(ItemType id, float progress) {
        Belt nextBelt = getNextBelt();
        if (nextBelt != null) {
            return nextBelt.acceptItem(id, progress);
        }
        return false;
    }

    public boolean acceptItem(ItemType id, float progress) {
        if (!itemId.isEmpty()) {
            if (itemProgress.getLast() < ItemSize) {
                return false;
            }
        }
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
