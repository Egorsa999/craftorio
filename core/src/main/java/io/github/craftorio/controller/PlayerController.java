package io.github.craftorio.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.craftorio.model.entity.Player;
import io.github.craftorio.view.CameraManager;

public class PlayerController {
    private final Player player;
    private final CameraManager camera;

    public PlayerController(Player player, CameraManager camera) {
        this.player = player;
        this.camera = camera;
    }

    public void update(float delta) {
        float dx = 0;
        float dy = 0;
        float dZoom = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) dx = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) dy = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) dy = -1;

        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) dZoom = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS)) dZoom = -1;

        if (dx != 0 || dy != 0) {
            player.updatePosition(delta, dx, dy);
            player.stopDigging();
        } else {
            player.stop();
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                player.tryDig();
            } else {
                player.stopDigging();
            }
        }

        if (dZoom != 0) {
            camera.addZoom(dZoom * delta * 5.0f);
        }
    }
}
