package io.github.craftorio;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.craftorio.controller.InputController;
import io.github.craftorio.model.Player;
import io.github.craftorio.view.PlayerCamera;

public class MainGame extends ApplicationAdapter {
    private PlayerCamera playerCamera;
    private InputController controller;

    @Override
    public void create() {
        Player player = new Player();
        controller = new InputController(player);
        playerCamera = new PlayerCamera(player);
    }

    @Override
    public void render() {
        // Get the time passed since the last frame (for smooth movement on any PC)
        float delta = Gdx.graphics.getDeltaTime();

        // 1. The Controller processes input and updates the Model
        controller.update(delta);

        // 2. The View renders the updated Model
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
