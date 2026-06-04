package io.github.craftorio.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.core.WorldMap;
import io.github.craftorio.model.enemy.WaveSpawner;
import io.github.craftorio.model.entity.Bullet;
import io.github.craftorio.model.entity.Player;
import io.github.craftorio.ui.PreviewState;
import io.github.craftorio.view.layers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WorldRenderer {
    private final CameraManager cameraManager;
    private final WorldMap worldMap;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final List<LayerRenderer> layerRenderers = new ArrayList<>();
    private final List<ShapeLayerRenderer> shapeLayerRenderers = new ArrayList<>();

    private float stateTime = 0f;

    public WorldRenderer(CameraManager cameraManager, WorldMap worldMap, BuildingRegistry registry,
                         WaveSpawner waveSpawner, TextureLoad textures, Player player, List<Bullet> bullets,
                         Supplier<PreviewState> previewStateSupplier) {
        this.cameraManager = cameraManager;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.worldMap = worldMap;

        layerRenderers.add(new MapLayerRenderer(worldMap, textures));
        layerRenderers.add(new BeltLayerRenderer(registry, textures));
        layerRenderers.add(new PipeLayerRenderer(registry, textures));
        layerRenderers.add(new ItemLayerRenderer(registry, textures));
        layerRenderers.add(new BuildingLayerRenderer(registry, textures));
        layerRenderers.add(new EnemyLayerRenderer(waveSpawner, textures));
        layerRenderers.add(new PlayerLayerRenderer(player, textures));
        layerRenderers.add(new BulletLayerRenderer(bullets, textures));
        layerRenderers.add(new BuildingPreviewLayerRenderer(previewStateSupplier, textures));

        shapeLayerRenderers.add(new WireLayerRenderer(registry));
        shapeLayerRenderers.add(new LaserLayerRenderer(registry));

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
    }

    public void render() {
        cameraManager.getViewport().apply();
        stateTime += Gdx.graphics.getDeltaTime();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cameraManager.update();

        OrthographicCamera camera = cameraManager.getCamera();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        VisibleBounds bounds = calculateVisibleBounds(camera);

        batch.begin();
        batch.enableBlending();

        for (LayerRenderer layer : layerRenderers){
            layer.render(batch, bounds, stateTime);
        }

        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (ShapeLayerRenderer layer : shapeLayerRenderers){
            layer.render(shapeRenderer, bounds, stateTime);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private VisibleBounds calculateVisibleBounds(OrthographicCamera camera) {
        float visibleWidth = camera.viewportWidth * camera.zoom;
        float visibleHeight = camera.viewportHeight * camera.zoom;

        int startX = (int) Math.max(0, (camera.position.x - visibleWidth / 2f) - 1);
        int endX = (int) Math.min(worldMap.getWidth(), (camera.position.x + visibleWidth / 2f) + 1);
        int startY = (int) Math.max(0, (camera.position.y - visibleHeight / 2f) - 1);
        int endY = (int) Math.min(worldMap.getHeight(), (camera.position.y + visibleHeight / 2f) + 1);

        return new VisibleBounds(startX, endX, startY, endY);
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
    }
}
