package io.github.craftorio;

public final class GameConfig {
    private GameConfig() {}

    public static final int WORLD_SIZE_WIDTH = 1000;
    public static final int WORLD_SIZE_HEIGHT = 1000;

    public static final float PLAYER_SPEED = 8f;

    public static final float TICK_TIME = 1f / 60f; // 60 Tick Per Second

    public static boolean SPAWN_ENEMY;
    public static boolean INFINITY_RESOURCES;

    public static boolean MUTE_MUSIC;
}
