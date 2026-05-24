package io.github.craftorio.view;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.craftorio.model.entity.Player;
import io.github.craftorio.model.core.WorldMap;

public class CameraManager {
    private static final float MIN_WIDTH = 32;
    private static final float MIN_HEIGHT = 18;
    private static final float MAX_WIDTH = 48;
    private static final float MAX_HEIGHT = 27;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Player player;
    private final WorldMap worldMap;

    public CameraManager(Player player, WorldMap worldMap) {
        this.player = player;
        this.worldMap = worldMap;
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(MIN_WIDTH, MIN_HEIGHT, MAX_WIDTH, MAX_HEIGHT, camera);
    }

    public void update() {
        float visibleWidth = camera.viewportWidth * camera.zoom;
        float visibleHeight = camera.viewportHeight * camera.zoom;
        float halfWidth = visibleWidth / 2f;
        float halfHeight = visibleHeight / 2f;

        camera.position.x = MathUtils.clamp(player.playerX, halfWidth, worldMap.getWidth() - halfWidth);
        camera.position.y = MathUtils.clamp(player.playerY, halfHeight, worldMap.getHeight() - halfHeight);
        camera.update();
    }

    public OrthographicCamera getCamera() { return camera; }
    public Viewport getViewport() { return viewport; }

    public Vector3 unproject(Vector3 screenCoords) {
        return viewport.unproject(screenCoords);
    }

    public void addZoom(float amount) {
        camera.zoom = MathUtils.clamp(camera.zoom + amount, 0.2f, 100f);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
