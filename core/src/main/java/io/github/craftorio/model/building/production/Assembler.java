package io.github.craftorio.model.building.production;

import io.github.craftorio.model.building.*;
import io.github.craftorio.model.building.power.PowerConnectable;
import io.github.craftorio.model.building.power.PowerConsumer;
import io.github.craftorio.model.building.power.PowerNode;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.model.item.Recipe;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Assembler extends DamageableBuilding implements ThroughItem, ReceiveItem, PowerConsumer, PowerConnectable {

    private final PowerNode powerNode;

    private final Map<ItemType, Integer> inputInventory = new HashMap<>();
    private final Map<ItemType, Integer> outputInventory = new HashMap<>();

    private final int MAX_CAPACITY_PER_ITEM = 50;
    private final int MAX_OUTPUT_CAPACITY = 50;

    private Recipe currentRecipe = null;

    private boolean isCraftingNow = false;
    private float progress = 0;

    private final ArrayList<Point> throughDelta = new ArrayList<>();
    private int lastThrough = 0;

    private final float maxPowerPerTick = 150.0f / 60.0f;
    private float satisfactionRatio = 0f;


    public Assembler(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.ASSEMBLER);

        this.powerNode = new PowerNode(this, registry);

        throughDelta.add(new Point(+0, +2));
        throughDelta.add(new Point(+1, +2));
        throughDelta.add(new Point(+2, +1));
        throughDelta.add(new Point(+2, +0));
        throughDelta.add(new Point(+1, -1));
        throughDelta.add(new Point(+0, -1));
        throughDelta.add(new Point(-1, +0));
        throughDelta.add(new Point(-1, +1));
    }

    @Override
    public void update() {
        super.update();
        if (currentRecipe == null) return;

        for (Map.Entry<ItemType, Integer> entry : outputInventory.entrySet()) {
            if (entry.getValue() > 0) {
                if (throughItem(entry.getKey())) {
                    outputInventory.put(entry.getKey(), outputInventory.get(entry.getKey()) - 1);
                    if (outputInventory.get(entry.getKey()) == 0) {
                        outputInventory.remove(entry.getKey());
                    }
                    break;
                }
            }
        }
        if (isCraftingNow) {
            System.out.println(satisfactionRatio);
            progress += satisfactionRatio;
            if (progress >= currentRecipe.getCraftTicks()) {
                progress = 0;
                isCraftingNow = false;
                outputInventory.put(currentRecipe.getOutput(), outputInventory.getOrDefault(currentRecipe.getOutput(), 0) + currentRecipe.getOutputAmount());
            }
            else return;
        }

        boolean canCraft = true;

        int currentOutputAmount = outputInventory.getOrDefault(currentRecipe.getOutput(), 0);
        if (currentOutputAmount + currentRecipe.getOutputAmount() > MAX_OUTPUT_CAPACITY) {
            canCraft = false;
        }

        if (canCraft) {
            for (Map.Entry<ItemType, Integer> entry : currentRecipe.getInputs().entrySet()) {
                if (inputInventory.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                    canCraft = false;
                    break;
                }
            }
        }
        if (canCraft) {
            isCraftingNow = true;
            for (Map.Entry<ItemType, Integer> entry : currentRecipe.getInputs().entrySet()) {
                inputInventory.put(entry.getKey(), inputInventory.get(entry.getKey()) - entry.getValue());
                if (inputInventory.get(entry.getKey()) == 0) {
                    inputInventory.remove(entry.getKey());
                }
            }
        }
    }

    @Override
    public boolean receiveItem(Building building, ItemType id) {
        if (currentRecipe != null && currentRecipe.getInputs().containsKey(id)) {
            int currentAmount = inputInventory.getOrDefault(id, 0);

            if (currentAmount < MAX_CAPACITY_PER_ITEM) {
                inputInventory.put(id, currentAmount + 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canReceiveFrom(Building building, Point point) {
        return true;
    }

    @Override
    public boolean throughItem(ItemType type) {
        for (int iterate = 0; iterate <= throughDelta.size(); iterate++) {
            lastThrough++;
            lastThrough %= throughDelta.size();
            int x = getX() + throughDelta.get(lastThrough).x;
            int y = getY() + throughDelta.get(lastThrough).y;
            Building nextBuilding = registry.getBuildingAt(x, y);
            if (nextBuilding instanceof ReceiveItem building) {
                if (building.receiveItem(this, type)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canThroughIn(Point point) {
        return true;
    }

    public Recipe getRecipe() {
        return this.currentRecipe;
    }

    public void setRecipe(Recipe recipe) {
        // temp clear
        inputInventory.clear();
        outputInventory.clear();

        currentRecipe = recipe;
    }

    public float getProgress() {
        if (currentRecipe == null) return 0.0f;
        return (float) progress / currentRecipe.getCraftTicks();
    }

    public Map<ItemType, Integer> getInputInventory() {
        return this.inputInventory;
    }

    public Map<ItemType, Integer> getOutputInventory() {
        return this.outputInventory;
    }

    @Override
    public PowerNode getPowerNode() {
        return powerNode;
    }

    @Override
    public float getRequiredPower() {
        return isCraftingNow ? maxPowerPerTick : 0f;
    }

    @Override
    public void setSatisfactionRatio(float ratio) {
        this.satisfactionRatio = Math.max(0f, Math.min(ratio, 1.0f));
    }
}
