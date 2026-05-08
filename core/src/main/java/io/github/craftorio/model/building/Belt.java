package io.github.craftorio.model.building;

import io.github.craftorio.model.Cell;
import io.github.craftorio.model.WorldMap;

import java.util.ArrayList;
import java.util.Map;

public class Belt extends Building {
    public Direction direction;

    private static final float speed = 1.0f;
    private static final float ItemSize = 0.3f;
    private static float animationOffset = 0.0f;

    // TODO replace by RingBuffer
    private ArrayList<Integer> itemId;
    private ArrayList<Float> itemProgress;

    public Belt(WorldMap worldMap, int col, int row, Direction direction) {
        super(worldMap, col, row, 1, 1);
        this.direction = direction;
        this.itemId = new ArrayList<>();
        this.itemProgress = new ArrayList<>();
    }

    public static void updateAnimationOffset(float delta) {
        animationOffset += speed * delta;
        animationOffset %= 1;
    }

    private Belt getNextBelt() {
        int nextCol = getCol();
        int nextRow = getRow();

        switch (direction) {
            case RIGHT: nextCol++; break;
            case LEFT:  nextCol--; break;
            case UP:    nextRow++; break;
            case DOWN:  nextRow--; break;
        }

        try {
            Cell nextCell = worldMap.getCell(nextCol, nextRow);
            if (nextCell != null && nextCell.getOccupiedBuilding() instanceof Belt nextBelt) {
                return nextBelt;
            }
            return null;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public void update(float delta) {
        float moveDistance = speed * delta;
        if (!itemId.isEmpty()) System.out.println("Items for belt on col: " + getCol() + "; row: " + getRow() + ";");
        for (int i = 0; i < itemId.size(); i++) {
            System.out.println("         id: " + itemId.get(i) + "; progress: " + itemProgress.get(i) + ";");
            float progressWill = itemProgress.get(i) + moveDistance;
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

    private boolean tryTransferItem(Integer id, float progress) {
        Belt nextBelt = getNextBelt();
        if (nextBelt != null) {
            return nextBelt.acceptItem(id, progress);
        }
        return false;
    }

    public boolean acceptItem(Integer id, float progress) {
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

    public ArrayList<Integer> getItemId() {
        return this.itemId;
    }

    public ArrayList<Float> getItemProgress() {
        return this.itemProgress;
    }
}
