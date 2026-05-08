package io.github.craftorio;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.craftorio.controller.InputController;
import io.github.craftorio.model.Player;
import io.github.craftorio.model.WorldMap;
import io.github.craftorio.view.PlayerCamera;

public class MainGame extends ApplicationAdapter {
    private PlayerCamera playerCamera;
    private WorldMap worldMap;
    private InputController controller;

    private int worldWidth = 100;
    private int worldHeight = 100;
    // constants for 60TPS(Tick Per Second)
    private static final float TIME_STEP = 1.0f / 60.0f;
    private float accumulator = 0f;

    @Override
    public void create() {

        worldMap = new WorldMap(worldWidth, worldHeight);

        Player player = new Player(worldMap);
        playerCamera = new PlayerCamera(player, worldMap);
        controller = new InputController(player, playerCamera);


    }

    @Override
    public void render() {
        // Get the time passed since the last frame (for smooth movement on any PC)
        float delta = Gdx.graphics.getDeltaTime();

        accumulator += delta;

        while (accumulator >= TIME_STEP) {
            controller.update(TIME_STEP);
            worldMap.update(TIME_STEP);
            accumulator -= TIME_STEP;
        }

        playerCamera.render();
    }

    @Override
    public void resize(int width, int height){
        playerCamera.resize(width, height);
    }

    @Override
    public void dispose() {
        playerCamera.dispose();
    }
}
