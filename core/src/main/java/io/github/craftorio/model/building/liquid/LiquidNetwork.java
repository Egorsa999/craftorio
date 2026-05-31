package io.github.craftorio.model.building.liquid;

import io.github.craftorio.model.building.logistics.Pipe;
import io.github.craftorio.model.item.LiquidType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LiquidNetwork {
    private final List<Pipe> members = new ArrayList<>();
    private float currSystemAmount;
    private LiquidType liquidType;

    public void addMember(Pipe pipe) {
        pipe.setNetwork(this);
        members.add(pipe);
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
        for (Pipe pipe : members) {
            cap += pipe.getLiquidCapacity();
        }
        return cap;
    }

    public float getFillRatio() {
        float cap = getCurrSystemCapacity();
        return cap > 0f ? currSystemAmount / cap : 0f;
    }

    public void tick() {
        float fill = getFillRatio();
        for (Pipe pipe : members) {
            pipe.setCurrentFill(fill);
        }
    }

    public void initFromPrevFill() {
        currSystemAmount = 0f;
        liquidType = null;

        //System.out.println("__________");

        for (Pipe pipe : members) {
            currSystemAmount += pipe.getPrevFill() * pipe.getLiquidCapacity();
            LiquidType prevType = pipe.getPrevLiquidType();
            //System.out.println(prevType);
            if (prevType != null) {
                liquidType = prevType;
            }
        }
        //System.out.println("__________");

        currSystemAmount = Math.min(currSystemAmount, getCurrSystemCapacity());
    }

    public List<Pipe> getMembers() {
        return Collections.unmodifiableList(members);
    }
}
