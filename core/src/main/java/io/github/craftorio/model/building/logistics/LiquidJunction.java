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

public class LiquidJunction extends DamageableBuilding implements ReceiveLiquid, ThroughLiquid, LiquidNetworkNode {
    private static final float CAPACITY = BalanceConfig.PIPE_CAPACITY;
    private static final float THROUGHPUT = BalanceConfig.PIPE_THROUGHPUT;

    private final LiquidNetwork[] networks = new LiquidNetwork[2];
    private final float[] currentFills = new float[2];
    private final float[] prevFills = new float[2];
    private final LiquidType[] prevTypes = new LiquidType[2];

    public LiquidJunction(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.LIQUID_JUNCTION);
    }

    @Override
    public int getSubNetworksCount() { return 2; }
    @Override
    public LiquidNetwork getNetwork(int index) { return networks[index]; }
    @Override
    public void setNetwork(int index, LiquidNetwork network) { networks[index] = network; }
    @Override
    public float getCapacity(int index) { return CAPACITY; }
    @Override
    public float getPrevFill(int index) { return prevFills[index]; }
    @Override
    public LiquidType getPrevLiquidType(int index) { return prevTypes[index]; }

    @Override
    public void savePrevFill() {
        for (int i = 0; i < 2; i++) {
            prevFills[i] = currentFills[i];
            prevTypes[i] = networks[i] != null ? networks[i].getLiquidType() : null;
        }
    }

    @Override
    public void setCurrentFill(int index, float fill) { currentFills[index] = fill; }

    @Override
    public int getIndexForDirection(Direction dir) {
        return (dir == Direction.LEFT || dir == Direction.RIGHT) ? 0 : 1;
    }

    @Override
    public LiquidNetworkNode getLinkedNode(int index) { return null; }

    @Override
    public void update() {
        super.update();
        for (Direction dir : Direction.values()) {
            int netIndex = getIndexForDirection(dir);
            LiquidNetwork net = networks[netIndex];

            if (net == null || net.getCurrSystemAmount() <= 0f) continue;
            LiquidType type = net.getLiquidType();
            if (type == null) continue;

            int nx = getX(); int ny = getY();
            switch (dir) {
                case RIGHT -> nx++; case LEFT -> nx--;
                case UP -> ny++; case DOWN -> ny--;
            }

            Building b = registry.getBuildingAt(nx, ny);
            if (b instanceof ReceiveLiquid receiver && !(b instanceof LiquidNetworkNode)) {
                if (receiver.canReceiveLiquidFrom(this, getAnchor(), type)) {
                    float toPush = Math.min(THROUGHPUT, net.getCurrSystemAmount());
                    float taken = net.takeLiquid(toPush);
                    if (taken > 0f) {
                        float accepted = receiver.receiveLiquid(this, type, taken);
                        if (accepted < taken) net.addLiquid(type, taken - accepted);
                    }
                }
            }
        }
    }

    private Direction getDirectionFrom(Building sender) {
        if (sender == null) return Direction.UP;
        if (registry.getBuildingAt(getX() - 1, getY()) == sender) return Direction.LEFT;
        if (registry.getBuildingAt(getX() + 1, getY()) == sender) return Direction.RIGHT;
        if (registry.getBuildingAt(getX(), getY() - 1) == sender) return Direction.DOWN;
        if (registry.getBuildingAt(getX(), getY() + 1) == sender) return Direction.UP;
        return Direction.UP;
    }

    @Override
    public float receiveLiquid(Building from, LiquidType type, float amount) {
        if (from == null || amount <= 0f) return 0f;
        Direction fromDir = getDirectionFrom(from);
        int netIndex = getIndexForDirection(fromDir);
        LiquidNetwork net = networks[netIndex];
        if (net == null) return 0f;
        return net.addLiquid(type, amount);
    }

    @Override
    public boolean canReceiveLiquidFrom(Building from, Point point, LiquidType type) {
        if (from == null) return false;
        int netIndex = getIndexForDirection(getDirectionFrom(from));
        LiquidNetwork net = networks[netIndex];
        if (net != null && net.getLiquidType() != null && net.getLiquidType() != type) return false;
        return true;
    }

    @Override
    public float throughLiquid(LiquidType type, float amount) { return 0f; }
    @Override
    public boolean canThroughLiquidIn(Point point) { return true; }
}
