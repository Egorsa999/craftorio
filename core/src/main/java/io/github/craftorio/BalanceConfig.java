package io.github.craftorio;

import io.github.craftorio.model.item.ItemType;

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

    // Pump
    public static final float PUMP_PRODUCTION_RATE = 5.0f;

    // Pipe
    public static final float PIPE_CAPACITY = 10.0f;
    public static final float PIPE_THROUGHPUT = 10.0f;

    // Turret
    public static final float TURRET_RANGE = 8.0f;
    public static final int TURRET_FIRE_COOLDOWN = 15;
    public static final int TURRET_DAMAGE = 10;
    public static final float TURRET_BULLET_SPEED = 0.4f;
    public static final ItemType TURRET_AMMO_TYPE = ItemType.BULLET;

    // Conveyor
    public static final float CONVEYOR_SPEED = 1f;
    public static final float CONVEYOR_ITEM_SIZE = 1 / 3f;

    // Router
    public static final int ROUTER_SPEED = 15;

}
