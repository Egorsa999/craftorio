package io.github.craftorio.model.building.logistics;

import io.github.craftorio.BalanceConfig;
import io.github.craftorio.GameConfig;
import io.github.craftorio.model.building.*;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.ItemType;

import java.awt.Point;
import java.util.ArrayList;

public class UndergroundBelt extends DamageableBuilding implements ReceiveItem, ThroughItem {
    private boolean isInput = false;
    private boolean isLinked = false;
    private UndergroundBelt linkedOut = null;
    private UndergroundBelt linkedIn = null;
    private int distance = 1;

    private static final float speed = BalanceConfig.CONVEYOR_SPEED * GameConfig.TICK_TIME;
    private static final float ItemSize = BalanceConfig.CONVEYOR_ITEM_SIZE;
    private final ArrayList<ItemType> itemQueue = new ArrayList<>();
    private final ArrayList<Float> progressQueue = new ArrayList<>();

    public ItemType currentItem = null;
    private int timer = 0;
    private int outputIndex = 0;
    private static final Direction[] DIRS = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};

    public UndergroundBelt(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.UNDERGROUND_BELT);
    }

    public void link(UndergroundBelt outBelt, int distance, Direction dir) {
        this.isInput = true;
        this.isLinked = true;
        this.linkedOut = outBelt;
        this.distance = distance;
        this.direction = dir;

        outBelt.isInput = false;
        outBelt.isLinked = true;
        outBelt.linkedIn = this;
        outBelt.direction = dir;
    }

    @Override
    public void update() {
        super.update();
        if (!isLinked) return;

        if (isInput) {
            for (int i = 0; i < itemQueue.size(); i++) {
                float progressWill = progressQueue.get(i) + speed;

                if (i > 0) {
                    float progressPrev = progressQueue.get(i - 1);
                    if (progressPrev - ItemSize < progressWill) {
                        progressWill = progressPrev - ItemSize;
                    }
                } else {
                    if (linkedOut.currentItem != null) {
                        if (progressWill > distance - ItemSize) {
                            progressWill = distance - ItemSize;
                        }
                    }
                }

                progressWill = Math.max(progressQueue.get(i), progressWill);

                if (progressWill >= distance) {
                    if (linkedOut.receiveItem(this, itemQueue.get(i))) {
                        itemQueue.remove(i);
                        progressQueue.remove(i);
                        i--;
                        continue;
                    } else {
                        progressWill = distance;
                    }
                }

                if (progressWill - progressQueue.get(i) > speed / 1.5f) {
                    progressQueue.set(i, progressWill);
                }
            }
        } else {
            if (currentItem != null) {
                if (timer > 0) {
                    timer--;
                } else {
                    Direction oppositeDir = getOppositeDirection(this.direction);
                    for (int i = 0; i < 4; i++) {
                        int checkIdx = (outputIndex + i) % 4;
                        Direction checkDir = DIRS[checkIdx];

                        if (checkDir == oppositeDir) continue;

                        if (pushToNeighbor(checkDir, currentItem)) {
                            currentItem = null;
                            outputIndex = (checkIdx + 1) % 4;
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean receiveItem(Building sender, ItemType item) {
        if (!isLinked) return false;

        if (isInput) {
            float startProgress = 0.0f;

            int insertIdx = itemQueue.size();
            for (int i = 0; i < progressQueue.size(); i++) {
                if (startProgress > progressQueue.get(i)) {
                    insertIdx = i;
                    break;
                }
            }

            if (insertIdx > 0 && progressQueue.get(insertIdx - 1) - ItemSize < startProgress) return false;
            if (insertIdx < progressQueue.size() && startProgress - ItemSize < progressQueue.get(insertIdx)) return false;

            itemQueue.add(insertIdx, item);
            progressQueue.add(insertIdx, startProgress);
            return true;
        } else {
            if (sender != linkedIn) return false;

            if (currentItem == null) {
                currentItem = item;
                timer = BalanceConfig.ROUTER_SPEED;
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean canReceiveItemFrom(Building building, Point point) {
        if (!isLinked) return false;

        if (!isInput) return false;

        int frontCol = getX();
        int frontRow = getY();
        switch (direction) {
            case RIGHT: frontCol++; break;
            case LEFT:  frontCol--; break;
            case UP:    frontRow++; break;
            case DOWN:  frontRow--; break;
        }

        if (point != null && point.x == frontCol && point.y == frontRow) {
            return false;
        }

        return true;
    }

    @Override
    public boolean throughItem(ItemType type) {
        return false;
    }

    @Override
    public boolean canThroughItemIn(Point point) {
        if (!isLinked) return false;

        if (isInput) return false;

        Direction oppositeDir = getOppositeDirection(this.direction);
        int backCol = getX();
        int backRow = getY();
        switch (oppositeDir) {
            case RIGHT: backCol++; break;
            case LEFT:  backCol--; break;
            case UP:    backRow++; break;
            case DOWN:  backRow--; break;
        }

        if (point != null && point.x == backCol && point.y == backRow) {
            return false;
        }

        return true;
    }

    private boolean pushToNeighbor(Direction dir, ItemType id) {
        int nextCol = getX();
        int nextRow = getY();

        switch (dir) {
            case RIGHT: nextCol++; break;
            case LEFT:  nextCol--; break;
            case UP:    nextRow++; break;
            case DOWN:  nextRow--; break;
        }

        Building nextBuilding = registry.getBuildingAt(nextCol, nextRow);
        if (nextBuilding instanceof ReceiveItem building) {
            return building.receiveItem(this, id);
        }
        return false;
    }

    private Direction getOppositeDirection(Direction dir) {
        switch (dir) {
            case UP: return Direction.DOWN;
            case DOWN: return Direction.UP;
            case LEFT: return Direction.RIGHT;
            case RIGHT: return Direction.LEFT;
            default: return Direction.DOWN;
        }
    }

    public UndergroundBelt getLinkedBelt() {
        return isInput ? linkedOut : linkedIn;
    }

    public boolean isInputBelt() {
        return isInput;
    }
}
