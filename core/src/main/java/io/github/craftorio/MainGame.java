package io.github.craftorio;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.craftorio.controller.InputController;
import io.github.craftorio.model.*;
import io.github.craftorio.model.building.BuildingFactory;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.ui.BuildTool;
import io.github.craftorio.model.ui.Inventory;
import io.github.craftorio.view.CameraManager;
import io.github.craftorio.view.WorldRenderer;

import java.awt.*;

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
    private Inventory inventory;

    private int worldWidth = GameConfig.WORLD_SIZE_WIDTH;
    private int worldHeight = GameConfig.WORLD_SIZE_HEIGHT;
    // constants for 60TPS(Tick Per Second)
    private static final float TIME_STEP = GameConfig.TICK_TIME;
    private float accumulator = 0f;

    @Override
    public void create() {

        inventory = new Inventory();
        worldMap = new WorldMap(worldWidth, worldHeight);

        Point spawnPoint = worldMap.findSpawnPoint();
        Player player = new Player(worldMap, spawnPoint);



        buildingRegistry = new BuildingRegistry();
        simulationEngine = new SimulationEngine(buildingRegistry);
        factory = new BuildingFactory(worldMap, buildingRegistry, inventory);

        playerCamera = new CameraManager(player, worldMap);
        buildingManager = new BuildingManager(buildingRegistry, worldMap, factory);
        buildTool = new BuildTool(buildingManager);
        WorldRender = new WorldRenderer(playerCamera, player, worldMap, buildTool, factory, buildingRegistry);

        Point corePoint = new Point(spawnPoint.x - 1, spawnPoint.y + 1);
        buildingManager.tryPlaceBuilding(BuildingType.CORE, corePoint, Direction.UP);

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
