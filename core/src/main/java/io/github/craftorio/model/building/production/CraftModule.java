package io.github.craftorio.model.building.production;

import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.model.item.Recipe;
import io.github.craftorio.model.item.LiquidType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftModule {
    private final Map<ItemType, Integer> inputItems = new HashMap<>();
    private final Map<ItemType, Integer> outputItems = new HashMap<>();
    private final Map<LiquidType, Float> inputLiquids = new HashMap<>();
    private final Map<LiquidType, Float> outputLiquids = new HashMap<>();

    private final int maxItemCapacity;
    private final float maxLiquidCapacity;

    private final boolean usesPower;
    private final float maxPowerPerTick;

    private Recipe currentRecipe = null;
    private boolean isCraftingNow = false;
    private float progress = 0;
    private float satisfactionRatio = 0f;

    private final List<Recipe> allowedRecipes;

    public CraftModule(int maxItemCapacity, float maxLiquidCapacity, List<Recipe> allowedRecipes, boolean usesPower, float maxPowerPerTick) {
        this.maxItemCapacity = maxItemCapacity;
        this.maxLiquidCapacity = maxLiquidCapacity;
        this.allowedRecipes = allowedRecipes;
        this.usesPower = usesPower;
        this.maxPowerPerTick = maxPowerPerTick;
    }

    public void update() {
        if (currentRecipe == null) return;

        if (isCraftingNow) {
            progress += satisfactionRatio;
            if (progress >= currentRecipe.getCraftTicks()) {
                finishCrafting();
            } else {
                return;
            }
        }
        tryStartCrafting();
    }

    private void tryStartCrafting() {
        if (!hasSpaceForOutputs() || !hasEnoughInputs()) return;

        isCraftingNow = true;

        for (Map.Entry<ItemType, Integer> entry : currentRecipe.getInputItems().entrySet()) {
            inputItems.put(entry.getKey(), inputItems.get(entry.getKey()) - entry.getValue());
            if (inputItems.get(entry.getKey()) <= 0) {
                inputItems.remove(entry.getKey());
            }
        }

        for (Map.Entry<LiquidType, Float> entry : currentRecipe.getInputLiquids().entrySet()) {
            float newValue = inputLiquids.get(entry.getKey()) - entry.getValue();
            if (newValue <= 0.001f) {
                inputLiquids.remove(entry.getKey());
            } else {
                inputLiquids.put(entry.getKey(), newValue);
            }
        }
    }

    private void finishCrafting() {
        progress = 0;
        isCraftingNow = false;

        for (Map.Entry<ItemType, Integer> entry : currentRecipe.getOutputItems().entrySet()) {
            outputItems.put(entry.getKey(), outputItems.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
        for (Map.Entry<LiquidType, Float> entry : currentRecipe.getOutputLiquids().entrySet()) {
            outputLiquids.put(entry.getKey(), outputLiquids.getOrDefault(entry.getKey(), 0f) + entry.getValue());
        }
    }

    private boolean hasSpaceForOutputs() {
        for (Map.Entry<ItemType, Integer> entry : currentRecipe.getOutputItems().entrySet()) {
            if (outputItems.getOrDefault(entry.getKey(), 0) + entry.getValue() > maxItemCapacity) {
                return false;
            }
        }
        for (Map.Entry<LiquidType, Float> entry : currentRecipe.getOutputLiquids().entrySet()) {
            if (outputLiquids.getOrDefault(entry.getKey(), 0f) + entry.getValue() > maxLiquidCapacity) {
                return false;
            }
        }
        return true;
    }

    private boolean hasEnoughInputs() {
        for (Map.Entry<ItemType, Integer> entry : currentRecipe.getInputItems().entrySet()) {
            if (inputItems.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        for (Map.Entry<LiquidType, Float> entry : currentRecipe.getInputLiquids().entrySet()) {
            if (inputLiquids.getOrDefault(entry.getKey(), 0f) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public boolean receiveItem(ItemType id) {
        if (currentRecipe != null && currentRecipe.getInputItems().containsKey(id)) {
            int currentAmount = inputItems.getOrDefault(id, 0);
            if (currentAmount < maxItemCapacity) {
                inputItems.put(id, currentAmount + 1);
                return true;
            }
        }
        return false;
    }

    public float receiveLiquid(LiquidType type, float amount) {
        if (currentRecipe != null && currentRecipe.getInputLiquids().containsKey(type)) {
            float currentAmount = inputLiquids.getOrDefault(type, 0f);
            float left = Math.max(0, amount - (maxLiquidCapacity - currentAmount));
            inputLiquids.put(type, Math.min(currentAmount + amount, maxLiquidCapacity));
            return Math.min(currentAmount + amount, maxLiquidCapacity) - currentAmount;
        }
        return 0.0f;
    }

    public void setRecipe(Recipe recipe) {
        inputItems.clear();
        outputItems.clear();
        inputLiquids.clear();
        outputLiquids.clear();

        currentRecipe = recipe;
        isCraftingNow = false;
        progress = 0;
    }

    public Recipe getRecipe() { return currentRecipe; }
    public boolean isCrafting() { return isCraftingNow; }
    public float getProgress() { return currentRecipe == null ? 0f : progress / currentRecipe.getCraftTicks(); }
    public void setSatisfactionRatio(float ratio) {
        this.satisfactionRatio = Math.max(0f, Math.min(ratio, 1.0f));
    }
    public float getSatisfactionRatio() { return satisfactionRatio; }

    public Map<ItemType, Integer> getInputItems() { return inputItems; }
    public Map<ItemType, Integer> getOutputItems() { return outputItems; }
    public Map<LiquidType, Float> getInputLiquids() { return inputLiquids; }
    public Map<LiquidType, Float> getOutputLiquids() { return outputLiquids; }

    public int getMaxItemCapacity() { return maxItemCapacity; }
    public float getMaxLiquidCapacity() { return maxLiquidCapacity; }
    public boolean usesPower() { return usesPower; }
    public float getMaxPowerPerTick() { return maxPowerPerTick; }

    public List<Recipe> getAllowedRecipes() {
        return this.allowedRecipes;
    }
}
