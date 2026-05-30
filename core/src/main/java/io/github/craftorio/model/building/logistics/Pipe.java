package io.github.craftorio.model.building.logistics;

import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.DamageableBuilding;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.building.ReceiveLiquid;
import io.github.craftorio.model.building.ThroughLiquid;
import io.github.craftorio.model.building.liquid.LiquidNetwork;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.LiquidType;

import java.awt.Point;

public class Pipe extends DamageableBuilding implements ThroughLiquid, ReceiveLiquid {
    private static final float CAPACITY = 10f;
    private static final float THROUGHPUT = 10f;

    private LiquidNetwork network;
    private float currentFill = 0f;
    private float prevFill = 0f;
    private LiquidType prevLiquidType;

    private int pipeType;
    private float rotation;
    private float reflection;

    public Pipe(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.PIPE);
        this.direction = direction;
        this.pipeType = 0;
        this.rotation = dirToInt(direction) * 90f;
        this.reflection = 1f;
        updateType();
    }

    @Override
    public float receiveLiquid(Building from, LiquidType type, float amount) {
        if (network == null || amount <= 0f || !canReceiveLiquidFrom(from, from.getAnchor())) {
            return 0f;
        }
        return network.addLiquid(type, amount);
    }

    @Override
    public boolean canReceiveLiquidFrom(Building from, Point point) {
        return from != null && from != getOutputBuilding();
    }

    @Override
    public float throughLiquid(LiquidType type, float amount) {
        if (amount <= 0f || network == null || !network.canAcceptLiquid(type)) {
            return 0f;
        }

        Building next = getOutputBuilding();
        if (next instanceof ReceiveLiquid receiver) {
            return receiver.receiveLiquid(this, type, amount);
        }
        return 0f;
    }

    @Override
    public boolean canThroughLiquidIn(Point point) {
        Point output = getOutputPoint();
        return output.equals(point);
    }

    public LiquidNetwork getNetwork() {
        return network;
    }

    public void setNetwork(LiquidNetwork network) {
        this.network = network;
    }

    public void setCurrentFill(float fill) {
        this.currentFill = fill;
    }

    public float getCurrentFill() {
        return currentFill;
    }

    public void savePrevFill() {
        this.prevFill = currentFill;
        this.prevLiquidType = network != null ? network.getLiquidType() : null;
    }

    public LiquidType getPrevLiquidType() {
        return prevLiquidType;
    }

    public float getPrevFill() {
        return prevFill;
    }

    public float getLiquidCapacity() {
        return CAPACITY;
    }

    public float getThroughput() {
        return THROUGHPUT;
    }

    @Override
    public void setAnchor(int x, int y) {
        super.setAnchor(x, y);
        updateType();
    }

    public Direction getFlowDirection() {
        return direction;
    }

    private Point getOutputPoint() {
        int x = getX();
        int y = getY();
        return switch (direction) {
            case RIGHT -> new Point(x + 1, y);
            case LEFT -> new Point(x - 1, y);
            case UP -> new Point(x, y + 1);
            case DOWN -> new Point(x, y - 1);
        };
    }

    private Building getOutputBuilding() {
        Point output = getOutputPoint();
        return registry.getBuildingAt(output.x, output.y);
    }

    private boolean flowsInFrom(Direction side) {
        Building building = getNeighborBuilding(side);
        if (!(building instanceof ThroughLiquid source)) {
            return false;
        }
        return source.canThroughLiquidIn(new Point(getX(), getY()));
    }

    private Building getNeighborBuilding(Direction side) {
        int x = getX();
        int y = getY();
        return switch (side) {
            case UP -> registry.getBuildingAt(x, y + 1);
            case DOWN -> registry.getBuildingAt(x, y - 1);
            case RIGHT -> registry.getBuildingAt(x + 1, y);
            case LEFT -> registry.getBuildingAt(x - 1, y);
        };
    }

    private Direction perpendicularLeft() {
        return switch (direction) {
            case UP -> Direction.LEFT;
            case RIGHT -> Direction.UP;
            case DOWN -> Direction.RIGHT;
            case LEFT -> Direction.DOWN;
        };
    }

    private Direction perpendicularRight() {
        return switch (direction) {
            case UP -> Direction.RIGHT;
            case RIGHT -> Direction.DOWN;
            case DOWN -> Direction.LEFT;
            case LEFT -> Direction.UP;
        };
    }

    private int dirToInt(Direction dir) {
        return switch (dir) {
            case UP -> 0;
            case RIGHT -> 1;
            case DOWN -> 2;
            case LEFT -> 3;
        };
    }

    private void updateType() {
        boolean downIn = flowsInFrom(direction.opposite());
        boolean leftIn = flowsInFrom(perpendicularLeft());
        boolean rightIn = flowsInFrom(perpendicularRight());

        if (downIn && leftIn && rightIn) {
            pipeType = 3;
            rotation = dirToInt(direction) * 90f;
            reflection = 1f;
            return;
        }

        if (downIn) {
            if (rightIn) {
                pipeType = 2;
                rotation = dirToInt(direction) * 90f;
                reflection = 1f;
                return;
            }
            if (leftIn) {
                pipeType = 2;
                rotation = dirToInt(direction) * 90f;
                reflection = -1f;
                return;
            }
        }

        if (leftIn && rightIn) {
            pipeType = 4;
            rotation = dirToInt(direction) * 90f;
            reflection = 1f;
            return;
        }

        if (leftIn) {
            pipeType = 1;
            rotation = dirToInt(direction) * 90f;
            reflection = 1f;
            return;
        }
        if (rightIn) {
            pipeType = 1;
            rotation = dirToInt(direction) * 90f;
            reflection = -1f;
            return;
        }

        pipeType = 0;
        rotation = dirToInt(direction) * 90f;
        reflection = 1f;
    }

    @Override
    public void update() {
        super.update();
        updateType();
        pushLiquidToOutput();
    }

    private void pushLiquidToOutput() {
        if (network == null) {
            return;
        }

        LiquidType type = network.getLiquidType();
        if (type == null || network.getCurrSystemAmount() <= 0f) {
            return;
        }

        Building output = getOutputBuilding();
        if (output instanceof Pipe || !(output instanceof ReceiveLiquid receiver)) {
            return;
        }

        if (!receiver.canReceiveLiquidFrom(this, getAnchor())) {
            return;
        }

        float toPush = Math.min(THROUGHPUT, network.getCurrSystemAmount());

        float taken = network.takeLiquid(toPush);
        if (taken <= 0f) {
            return;
        }

        float accepted = receiver.receiveLiquid(this, type, taken);
        if (accepted < taken) {
            network.addLiquid(type, taken - accepted);
        }
    }

    public int getPipeType() {
        return pipeType;
    }

    public float getRotation() {
        return rotation;
    }

    public float getReflection() {
        return reflection;
    }
}
