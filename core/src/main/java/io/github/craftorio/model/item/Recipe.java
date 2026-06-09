package io.github.craftorio.model.item;

import java.util.Collections;
import java.util.Map;

public enum Recipe {
    TEST1(
        Map.of(ItemType.COPPER_ORE, 1, ItemType.COAL, 3),
        Map.of(LiquidType.WATER, 10.0f),
        Map.of(ItemType.IRON_ORE, 1),
        Collections.emptyMap(),
        90
    ),
    TEST2(
        Map.of(ItemType.IRON_ORE, 2),
        Collections.emptyMap(),
        Map.of(ItemType.COPPER_ORE, 2),
        Collections.emptyMap(),
        90
    ),
    TEST3(
        Collections.emptyMap(),
        Map.of(LiquidType.WATER, 10.0f),
        Collections.emptyMap(),
        Map.of(LiquidType.OIL, 1.0f),
        90
    ),
    ROCKET_FUEL(
        Collections.emptyMap(),
        Map.of(LiquidType.WATER, 10.0f, LiquidType.OIL, 5.0f),
        Collections.emptyMap(),
        Map.of(LiquidType.ROCKET_FUEL, 1.0f),
        120
    ),
    BULLET(
        Map.of(ItemType.COPPER_INGOT, 1, ItemType.COAL, 1),
        Collections.emptyMap(),
        Map.of(ItemType.BULLET, 3),
        Collections.emptyMap(),
        60
    ),
    STEEL_BULLET(
        Map.of(ItemType.STEEL, 1, ItemType.BULLET, 1),
        Collections.emptyMap(),
        Map.of(ItemType.STEEL_BULLET, 1),
        Collections.emptyMap(),
        60
    ),
    IRON_INGOT(
        Map.of(ItemType.IRON_ORE, 2),
        Collections.emptyMap(),
        Map.of(ItemType.IRON_INGOT, 1),
        Collections.emptyMap(),
        120
    ),
    COPPER_INGOT(
        Map.of(ItemType.COPPER_ORE, 1),
        Collections.emptyMap(),
        Map.of(ItemType.COPPER_INGOT, 1),
        Collections.emptyMap(),
        120
    ),
    STEEL(
        Map.of(ItemType.IRON_INGOT, 2, ItemType.COAL, 2),
        Collections.emptyMap(),
        Map.of(ItemType.STEEL, 1),
        Collections.emptyMap(),
        120
    ),
    CHIP(
        Map.of(ItemType.COPPER_INGOT, 5, ItemType.PLASTIC, 5),
        Collections.emptyMap(),
        Map.of(ItemType.CHIP, 1),
        Collections.emptyMap(),
        120
    ),
    GLASS (
        Map.of(ItemType.STONE, 2),
        Collections.emptyMap(),
        Map.of(ItemType.GLASS, 1),
        Collections.emptyMap(),
        120
    ),
    PLASTIC(
        Map.of(ItemType.COAL, 2),
        Map.of(LiquidType.OIL, 3.0f),
        Map.of(ItemType.PLASTIC, 1),
        Collections.emptyMap(),
        120
    );

    private final Map<ItemType, Integer> inputItems;
    private final Map<LiquidType, Float> inputLiquids;
    private final Map<ItemType, Integer> outputItems;
    private final Map<LiquidType, Float> outputLiquids;
    private final int craftTicks;

    Recipe(Map<ItemType, Integer> inputItems, Map<LiquidType, Float> inputLiquids,
           Map<ItemType, Integer> outputItems, Map<LiquidType, Float> outputLiquids, int craftTicks) {
        this.inputItems = inputItems;
        this.inputLiquids = inputLiquids;
        this.outputItems = outputItems;
        this.outputLiquids = outputLiquids;
        this.craftTicks = craftTicks;
    }

    public Map<ItemType, Integer> getInputItems() { return inputItems; }
    public Map<LiquidType, Float> getInputLiquids() { return inputLiquids; }
    public Map<ItemType, Integer> getOutputItems() { return outputItems; }
    public Map<LiquidType, Float> getOutputLiquids() { return outputLiquids; }
    public int getCraftTicks() { return craftTicks; }
}
