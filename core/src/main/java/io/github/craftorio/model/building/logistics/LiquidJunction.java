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

public class LiquidJunction extends DamageableBuilding implements ReceiveLiquid, ThroughLiquid {
    private static final float CAPACITY = BalanceConfig.PIPE_CAPACITY;
    private static final float THROUGHPUT = BalanceConfig.PIPE_THROUGHPUT;

    private final float[] amounts;
    private final LiquidType[] types;

    public LiquidJunction(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.LIQUID_JUNCTION);

        amounts = new float[4];
        types = new LiquidType[4];
    }

    private int dirToIndex(Direction dir) {
        switch (dir) {
            case UP: return 0;
            case RIGHT: return 1;
            case DOWN: return 2;
            case LEFT: return 3;
            default: return 0;
        }
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

            if (amounts[index] > 0f && types[index] != null) {
                float toPush = Math.min(THROUGHPUT, amounts[index]);
                float acceptedAmount = pushToNeighbor(dir, types[index], toPush);

                if (acceptedAmount > 0f) {
                    amounts[index] -= acceptedAmount;

                    if (amounts[index] <= 0.0001f) {
                        amounts[index] = 0f;
                        types[index] = null;
                    }
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

        Direction travelingDir = getDirectionFrom(from);
        int index = dirToIndex(travelingDir);

        if (types[index] != null && types[index] != type) {
            return 0f;
        }

        float availableSpace = CAPACITY - amounts[index];
        if (availableSpace <= 0f) {
            return 0f;
        }

        float accepted = Math.min(amount, availableSpace);
        types[index] = type;
        amounts[index] += accepted;

        return accepted;
    }

    @Override
    public boolean canReceiveLiquidFrom(Building from, Point point, LiquidType type) {
        if (from == null) return false;

        Direction travelingDir = getDirectionFrom(from);
        int index = dirToIndex(travelingDir);

        if (types[index] != null && types[index] != type) return false;
        return amounts[index] < CAPACITY;
    }

    @Override
    public float throughLiquid(LiquidType type, float amount) {
        return 0f;
    }

    @Override
    public boolean canThroughLiquidIn(Point point) {
        return true;
    }
}
