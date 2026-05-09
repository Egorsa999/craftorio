package io.github.craftorio.view.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.Player;
import io.github.craftorio.model.WorldMap;
import io.github.craftorio.model.building.*;
import io.github.craftorio.model.generator.ResourceType;
import io.github.craftorio.model.ui.BuildTool;
import io.github.craftorio.view.item_renderer.BeltRenderer;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlayerCamera {
    private final Viewport viewport;
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;
    private final BuildTool buildTool;
    private final BuildingFactory factory;
    private final BuildingRegistry registry;
    private final TextureAtlas atlas;
    private final Map<ResourceType, TextureRegion> tileTextures;

    private final TextureRegion playerTexture;
    private final TextureRegion whitePixel;
    private final TextureRegion conveyorTexture;

    private final Player player;
    private final WorldMap worldMap;
    private final SpriteBatch batch;

    private final float MIN_WIDTH = 32;
    private final float MIN_HEIGHT = 18;
    private final float MAX_WIDTH = 48;
    private final float MAX_HEIGHT = 27;
    private final float PLAYER_SIZE = 1f;


    public PlayerCamera(Player player, WorldMap worldMap, BuildTool buildTool, BuildingFactory factory,
                        BuildingRegistry registry) {
        tileTextures = new HashMap<>();
        batch = new SpriteBatch();

        this.atlas = new TextureAtlas(Gdx.files.internal("atlas/main_atlas.atlas"));
        this.playerTexture = atlas.findRegion("player");
        this.whitePixel = atlas.findRegion("blank");
        this.conveyorTexture = atlas.findRegion("conveyor");
        tileTextures.put(ResourceType.IRON, atlas.findRegion("iron"));
        tileTextures.put(ResourceType.COPPER, atlas.findRegion("copper"));
        tileTextures.put(ResourceType.NONE, atlas.findRegion("ground"));


        this.player = player;
        this.worldMap = worldMap;

        this.registry = registry;
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(MIN_WIDTH, MIN_HEIGHT, MAX_WIDTH, MAX_HEIGHT, camera);
        this.shapeRenderer = new ShapeRenderer();
        this.buildTool = buildTool;
        this.factory = factory;

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        updateCameraPosition();

        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();

        batch.begin();

        drawVisibleMap();
        drawVisibleBuildings();
        drawBuildPreview();
        drawPlayer();

        batch.end();
    }

    private void drawBuildPreview() {
        if (!buildTool.isActive()) return;

        Point pos = buildTool.getHoverPosition();
        BuildingType type = buildTool.getSelectedType();
        Direction rotation = buildTool.getCurrentRotation();

        float width = factory.calculateOccupiedWidth(type, rotation);
        float height = factory.calculateOccupiedHeight(type, rotation);

        //System.out.println(pos.x + " " + pos.y);
        if (type == BuildingType.BELT) {
            BeltRenderer.draw(batch, conveyorTexture, (Belt) factory.createBuilding(BuildingType.BELT, new Point(pos.x, pos.y), rotation), Belt.getAnimationOffset(), 0.5f);
            return;
        }
        batch.setColor(0.2f, 0.8f, 0.2f, 0.5f);
        batch.draw(whitePixel, pos.x, pos.y, width, height);
        batch.setColor(Color.WHITE);
    }

    public Vector3 unproject(Vector3 screenCoords) {
        return viewport.unproject(screenCoords);
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
                ResourceType type = worldMap.getCell(x, y).getResourceType();
                TextureRegion region = tileTextures.get(type);

                if (region != null) {
                    batch.draw(region, x, y, 1, 1);
                }
            }
        }
    }

    private void drawVisibleBuildings() {
        float visibleWidth = camera.viewportWidth * camera.zoom;
        float visibleHeight = camera.viewportHeight * camera.zoom;

        int startX = (int) Math.max(0, (camera.position.x - visibleWidth / 2f) - 1);
        int endX = (int) Math.min(worldMap.getWidth(), (camera.position.x + visibleWidth / 2f) + 1);

        int startY = (int) Math.max(0, (camera.position.y - visibleHeight / 2f) - 1);
        int endY = (int) Math.min(worldMap.getHeight(), (camera.position.y + visibleHeight / 2f) + 1);

        Set<Building> used = new HashSet<>();

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                Building current = registry.getBuildingAt(new Point(x, y));
                if (current == null || used.contains(current))continue;
                if (current instanceof Belt currentBelt) {
                    BeltRenderer.draw(batch, conveyorTexture, currentBelt, Belt.getAnimationOffset(), 1f);
                    continue;
                }
                used.add(current);

                batch.setColor(Color.GREEN);
                batch.draw(whitePixel, current.getX(), current.getY(), current.getWidth(), current.getHeight());
                batch.setColor(Color.WHITE);
            }
        }
    }

    private void drawPlayer() {
        if (playerTexture != null) {
            batch.draw(
                playerTexture,
                player.playerX - (PLAYER_SIZE / 2),
                player.playerY - (PLAYER_SIZE / 2),
                PLAYER_SIZE,
                PLAYER_SIZE
            );
        }
    }

    public void addZoom(float amount) {
        camera.zoom += amount;
        camera.zoom = MathUtils.clamp(camera.zoom, 0.2f, 2.5f);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void dispose() {
        batch.dispose();
        atlas.dispose();
    }
}
