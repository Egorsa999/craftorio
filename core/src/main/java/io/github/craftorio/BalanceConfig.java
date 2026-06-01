package io.github.craftorio;

public final class BalanceConfig {
    private BalanceConfig() {}

    // Miners
    public static final float MINER_TICKS = 500f;
    public static final float HORIZONTAL_MINER_TICKS = 240f;

    // Power Consumer
    public static final float FURNACE_POWER_CONSUMPTION = 50f;
    public static final float ASSEMBLER_POWER_CONSUMPTION = 150f;
    public static final float CHEMICAL_PLANT_POWER_CONSUMPTION = 300f;


    // Power Producers
    public static final float COAL_GENERATOR_POWER_PRODUCTION = 100f;
    public static final float COAL_GENERATOR_COAL_FRAME_TIME = 100f;


    //Conveyor
    public static final float CONVEYOR_SPEED = 1f;
    public static final float CONVEYOR_ITEM_SIZE = 1 / 3f;

    public static final int MAX_BUILD_DISTANCE = 35;

}
