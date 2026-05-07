package io.github.craftorio.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.craftorio.model.Player;
import io.github.craftorio.model.WorldMap;

public class PlayerCamera {
    private final Viewport viewport;
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;

    private final Player player;
    private final WorldMap worldMap;

    private final float MIN_WIDTH = 32;
    private final float MIN_HEIGHT = 18;
    private final float MAX_WIDTH = 48;
    private final float MAX_HEIGHT = 27;
    private final float PLAYER_SIZE = 1f;

    public PlayerCamera(Player player, WorldMap worldMap) {
        this.player = player;
        this.worldMap = worldMap;

        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(MIN_WIDTH, MIN_HEIGHT, MAX_WIDTH, MAX_HEIGHT, camera);
        this.shapeRenderer = new ShapeRenderer();

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateCameraPosition();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawVisibleMap();
        drawPlayer();

        shapeRenderer.end();
    }

    private void updateCameraPosition() {
        camera.position.x = player.playerX;
        camera.position.y = player.playerY;

        float visibleWidth = camera.viewportWidth * camera.zoom;
        float visibleHeight = camera.viewportHeight * camera.zoom;
        float halfWidth = visibleWidth / 2f;
        float halfHeight = visibleHeight / 2f;

        camera.position.x = MathUtils.clamp(camera.position.x, halfWidth, worldMap.getWidth() - halfWidth);
        camera.position.y = MathUtils.clamp(camera.position.y, halfHeight, worldMap.getHeight() - halfHeight);

        camera.update();
    }

    private void drawVisibleMap() {
        float visibleWidth = camera.viewportWidth * camera.zoom;
        float visibleHeight = camera.viewportHeight * camera.zoom;

        int startX = (int) Math.max(0, (camera.position.x - visibleWidth / 2f) - 1);
        int endX = (int) Math.min(worldMap.getWidth(), (camera.position.x + visibleWidth / 2f) + 1);

        int startY = (int) Math.max(0, (camera.position.y - visibleHeight / 2f) - 1);
        int endY = (int) Math.min(worldMap.getHeight(), (camera.position.y + visibleHeight / 2f) + 1);

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                switch (worldMap.getCell(x, y).getResourseType()) {
                    case NONE:
                        shapeRenderer.setColor(Color.SLATE);
                        break;
                    case IRON:
                        shapeRenderer.setColor(Color.DARK_GRAY);
                        break;
                    case COPPER:
                        shapeRenderer.setColor(Color.BROWN);
                        break;
                }
                shapeRenderer.rect(x, y, 1, 1);
            }
        }
    }

    private void drawPlayer() {
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.rect(
            player.playerX - (PLAYER_SIZE / 2),
            player.playerY - (PLAYER_SIZE / 2),
            PLAYER_SIZE,
            PLAYER_SIZE
        );
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
