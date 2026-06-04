package io.github.craftorio.model.building.liquid;

import io.github.craftorio.model.item.LiquidType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LiquidNetwork {
    public static class NodeEntry {
        public final LiquidNetworkNode node;
        public final int index;
        public NodeEntry(LiquidNetworkNode node, int index) {
            this.node = node;
            this.index = index;
        }
    }

    private final List<NodeEntry> members = new ArrayList<>();
    private float currSystemAmount;
    private LiquidType liquidType;

    public void addMember(LiquidNetworkNode node, int index) {
        node.setNetwork(index, this);
        members.add(new NodeEntry(node, index));
    }

    public LiquidType getLiquidType() {
        return liquidType;
    }

    public boolean canAcceptLiquid(LiquidType type) {
        return type != null && (liquidType == null || liquidType == type);
    }

    public float addLiquid(LiquidType type, float amount) {
        if (!canAcceptLiquid(type) || amount <= 0f) {
            return 0f;
        }

        float before = currSystemAmount;
        float capacity = getCurrSystemCapacity();
        currSystemAmount = Math.min(currSystemAmount + amount, capacity);
        float added = currSystemAmount - before;

        if (added > 0f && liquidType == null) {
            liquidType = type;
        }

        return added;
    }

    public float takeLiquid(float amount) {
        float available = currSystemAmount;
        float taken = Math.min(amount, available);
        currSystemAmount -= taken;
        if (currSystemAmount <= 0f) {
            currSystemAmount = 0f;
            liquidType = null;
        }
        return taken;
    }

    public float getCurrSystemAmount() {
        return currSystemAmount;
    }

    public float getCurrSystemCapacity() {
        float cap = 0f;
        for (NodeEntry entry : members) {
            cap += entry.node.getCapacity(entry.index);
        }
        return cap;
    }

    public float getFillRatio() {
        float cap = getCurrSystemCapacity();
        return cap > 0f ? currSystemAmount / cap : 0f;
    }

    public void tick() {
        float fill = getFillRatio();
        for (NodeEntry entry : members) {
            entry.node.setCurrentFill(entry.index, fill);
        }
    }

    public void initFromPrevFill() {
        currSystemAmount = 0f;
        liquidType = null;

        for (NodeEntry entry : members) {
            currSystemAmount += entry.node.getPrevFill(entry.index) * entry.node.getCapacity(entry.index);
            LiquidType prevType = entry.node.getPrevLiquidType(entry.index);
            if (prevType != null) {
                liquidType = prevType;
            }
        }

        currSystemAmount = Math.min(currSystemAmount, getCurrSystemCapacity());
    }

    public List<NodeEntry> getMembers() {
        return Collections.unmodifiableList(members);
    }
}
