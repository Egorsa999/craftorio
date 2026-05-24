package io.github.craftorio.model.building.production;

import io.github.craftorio.model.building.*;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.model.item.Recipe;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Assembler extends DamageableBuilding implements ThroughItem, ReceiveItem {
    private final Map<ItemType, Integer> inputInventory = new HashMap<>();
    private final Map<ItemType, Integer> outputInventory = new HashMap<>();

    private Recipe currentRecipe = null;

    private boolean isCraftingNow = false;
    private int progress = 0;

    private final ArrayList<Point> throughDelta = new ArrayList<>();
    private int lastThrough = 0;

    public Assembler(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.ASSEMBLER);

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
            progress++;
            if (progress == currentRecipe.getCraftTicks()) {
                progress = 0;
                isCraftingNow = false;
                outputInventory.put(currentRecipe.getOutput(), outputInventory.getOrDefault(currentRecipe.getOutput(), 0) + currentRecipe.getOutputAmount());
            }
            return;
        }
        boolean canCraft = true;
        for (Map.Entry<ItemType, Integer> entry : currentRecipe.getInputs().entrySet()) {
            if (inputInventory.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                canCraft = false;
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
        inputInventory.put(id, inputInventory.getOrDefault(id, 0) + 1);
        return true;
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
}
