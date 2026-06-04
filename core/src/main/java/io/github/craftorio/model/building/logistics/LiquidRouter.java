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

public class LiquidRouter extends DamageableBuilding implements ReceiveLiquid, ThroughLiquid, LiquidNetworkNode {
    private static final float CAPACITY = BalanceConfig.PIPE_CAPACITY;
    private static final float THROUGHPUT = BalanceConfig.PIPE_THROUGHPUT;

    private LiquidNetwork network;
    private float currentFill = 0f;
    private float prevFill = 0f;
    private LiquidType prevLiquidType;

    private int outputIndex = 0;
    private static final Direction[] DIRS = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};

    public LiquidRouter(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.LIQUID_ROUTER);
    }

    @Override
    public int getSubNetworksCount() { return 1; }
    @Override
    public LiquidNetwork getNetwork(int index) { return network; }
    @Override
    public void setNetwork(int index, LiquidNetwork network) { this.network = network; }
    @Override
    public float getCapacity(int index) { return CAPACITY; }
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
    public int getIndexForDirection(Direction dir) { return 0; }
    @Override
    public LiquidNetworkNode getLinkedNode(int index) { return null; }

    @Override
    public void update() {
        super.update();
        if (network != null && network.getCurrSystemAmount() > 0f) {
            LiquidType type = network.getLiquidType();
            if (type == null) return;

            for (int i = 0; i < 4; i++) {
                int checkIdx = (outputIndex + i) % 4;
                Direction dir = DIRS[checkIdx];

                int nx = getX(); int ny = getY();
                switch (dir) {
                    case RIGHT -> nx++; case LEFT -> nx--;
                    case UP -> ny++; case DOWN -> ny--;
                }

                Building b = registry.getBuildingAt(nx, ny);
                if (b instanceof ReceiveLiquid receiver && !(b instanceof LiquidNetworkNode)) {
                    if (receiver.canReceiveLiquidFrom(this, getAnchor(), type)) {
                        float toPush = Math.min(THROUGHPUT, network.getCurrSystemAmount());
                        float taken = network.takeLiquid(toPush);
                        if (taken > 0f) {
                            float accepted = receiver.receiveLiquid(this, type, taken);
                            if (accepted < taken) network.addLiquid(type, taken - accepted);
                            if (accepted > 0f) {
                                outputIndex = (checkIdx + 1) % 4;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public float receiveLiquid(Building from, LiquidType type, float amount) {
        if (network == null || amount <= 0f) return 0f;
        return network.addLiquid(type, amount);
    }

    @Override
    public boolean canReceiveLiquidFrom(Building from, Point point, LiquidType type) {
        if (network != null && network.getLiquidType() != null && network.getLiquidType() != type) return false;
        return true;
    }

    @Override
    public float throughLiquid(LiquidType type, float amount) { return 0f; }
    @Override
    public boolean canThroughLiquidIn(Point point) { return true; }

    public LiquidType getLiquidType() { return network != null ? network.getLiquidType() : null; }
    public float getCurrentAmount() { return currentFill * CAPACITY; }
    public float getCapacity() { return CAPACITY; }
}
