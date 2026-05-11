package io.github.craftorio.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.Player;
import io.github.craftorio.model.WorldMap;
import io.github.craftorio.model.building.*;
import io.github.craftorio.model.generator.ResourceType;
import io.github.craftorio.model.generator.TerrainType;
import io.github.craftorio.model.ui.BuildTool;
import io.github.craftorio.view.renderer.BeltRenderer;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class WorldRenderer {
    private static final float PLAYER_SIZE = 1f;

    private final CameraManager cameraManager;
    private final Player player;
    private final WorldMap worldMap;
    private final BuildTool buildTool;
    private final BuildingFactory factory;
    private final BuildingRegistry registry;

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final TextureAtlas atlas;

    private final TextureLoad Textures;

    private float stateTime = 0f;

    private final Set<Building> renderedBuildingsThisFrame = new HashSet<>();

    public WorldRenderer(CameraManager cameraManager, Player player, WorldMap worldMap,
                         BuildTool buildTool, BuildingFactory factory, BuildingRegistry registry) {
        this.cameraManager = cameraManager;
        this.player = player;
        this.worldMap = worldMap;
        this.buildTool = buildTool;
        this.factory = factory;
        this.registry = registry;

        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        this.atlas = new TextureAtlas(Gdx.files.internal("atlas/main_atlas.atlas"));
        Textures = new TextureLoad(atlas);

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
    }

    public void render() {
        stateTime += Gdx.graphics.getDeltaTime();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cameraManager.update();
        OrthographicCamera camera = cameraManager.getCamera();

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        VisibleBounds bounds = calculateVisibleBounds(camera);

        batch.begin();
        batch.enableBlending();

        drawVisibleMap(bounds);
        drawVisibleBuildings(bounds);
        drawItems(bounds);
        drawPlayer();

        drawBuildPreviewTextures();

        batch.end();
    }

    private void drawItems(VisibleBounds bounds) {
        for (int x = bounds.startX; x < bounds.endX; x++) {
            for (int y = bounds.endY - 1; y >= bounds.startY; y--) {
                Building current = registry.getBuildingAt(new Point(x, y));
                if (current instanceof Belt belt) {
                    BeltRenderer.drawItems(batch, Textures, belt, Belt.getAnimationOffset(), 1f);
                }
            }
        }
    }

    private void drawVisibleMap(VisibleBounds bounds) {
        for (int x = bounds.startX; x < bounds.endX; x++) {
            for (int y = bounds.startY; y < bounds.endY; y++) {
                TerrainType terrainType = worldMap.getCell(x, y).getTerrainType();
                TextureRenderer.draw(batch, Textures.get(terrainType), x, y, 1, 1, 0, null, stateTime);
                ResourceType type = worldMap.getCell(x, y).getResourceType();
                if (type != ResourceType.NONE) {
                    TextureRenderer.draw(batch, Textures.get(type), x, y, 1, 1, 0, null, stateTime);
                }
            }
        }
    }

    private void drawVisibleBuildings(VisibleBounds bounds) {
        renderedBuildingsThisFrame.clear();

        for (int x = bounds.startX; x < bounds.endX; x++) {
            for (int y = bounds.startY; y < bounds.endY; y++) {
                Building current = registry.getBuildingAt(new Point(x, y));

                if (current == null || !renderedBuildingsThisFrame.add(current)) continue;

                if (current instanceof Belt belt) {
                    BeltRenderer.drawBackground(batch, Textures.getConveyorTextures(), belt, Belt.getAnimationOffset(), 1f);
                    continue;
                }

                TextureRenderer.draw(
                    batch, Textures.get(current.type),
                    x, y,
                    current.getWidth(), current.getHeight(),
                    current.direction.to_degrees(),
                    null, stateTime
                );
            }
        }
    }

    private void drawPlayer() {
        String baseName = "player";
        if (player.isMoving()) baseName += "_run";
        else baseName += "_idle";

        boolean flipX = false;

        if (player.getDirection() == Direction.LEFT)flipX = true;

        switch (player.getDirection()){
            case UP:
                baseName += "_up";
                break;
            case DOWN:
                baseName += "_down";
                break;
            case LEFT, RIGHT:
                baseName += "_side";
                break;
        }

        float x = player.playerX - (PLAYER_SIZE / 2f);
        float y = player.playerY - (PLAYER_SIZE / 2f);

        float drawWidth = PLAYER_SIZE;

        if (flipX) {
            x += PLAYER_SIZE;
            drawWidth = -PLAYER_SIZE;
        }

        TextureRenderer.draw(
            batch, Textures.get(baseName),
            x, y,
            drawWidth, PLAYER_SIZE,
            0, null,
            stateTime
        );
    }

    private void drawBuildPreviewTextures() {
        if (!buildTool.isActive()) return;

        Point pos = buildTool.getHoverPosition();
        BuildingType type = buildTool.getSelectedType();
        Direction rotation = buildTool.getCurrentRotation();

        float width = factory.calculateOccupiedWidth(type, rotation);
        float height = factory.calculateOccupiedHeight(type, rotation);

        if (type == BuildingType.BELT) {
            BeltRenderer.drawBackground(batch, Textures.getConveyorTextures(), (Belt) factory.createBuilding(BuildingType.BELT, new Point(pos.x, pos.y), rotation), Belt.getAnimationOffset(), 0.5f);
            return;
        }

        TextureRenderer.draw(
            batch, Textures.get(type),
            (float)pos.getX(), (float)pos.getY(),
            width, height,
            rotation.to_degrees(), new Color(1f, 1f, 1f, 0.5f),
            0f
        );
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
        atlas.dispose();
    }

    private record VisibleBounds(int startX, int endX, int startY, int endY) {}
}
