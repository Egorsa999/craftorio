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


import java.awt.*;

public class GameScreen implements Screen {
    private MainGame game;

    private CameraManager playerCamera;
    private WorldRenderer WorldRender;
    private PlayerController playerController;
    private BuildInputHandler buildInputHandler;
    private WorldInteractionHandler worldInteractionHandler;
    private DebugInputHandler debugInputHandler;
    private BuildingManager buildingManager;
    private BuildTool buildTool;
    private PathFinder pathFinder;
    private SimulationEngine engine;

    private InventoryUI inventoryUI;
    private AssemblerUI assemblerUI;
    private TextureAtlas atlas;

    private int worldWidth = GameConfig.WORLD_SIZE_WIDTH;
    private int worldHeight = GameConfig.WORLD_SIZE_HEIGHT;
    private static final float TIME_STEP = GameConfig.TICK_TIME;
    private float accumulator = 0f;

    public GameScreen(MainGame game){
        this.game = game;

        this.atlas = new TextureAtlas(Gdx.files.internal("atlas/main_atlas.atlas"));
        TextureLoad textures = new TextureLoad(atlas);
        Inventory inventory = new Inventory();
        WorldMap worldMap = new WorldMap(worldWidth, worldHeight);
        BuildingRegistry buildingRegistry = new BuildingRegistry();

        Point spawnPoint = worldMap.findSpawnPoint();
        Player player = new Player(worldMap, buildingRegistry, spawnPoint);

        Point corePoint = new Point(spawnPoint.x - 1, spawnPoint.y + 1);
        pathFinder = new PathFinder(corePoint.x + 1, corePoint.y + 1, worldMap, buildingRegistry);
        pathFinder.updateFlowField();
        WaveSpawner waveSpawner = new WaveSpawner(pathFinder, buildingRegistry, worldMap);

        engine = new SimulationEngine(buildingRegistry, waveSpawner);
        BuildingFactory factory = new BuildingFactory(buildingRegistry, inventory, worldMap, engine);

        this.buildingManager = new BuildingManager(buildingRegistry, worldMap, factory, player);
        this.buildTool = new BuildTool(buildingManager, factory);

        buildingManager.tryPlaceBuilding(BuildingType.CORE, corePoint, Direction.UP);

        this.playerCamera = new CameraManager(player, worldMap);
        this.WorldRender = new WorldRenderer(playerCamera, worldMap, buildingRegistry, waveSpawner, textures,
            player, engine.getBullets(), buildTool::getPreviewState);

        this.inventoryUI = new InventoryUI(textures, inventory);
        this.assemblerUI = new AssemblerUI(textures);

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

    int counter = 0;
    boolean isCalculating = false;

    @Override
    public void render(float v) {
        float delta = Gdx.graphics.getDeltaTime();
        accumulator += delta;
        counter++;

        if (counter == 120){
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
            playerController.update(TIME_STEP);
            if (buildTool.isActive()) {
                buildInputHandler.updateHoverPosition(Gdx.input.getX(), Gdx.input.getY());
            }

            engine.update();
            accumulator -= TIME_STEP;
        }
        WorldRender.render();

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
        WorldRender.dispose();
        inventoryUI.dispose();
        assemblerUI.dispose();
        atlas.dispose();
    }
}
