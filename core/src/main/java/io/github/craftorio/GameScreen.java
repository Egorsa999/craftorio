package io.github.craftorio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.craftorio.controller.*;
import io.github.craftorio.model.building.BuildingFactory;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.building.liquid.LiquidNetworkManager;
import io.github.craftorio.model.core.*;
import io.github.craftorio.model.enemy.PathFinder;
import io.github.craftorio.model.enemy.WaveSpawner;
import io.github.craftorio.model.entity.Player;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.ui.BuildTool;
import io.github.craftorio.ui.Inventory;
import io.github.craftorio.view.CameraManager;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.WorldRenderer;
import io.github.craftorio.view.ui.BuildMenuUI;
import io.github.craftorio.view.ui.CraftingUI;
import io.github.craftorio.view.ui.InventoryUI;
import io.github.craftorio.view.ui.PlayerUI;
import io.github.craftorio.view.ui.RocketUI;
import io.github.craftorio.view.ui.*;

import java.awt.Point;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GameScreen implements Screen {
    private final MainGame game;

    // View & UI
    private final CameraManager playerCamera;
    private final WorldRenderer worldRenderer;
    private final InventoryUI inventoryUI;
    private final BuildMenuUI buildMenuUI;
    private final CraftingUI craftingUI;
    private final RocketUI rocketUI;
    private final TextureAtlas atlas;
    private final PlayerUI playerUI;
    private final WaveUI waveUI;

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
        inventory.add(ItemType.COPPER_ORE, 10000);
        inventory.add(ItemType.IRON_ORE, 10000);

        Point spawnPoint = worldMap.findSpawnPoint();
        Player player = new Player(worldMap, buildingRegistry, spawnPoint, inventory);

        Point corePoint = new Point(spawnPoint.x - 1, spawnPoint.y + 1);
        this.pathFinder = new PathFinder(corePoint.x + 1, corePoint.y + 1, worldMap, buildingRegistry);
        this.pathFinder.updateFlowField();

        WaveSpawner waveSpawner = new WaveSpawner(pathFinder, buildingRegistry, worldMap);
        LiquidNetworkManager liquidNetworkManager = new LiquidNetworkManager(buildingRegistry);
        this.engine = new SimulationEngine(buildingRegistry, waveSpawner, liquidNetworkManager);
        BuildingFactory factory = new BuildingFactory(buildingRegistry, inventory, worldMap, engine);

        BuildingManager buildingManager = new BuildingManager(buildingRegistry, worldMap, factory, player);
        this.buildTool = new BuildTool(buildingManager, factory, inventory);

        buildingManager.tryPlaceBuilding(BuildingType.CORE, corePoint, Direction.UP);

        // View creation
        this.playerCamera = new CameraManager(player, worldMap);

        this.inventoryUI = new InventoryUI(textures, inventory);
        this.buildMenuUI = new BuildMenuUI(buildTool, inventory, textures);
        this.craftingUI = new CraftingUI(textures);
        this.rocketUI = new RocketUI(textures);
        this.playerUI = new PlayerUI(player, playerCamera);
        this.waveUI = new WaveUI(textures, waveSpawner);

        // Controller creation
        this.playerController = new PlayerController(player, playerCamera);
        this.buildInputHandler = new BuildInputHandler(buildTool, playerCamera, factory);
        this.worldInteractionHandler = new WorldInteractionHandler(playerCamera, buildingManager, craftingUI, rocketUI);
        this.debugInputHandler = new DebugInputHandler(waveSpawner, playerCamera, buildTool);

        this.worldRenderer = new WorldRenderer(playerCamera, worldMap, buildingRegistry, waveSpawner, textures,
            player, engine.getBullets(), buildTool::getPreviewState, worldInteractionHandler);
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(inventoryUI.getStage());
        multiplexer.addProcessor(buildMenuUI.getStage());
        multiplexer.addProcessor(craftingUI.getStage());
        multiplexer.addProcessor(rocketUI.getStage());
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
        buildMenuUI.render();
        craftingUI.render();
        rocketUI.render();
        playerUI.render();
        if (GameConfig.SPAWN_ENEMY) waveUI.render();
    }

    @Override
    public void resize(int width, int height) {
        playerCamera.resize(width, height);
        Viewport gameViewport = playerCamera.getViewport();
        int gameX = gameViewport.getScreenX();
        int gameY = gameViewport.getScreenY();
        int gameWidth = gameViewport.getScreenWidth();
        int gameHeight = gameViewport.getScreenHeight();

        inventoryUI.resize(gameWidth, gameHeight);
        buildMenuUI.resize(gameWidth, gameHeight);
        craftingUI.resize(gameWidth, gameHeight);
        rocketUI.resize(gameWidth, gameHeight);
        playerUI.resize(gameWidth, gameHeight);
        waveUI.resize(gameWidth, gameHeight);

        inventoryUI.getStage().getViewport().setScreenBounds(gameX, gameY, gameWidth, gameHeight);
        buildMenuUI.getStage().getViewport().setScreenBounds(gameX, gameY, gameWidth, gameHeight);
        craftingUI.getStage().getViewport().setScreenBounds(gameX, gameY, gameWidth, gameHeight);
        rocketUI.getStage().getViewport().setScreenBounds(gameX, gameY, gameWidth, gameHeight);
        playerUI.getStage().getViewport().setScreenBounds(gameX, gameY, gameWidth, gameHeight);
        waveUI.getStage().getViewport().setScreenBounds(gameX, gameY, gameWidth, gameHeight);
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
        playerUI.dispose();

        worldRenderer.dispose();
        inventoryUI.dispose();
        buildMenuUI.dispose();
        craftingUI.dispose();
        rocketUI.dispose();
        waveUI.dispose();
        atlas.dispose();
    }
}
