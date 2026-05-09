package io.github.craftorio;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.craftorio.controller.InputController;
import io.github.craftorio.model.*;
import io.github.craftorio.model.building.BuildingFactory;
import io.github.craftorio.model.ui.BuildTool;
import io.github.craftorio.view.CameraManager;
import io.github.craftorio.view.WorldRenderer;

public class MainGame extends ApplicationAdapter {
    private CameraManager playerCamera;
    private WorldRenderer WorldRender;
    private WorldMap worldMap;
    private InputController controller;
    private BuildingManager buildingManager;
    private BuildTool buildTool;
    private BuildingFactory factory;
    private BuildingRegistry buildingRegistry;
    private SimulationEngine simulationEngine;

    private int worldWidth = GameConfig.WORLD_SIZE_WIDTH;
    private int worldHeight = GameConfig.WORLD_SIZE_HEIGHT;
    // constants for 60TPS(Tick Per Second)
    private static final float TIME_STEP = GameConfig.TICK_TIME;
    private float accumulator = 0f;

    @Override
    public void create() {
        buildTool = new BuildTool();

        worldMap = new WorldMap(worldWidth, worldHeight);
        buildingRegistry = new BuildingRegistry();
        simulationEngine = new SimulationEngine(buildingRegistry);
        factory = new BuildingFactory(worldMap, buildingRegistry);
        Player player = new Player(worldMap);
        playerCamera = new CameraManager(player, worldMap);
        WorldRender = new WorldRenderer(playerCamera, player, worldMap, buildTool, factory, buildingRegistry);
        buildingManager = new BuildingManager(buildingRegistry, worldMap, factory);
        controller = new InputController(player, playerCamera, buildTool, buildingManager, factory);
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render() {
        // Get the time passed since the last frame (for smooth movement on any PC)
        float delta = Gdx.graphics.getDeltaTime();

        accumulator += delta;

        while (accumulator >= TIME_STEP) {
            controller.update(TIME_STEP);
            simulationEngine.update();
            accumulator -= TIME_STEP;
        }

        WorldRender.render();
    }

    @Override
    public void resize(int width, int height){
        playerCamera.resize(width, height);
    }

    @Override
    public void dispose() {
        WorldRender.dispose();
    }
}
