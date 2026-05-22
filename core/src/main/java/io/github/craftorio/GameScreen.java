package io.github.craftorio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.craftorio.controller.InputController;
import io.github.craftorio.model.*;
import io.github.craftorio.model.building.BuildingFactory;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.enemy.PathFinder;
import io.github.craftorio.model.enemy.WaveSpawner;
import io.github.craftorio.model.ui.BuildTool;
import io.github.craftorio.model.ui.Inventory;
import io.github.craftorio.view.CameraManager;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.WorldRenderer;
import io.github.craftorio.view.ui.InventoryUI;

import java.awt.*;

public class GameScreen implements Screen {
    private MainGame game;

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
    private InventoryUI inventoryUI;
    private TextureLoad textures;
    private TextureAtlas atlas;
    private PathFinder pathFinder;
    private WaveSpawner waveSpawner;


    private int worldWidth = GameConfig.WORLD_SIZE_WIDTH;
    private int worldHeight = GameConfig.WORLD_SIZE_HEIGHT;
    // constants for 60TPS(Tick Per Second)
    private static final float TIME_STEP = GameConfig.TICK_TIME;
    private float accumulator = 0f;

    public GameScreen(MainGame game){
        this.game = game;

        this.atlas = new TextureAtlas(Gdx.files.internal("atlas/main_atlas.atlas"));
        textures = new TextureLoad(atlas);

        inventory = new Inventory();
        worldMap = new WorldMap(worldWidth, worldHeight);

        Point spawnPoint = worldMap.findSpawnPoint();

        buildingRegistry = new BuildingRegistry();

        factory = new BuildingFactory(worldMap, buildingRegistry, inventory);

        Player player = new Player(worldMap, buildingRegistry, spawnPoint);
        playerCamera = new CameraManager(player, worldMap);
        buildingManager = new BuildingManager(buildingRegistry, worldMap, factory, player);
        buildTool = new BuildTool(buildingManager);

        Point corePoint = new Point(spawnPoint.x - 1, spawnPoint.y + 1);
        buildingManager.tryPlaceBuilding(BuildingType.CORE, corePoint, Direction.UP);


        pathFinder = new PathFinder(corePoint.x + 1, corePoint.y + 1, worldMap, buildingRegistry);
        pathFinder.updateFlowField();
        waveSpawner = new WaveSpawner(pathFinder, buildingRegistry, worldMap);
        WorldRender = new WorldRenderer(textures, playerCamera, player, worldMap, buildTool, factory, buildingRegistry, waveSpawner, pathFinder);


        controller = new InputController(player, playerCamera, buildTool, buildingManager, factory, waveSpawner);
        simulationEngine = new SimulationEngine(buildingRegistry, waveSpawner);
        inventoryUI = new InventoryUI(textures, inventory);

        factory.setSimulationEngine(simulationEngine);

        WorldRender.setSimulationEngine(simulationEngine);
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();

        multiplexer.addProcessor(inventoryUI.getStage());
        multiplexer.addProcessor(controller);

        Gdx.input.setInputProcessor(multiplexer);
    }

    int counter = 0;
    boolean isCalculating = false;


    @Override
    public void render(float v) {
        float delta = Gdx.graphics.getDeltaTime();

        accumulator += delta;
        counter++;
        if (counter == 240){
            if (!isCalculating) {
                isCalculating = true;

                new Thread(() -> {
                    try {
                        pathFinder.updateFlowField();
                    } finally {
                        isCalculating = false;
                    }
                }).start();
                counter = 0;
            }
        }
        while (accumulator >= TIME_STEP) {
            controller.update(TIME_STEP);
            simulationEngine.update();
            accumulator -= TIME_STEP;
        }

        WorldRender.render();

        inventoryUI.render();
    }

    @Override
    public void resize(int width, int height) {
        playerCamera.resize(width, height);

        inventoryUI.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        WorldRender.dispose();
        inventoryUI.dispose();
        atlas.dispose();
    }

}
