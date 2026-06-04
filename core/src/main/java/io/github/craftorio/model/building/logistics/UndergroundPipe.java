package io.github.craftorio.model.building.logistics;

import io.github.craftorio.BalanceConfig;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.DamageableBuilding;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.building.ReceiveLiquid;
import io.github.craftorio.model.building.ThroughLiquid;
import io.github.craftorio.model.building.liquid.LiquidNetwork;
import io.github.craftorio.model.building.liquid.LiquidNetworkNode;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.LiquidType;

import java.awt.Point;

public class UndergroundPipe extends DamageableBuilding implements ReceiveLiquid, ThroughLiquid, LiquidNetworkNode {
    private boolean isInput = false;
    private boolean isLinked = false;
    private UndergroundPipe linkedOut = null;
    private UndergroundPipe linkedIn = null;
    private int distance = 1;

    private LiquidNetwork network;
    private float currentFill = 0f;
    private float prevFill = 0f;
    private LiquidType prevLiquidType;

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

    @Override
    public int getSubNetworksCount() { return 1; }
    @Override
    public LiquidNetwork getNetwork(int index) { return network; }
    @Override
    public void setNetwork(int index, LiquidNetwork network) { this.network = network; }
    @Override
    public float getPrevFill(int index) { return prevFill; }
    @Override
    public LiquidType getPrevLiquidType(int index) { return prevLiquidType; }
    @Override
    public void savePrevFill() {
        this.prevFill = currentFill;
        this.prevLiquidType = network != null ? network.getLiquidType() : null;
    }
    @Override
    public void setCurrentFill(int index, float fill) { this.currentFill = fill; }
    @Override
    public float getCapacity(int index) { return getCapacity(); }

    @Override
    public int getIndexForDirection(Direction dir) {
        if (isInput) {
            return (dir != direction) ? 0 : -1;
        } else {
            return (dir != getOppositeDirection(direction)) ? 0 : -1;
        }
    }

    @Override
    public LiquidNetworkNode getLinkedNode(int index) {
        return getLinkedPipe();
    }

    public float getCapacity() {
        return BalanceConfig.PIPE_CAPACITY * Math.max(1, distance);
    }

    @Override
    public void update() {
        super.update();
        if (!isLinked || isInput || network == null || network.getCurrSystemAmount() <= 0f) return;

        LiquidType type = network.getLiquidType();
        if (type == null) return;

        Direction oppositeDir = getOppositeDirection(this.direction);

        for (Direction dir : Direction.values()) {
            if (dir == oppositeDir) continue;

            int nx = getX(); int ny = getY();
            switch (dir) {
                case RIGHT -> nx++; case LEFT -> nx--;
                case UP -> ny++; case DOWN -> ny--;
            }

            Building b = registry.getBuildingAt(nx, ny);
            if (b instanceof ReceiveLiquid receiver && !(b instanceof LiquidNetworkNode)) {
                if (receiver.canReceiveLiquidFrom(this, getAnchor(), type)) {
                    float toPush = Math.min(BalanceConfig.PIPE_THROUGHPUT, network.getCurrSystemAmount());
                    float taken = network.takeLiquid(toPush);
                    if (taken > 0f) {
                        float accepted = receiver.receiveLiquid(this, type, taken);
                        if (accepted < taken) network.addLiquid(type, taken - accepted);
                    }
                }
            }
        }
    }

    @Override
    public float receiveLiquid(Building from, LiquidType type, float amount) {
        if (!isLinked || network == null || amount <= 0f) return 0f;
        return network.addLiquid(type, amount);
    }

    @Override
    public boolean canReceiveLiquidFrom(Building from, Point point, LiquidType type) {
        if (!isLinked || !isInput) return false;
        if (network != null && network.getLiquidType() != null && network.getLiquidType() != type) return false;

        int frontCol = getX(); int frontRow = getY();
        switch (direction) {
            case RIGHT -> frontCol++; case LEFT -> frontCol--;
            case UP -> frontRow++; case DOWN -> frontRow--;
        }
        return point == null || point.x != frontCol || point.y != frontRow;
    }

    @Override
    public float throughLiquid(LiquidType type, float amount) { return 0f; }

    @Override
    public boolean canThroughLiquidIn(Point point) {
        if (!isLinked || isInput) return false;

        Direction oppositeDir = getOppositeDirection(this.direction);
        int backCol = getX(); int backRow = getY();
        switch (oppositeDir) {
            case RIGHT -> backCol++; case LEFT -> backCol--;
            case UP -> backRow++; case DOWN -> backRow--;
        }
        return point == null || point.x != backCol || point.y != backRow;
    }

    private Direction getOppositeDirection(Direction dir) {
        return switch (dir) { case UP -> Direction.DOWN; case DOWN -> Direction.UP; case LEFT -> Direction.RIGHT; case RIGHT -> Direction.LEFT; };
    }

    public UndergroundPipe getLinkedPipe() { return isInput ? linkedOut : linkedIn; }
    public boolean isInputPipe() { return isInput; }
    public LiquidType getLiquidType() { return network != null ? network.getLiquidType() : null; }
    public float getCurrentAmount() { return currentFill * getCapacity(); }
}
