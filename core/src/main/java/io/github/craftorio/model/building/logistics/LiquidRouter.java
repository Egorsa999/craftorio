package io.github.craftorio.model.building.logistics;

import io.github.craftorio.BalanceConfig;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.DamageableBuilding;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.building.ReceiveLiquid;
import io.github.craftorio.model.building.ThroughLiquid;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.LiquidType;

import java.awt.Point;

public class LiquidRouter extends DamageableBuilding implements ReceiveLiquid, ThroughLiquid {
    private static final float CAPACITY = BalanceConfig.PIPE_CAPACITY;
    private static final float THROUGHPUT = BalanceConfig.PIPE_THROUGHPUT;

    private LiquidType currentType = null;
    private float currentAmount = 0f;
    private int outputIndex = 0;

    private static final Direction[] DIRS = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};

    public LiquidRouter(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.LIQUID_ROUTER);
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
        if (currentType != null && currentAmount > 0f) {
            for (int i = 0; i < 4; i++) {
                int checkIdx = (outputIndex + i) % 4;
                Direction checkDir = DIRS[checkIdx];

                float toPush = Math.min(THROUGHPUT, currentAmount);
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
        Building nextBuilding = getNeighbor(dir);

        if (nextBuilding instanceof ReceiveLiquid building) {
            return building.receiveLiquid(this, type, amount);
        }
        return 0f;
    }

    @Override
    public float receiveLiquid(Building from, LiquidType type, float amount) {
        if (from == null || amount <= 0f) return 0f;
        if (currentType != null && currentType != type) return 0f;

        float availableSpace = CAPACITY - currentAmount;
        if (availableSpace <= 0f) return 0f;

        float accepted = Math.min(amount, availableSpace);
        currentType = type;
        currentAmount += accepted;

        return accepted;
    }

    @Override
    public boolean canReceiveLiquidFrom(Building from, Point point, LiquidType type) {
        if (currentType != null && currentType != type) return false;
        return currentAmount < CAPACITY;
    }

    @Override
    public float throughLiquid(LiquidType type, float amount) {
        return 0f;
    }

    @Override
    public boolean canThroughLiquidIn(Point point) {
        return true;
    }

    public LiquidType getLiquidType() {
        return currentType;
    }

    public float getCurrentAmount() {
        return currentAmount;
    }

    public float getCapacity() {
        return CAPACITY;
    }
}
