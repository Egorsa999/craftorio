package io.github.craftorio.model.building;

import io.github.craftorio.GameConfig;
import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.ItemType;

import java.awt.*;
import java.util.ArrayList;

public class Belt extends Building implements ThroughItem, ReceiveItem {
    private static final float speed = 1f * GameConfig.TICK_TIME;
    private static final float ItemSize = 1f / 3f;
    private static float animationOffset = 0.0f;

    private ArrayList<ItemType> itemId;
    private ArrayList<Float> itemProgress;
    private ArrayList<Direction> itemFrom;

    public Belt(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, BuildingType.BELT.getWidth(), BuildingType.BELT.getHeight(), direction);
        this.direction = direction;
        this.itemId = new ArrayList<>();
        this.itemProgress = new ArrayList<>();
        this.itemFrom = new ArrayList<>();
    }

    public static void updateAnimationOffset(float delta) {
        animationOffset += speed * delta;
        animationOffset %= 1;
    }

    public static float getAnimationOffset() {
        return animationOffset;
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
        for (int i = 0; i < itemId.size(); i++) {
            float progressWill = itemProgress.get(i) + speed;

            if (i > 0) {
                float progressPrev = itemProgress.get(i - 1);
                if (progressPrev - ItemSize < progressWill) {
                    progressWill = progressPrev - ItemSize;
                }
            } else {
                Building nextBuilding = getNextBuilding();
                if (nextBuilding instanceof Belt nextBelt) {
                    if (!nextBelt.itemProgress.isEmpty()) {
                        float tailProgress = nextBelt.itemProgress.getLast();
                        if (progressWill - 1.0f + ItemSize > tailProgress) {
                            progressWill = 1.0f + tailProgress - ItemSize;
                        }
                    }
                }
            }

            progressWill = Math.max(itemProgress.get(i), progressWill);

            if (progressWill >= 1.0f) {
                if (throughItem(i, itemId.get(i), progressWill - 1.0f)) {
                    itemId.remove(i);
                    itemProgress.remove(i);
                    itemFrom.remove(i);
                    i--;
                    continue;
                } else {
                    progressWill = 1.0f;
                }
            }
            if (progressWill - itemProgress.get(i) <= speed / 1.5f) continue;
            itemProgress.set(i, progressWill);
        }
    }

    public boolean throughItem(int index, ItemType id, Float progress) {
        Building nextBuilding = getNextBuilding();
        if (nextBuilding instanceof Belt nextBelt) {
            return nextBelt.receiveItem(id, progress, this.direction);
        } else if (nextBuilding instanceof ReceiveItem building) {
            return building.receiveItem(id, progress);
        }
        return false;
    }

    @Override
    public boolean throughItem(ItemType id, Float progress) { return false; }

    @Override
    public boolean receiveItem(ItemType id, Float progress) {
        return receiveItem(id, progress, this.direction);
    }

    public boolean receiveItem(ItemType id, Float progress, Direction from) {
        if (itemId.isEmpty()) {
            itemId.add(id);
            itemProgress.add(progress);
            itemFrom.add(from);
            return true;
        }

        int insertIdx = itemId.size();
        for (int i = 0; i < itemProgress.size(); i++) {
            if (progress > itemProgress.get(i)) {
                insertIdx = i;
                break;
            }
        }

        if (insertIdx > 0 && itemProgress.get(insertIdx - 1) - ItemSize < progress) return false;
        if (insertIdx < itemProgress.size() && progress - ItemSize < itemProgress.get(insertIdx)) return false;

        itemId.add(insertIdx, id);
        itemProgress.add(insertIdx, progress);
        itemFrom.add(insertIdx, from);
        return true;
    }

    public ArrayList<ItemType> getItemId() { return this.itemId; }
    public ArrayList<Float> getItemProgress() { return this.itemProgress; }
    public ArrayList<Direction> getItemFrom() { return this.itemFrom; }
}
