package io.github.craftorio.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.craftorio.model.Player;
import com.badlogic.gdx.graphics.Color;

import java.awt.*;

public class PlayerCamera {
    private Viewport viewport;
    private OrthographicCamera camera;

    private final float MIN_WIDTH = 32;
    private final float MIN_HEIGHT = 18;

    private final float MAX_WIDTH = 48;
    private final float MAX_HEIGHT = 27;


    private final ShapeRenderer shapeRenderer;

    Player player;
    public PlayerCamera(Player player){
        this.player = player;
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(MIN_WIDTH, MIN_HEIGHT, MAX_WIDTH, MAX_HEIGHT, camera);
        this.shapeRenderer = new ShapeRenderer();

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1); // Gray
    }

    public void render(){
        camera.position.x = player.playerX;
        camera.position.y = player.playerY;

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.SLATE);
        shapeRenderer.rect(
            camera.position.x - (camera.viewportWidth * camera.zoom) / 2f,
            camera.position.y - (camera.viewportHeight * camera.zoom) / 2f,
            camera.viewportWidth * camera.zoom,
            camera.viewportHeight * camera.zoom
        );

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(2, 2, 1, 1);

        float playerSize = 1f;

        shapeRenderer.setColor((float)Math.random(), (float)Math.random(),
            (float)Math.random(), 1);

        shapeRenderer.rect(
            player.playerX - (playerSize / 2),
            player.playerY - (playerSize / 2),
            playerSize,
            playerSize
        );

        shapeRenderer.end();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

}
