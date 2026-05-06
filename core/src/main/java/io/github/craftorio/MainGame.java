package io.github.craftorio;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.craftorio.controller.InputController;
import io.github.craftorio.model.World;
import io.github.craftorio.view.GameRenderer;

public class MainGame extends ApplicationAdapter {
    private GameRenderer renderer;
    private InputController controller;

    @Override
    public void create() {
        // Initialize our architecture (strictly in this order)
        World world = new World();
        controller = new InputController(world);
        renderer = new GameRenderer(world);
    }

    @Override
    public void render() {
        // Get the time passed since the last frame (for smooth movement on any PC)
        float delta = Gdx.graphics.getDeltaTime();

        // 1. The Controller processes input and updates the Model
        controller.update(delta);

        // 2. The View renders the updated Model
        renderer.render();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
