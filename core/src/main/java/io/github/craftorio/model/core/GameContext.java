package io.github.craftorio.model.core;

import io.github.craftorio.model.building.BuildingFactory;
import io.github.craftorio.model.entity.Player;
import io.github.craftorio.model.enemy.WaveSpawner;
import io.github.craftorio.model.enemy.PathFinder;
import io.github.craftorio.model.ui.BuildTool;
import io.github.craftorio.model.ui.Inventory;
import io.github.craftorio.view.TextureLoad;

public class GameContext {
    public static GameContext current;

    public final WorldMap worldMap;
    public final BuildingRegistry registry;
    public final Inventory inventory;
    public final TextureLoad textures;

    public BuildTool buildTool;
    public BuildingFactory factory;
    public Player player;
    public PathFinder pathFinder;
    public WaveSpawner waveSpawner;
    public SimulationEngine engine;


    public GameContext(WorldMap worldMap, BuildingRegistry registry, Inventory inventory, TextureLoad textures) {
        this.worldMap = worldMap;
        this.registry = registry;
        this.inventory = inventory;
        this.textures = textures;

        GameContext.current = this;
    }
}
