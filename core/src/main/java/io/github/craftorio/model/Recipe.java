package io.github.craftorio.model;

import java.util.Map;

public enum Recipe {
    TEST1(ItemType.IRON_ORE, 1, 90, Map.of(
        ItemType.COPPER_ORE, 1,
        ItemType.COAL, 3
    )),
    TEST2(ItemType.COPPER_ORE, 2, 90, Map.of(
                           ItemType.IRON_ORE, 2
    ));

    private final ItemType output;
    private final int outputAmount;
    private final int craftTicks;
    private final Map<ItemType, Integer> inputs;

    Recipe(ItemType output, int outputAmount, int craftTicks, Map<ItemType, Integer> inputs) {
        this.output = output;
        this.outputAmount = outputAmount;
        this.craftTicks = craftTicks;
        this.inputs = inputs;
    }

    public ItemType getOutput() {
        return output;
    }

    public int getOutputAmount() {
        return outputAmount;
    }

    public int getCraftTicks() {
        return craftTicks;
    }

    public Map<ItemType, Integer> getInputs() {
        return inputs;
    }
}
