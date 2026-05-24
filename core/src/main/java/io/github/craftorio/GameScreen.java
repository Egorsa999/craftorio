package io.github.craftorio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.craftorio.controller.*;
import io.github.craftorio.model.building.BuildingFactory;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.core.*;
import io.github.craftorio.model.enemy.PathFinder;
import io.github.craftorio.model.enemy.WaveSpawner;
import io.github.craftorio.model.entity.Player;
import io.github.craftorio.model.ui.BuildTool;
import io.github.craftorio.model.ui.Inventory;
import io.github.craftorio.view.CameraManager;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.WorldRenderer;
import io.github.craftorio.view.ui.AssemblerUI;
import io.github.craftorio.view.ui.InventoryUI;

import java.awt.Point;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GameScreen implements Screen {
    private final MainGame game;

    // View & UI
    private final CameraManager playerCamera;
    private final WorldRenderer worldRenderer; // С маленькой буквы по правилам Java
    private final InventoryUI inventoryUI;
    private final AssemblerUI assemblerUI;
    private final TextureAtlas atlas;

    // Controllers
    private final PlayerController playerController;
    private final BuildInputHandler buildInputHandler;
    private final WorldInteractionHandler worldInteractionHandler;
    private final DebugInputHandler debugInputHandler;

    // Core Logic
    private final BuildTool buildTool;
    private final PathFinder pathFinder;
    private final SimulationEngine engine;

    // Timing & Threading
    private float accumulator = 0f;
    private float pathfinderTimer = 0f;
    private static final float PATHFINDER_UPDATE_INTERVAL = 2.0f;

    private final ExecutorService executorService;
    private Future<?> pathfinderTask;

    public GameScreen(MainGame game) {
        this.game = game;

        this.executorService = Executors.newSingleThreadExecutor();

        this.atlas = new TextureAtlas(Gdx.files.internal("atlas/main_atlas.atlas"));
        TextureLoad textures = new TextureLoad(atlas);

        // Model Creation
        WorldMap worldMap = new WorldMap(GameConfig.WORLD_SIZE_WIDTH, GameConfig.WORLD_SIZE_HEIGHT);
        BuildingRegistry buildingRegistry = new BuildingRegistry();
        Inventory inventory = new Inventory();

        Point spawnPoint = worldMap.findSpawnPoint();
        Player player = new Player(worldMap, buildingRegistry, spawnPoint);

        Point corePoint = new Point(spawnPoint.x - 1, spawnPoint.y + 1);
        this.pathFinder = new PathFinder(corePoint.x + 1, corePoint.y + 1, worldMap, buildingRegistry);
        this.pathFinder.updateFlowField();

        WaveSpawner waveSpawner = new WaveSpawner(pathFinder, buildingRegistry, worldMap);
        this.engine = new SimulationEngine(buildingRegistry, waveSpawner);
        BuildingFactory factory = new BuildingFactory(buildingRegistry, inventory, worldMap, engine);

        BuildingManager buildingManager = new BuildingManager(buildingRegistry, worldMap, factory, player);
        this.buildTool = new BuildTool(buildingManager, factory);

        buildingManager.tryPlaceBuilding(BuildingType.CORE, corePoint, Direction.UP);

        // View creation
        this.playerCamera = new CameraManager(player, worldMap);
        this.worldRenderer = new WorldRenderer(playerCamera, worldMap, buildingRegistry, waveSpawner, textures,
            player, engine.getBullets(), buildTool::getPreviewState);

        this.inventoryUI = new InventoryUI(textures, inventory);
        this.assemblerUI = new AssemblerUI(textures);

        // Controller creation
        this.playerController = new PlayerController(player, playerCamera);
        this.buildInputHandler = new BuildInputHandler(buildTool, playerCamera, factory);
        this.worldInteractionHandler = new WorldInteractionHandler(playerCamera, buildingManager, assemblerUI);
        this.debugInputHandler = new DebugInputHandler(waveSpawner, playerCamera, buildTool);
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(inventoryUI.getStage());
        multiplexer.addProcessor(assemblerUI.getStage());
        multiplexer.addProcessor(buildInputHandler);
        multiplexer.addProcessor(worldInteractionHandler);
        multiplexer.addProcessor(debugInputHandler);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        accumulator += delta;
        pathfinderTimer += delta;

        if (pathfinderTimer >= PATHFINDER_UPDATE_INTERVAL) {
            pathfinderTimer = 0f;
            if (pathfinderTask == null || pathfinderTask.isDone()) {
                pathfinderTask = executorService.submit(pathFinder::updateFlowField);
            }
        }

        while (accumulator >= GameConfig.TICK_TIME) {
            playerController.update(GameConfig.TICK_TIME);
            if (buildTool.isActive()) {
                buildInputHandler.updateHoverPosition(Gdx.input.getX(), Gdx.input.getY());
            }
            engine.update();
            accumulator -= GameConfig.TICK_TIME;
        }

        worldRenderer.render();
        inventoryUI.render();
        assemblerUI.render();
    }

    @Override
    public void resize(int width, int height) {
        playerCamera.resize(width, height);
        inventoryUI.resize(width, height);
        assemblerUI.resize(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        executorService.shutdownNow();

        worldRenderer.dispose();
        inventoryUI.dispose();
        assemblerUI.dispose();
        atlas.dispose();
    }
}
