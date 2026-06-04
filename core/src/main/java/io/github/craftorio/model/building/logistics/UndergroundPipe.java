package io.github.craftorio.model.building.logistics;

import io.github.craftorio.BalanceConfig;
import io.github.craftorio.model.building.*;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.LiquidType;

import java.awt.Point;

public class UndergroundPipe extends DamageableBuilding implements ReceiveLiquid, ThroughLiquid {
    private boolean isInput = false;
    private boolean isLinked = false;
    private UndergroundPipe linkedOut = null;
    private UndergroundPipe linkedIn = null;
    private int distance = 1;

    private float currentAmount = 0f;
    private LiquidType currentType = null;

    private int outputIndex = 0;
    private static final Direction[] DIRS = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};

    public UndergroundPipe(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.UNDERGROUND_PIPE);
    }

    public void link(UndergroundPipe outPipe, int distance, Direction dir) {
        this.isInput = true;
        this.isLinked = true;
        this.linkedOut = outPipe;
        this.distance = distance;
        this.direction = dir;

        outPipe.isInput = false;
        outPipe.isLinked = true;
        outPipe.linkedIn = this;
        outPipe.direction = dir;
    }

    public float getCapacity() {
        return BalanceConfig.PIPE_CAPACITY * Math.max(1, distance);
    }

    @Override
    public void update() {
        super.update();
        if (!isLinked || currentAmount <= 0f || currentType == null) return;

        float toPush = Math.min(BalanceConfig.PIPE_THROUGHPUT, currentAmount);

        if (isInput) {
            float accepted = linkedOut.receiveLiquid(this, currentType, toPush);
            if (accepted > 0f) {
                currentAmount -= accepted;
                if (currentAmount <= 0.0001f) {
                    currentAmount = 0f;
                    currentType = null;
                }
            }
        } else {
            Direction oppositeDir = getOppositeDirection(this.direction);

            for (int i = 0; i < 4; i++) {
                int checkIdx = (outputIndex + i) % 4;
                Direction checkDir = DIRS[checkIdx];

                if (checkDir == oppositeDir) continue;

                float accepted = pushToNeighbor(checkDir, currentType, toPush);
                if (accepted > 0f) {
                    currentAmount -= accepted;
                    if (currentAmount <= 0.0001f) {
                        currentAmount = 0f;
                        currentType = null;
                    }

                    outputIndex = (checkIdx + 1) % 4;
                    break;
                }
            }
        }
    }

    private float pushToNeighbor(Direction dir, LiquidType type, float amount) {
        int nextCol = getX();
        int nextRow = getY();

        switch (dir) {
            case RIGHT: nextCol++; break;
            case LEFT:  nextCol--; break;
            case UP:    nextRow++; break;
            case DOWN:  nextRow--; break;
        }

        Building nextBuilding = registry.getBuildingAt(nextCol, nextRow);
        if (nextBuilding instanceof ReceiveLiquid building) {
            return building.receiveLiquid(this, type, amount);
        }
        return 0f;
    }

    @Override
    public float receiveLiquid(Building from, LiquidType type, float amount) {
        if (!isLinked || amount <= 0f) return 0f;

        if (isInput) {
            if (currentType != null && currentType != type) return 0f;

            float availableSpace = getCapacity() - currentAmount;
            if (availableSpace <= 0f) return 0f;

            float accepted = Math.min(amount, availableSpace);
            currentType = type;
            currentAmount += accepted;

            return accepted;
        } else {
            if (from != linkedIn) return 0f;

            if (currentType != null && currentType != type) return 0f;

            float availableSpace = getCapacity() - currentAmount;
            if (availableSpace <= 0f) return 0f;

            float accepted = Math.min(amount, availableSpace);
            currentType = type;
            currentAmount += accepted;

            return accepted;
        }
    }

    @Override
    public boolean canReceiveLiquidFrom(Building from, Point point, LiquidType type) {
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

        if (point != null && point.x == frontCol && point.y == frontRow) return false;

        return true;
    }

    @Override
    public float throughLiquid(LiquidType type, float amount) {
        return 0f;
    }

    @Override
    public boolean canThroughLiquidIn(Point point) {
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

        if (point != null && point.x == backCol && point.y == backRow) return false;

        return true;
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

    public UndergroundPipe getLinkedPipe() {
        return isInput ? linkedOut : linkedIn;
    }

    public boolean isInputPipe() {
        return isInput;
    }

    public LiquidType getLiquidType() {
        return currentType;
    }

    public float getCurrentAmount() {
        return currentAmount;
    }
}
