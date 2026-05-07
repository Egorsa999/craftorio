package io.github.craftorio.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.craftorio.model.Player;

public class InputController {
    private final Player player;

    // The controller needs a reference to the Model to control it
    public InputController(Player player) {
        this.player = player;
    }

    public void update(float delta) {
        float dx = 0;
        float dy = 0;

        // Read WASD or arrow key presses
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) dx = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) dy = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) dy = -1;

        // Pass the command to the Model
        if (dx != 0 || dy != 0) {
            player.updatePosition(delta, dx, dy);
        }
    }
}
