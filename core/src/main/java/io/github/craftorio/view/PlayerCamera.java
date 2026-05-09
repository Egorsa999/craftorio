package io.github.craftorio.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.Player;
import io.github.craftorio.model.WorldMap;
import io.github.craftorio.model.building.*;
import io.github.craftorio.model.ui.BuildTool;
import io.github.craftorio.view.renderer.BeltRenderer;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerCamera {
    private final Viewport viewport;
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;
    private final BuildTool buildTool;
    private final BuildingFactory factory;
    private final BuildingRegistry registry;

    private final Player player;
    private final WorldMap worldMap;

    private final float MIN_WIDTH = 32;
    private final float MIN_HEIGHT = 18;
    private final float MAX_WIDTH = 48;
    private final float MAX_HEIGHT = 27;
    private final float PLAYER_SIZE = 1f;

    public PlayerCamera(Player player, WorldMap worldMap, BuildTool buildTool, BuildingFactory factory,
                        BuildingRegistry registry) {
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
        shapeRenderer.setProjectionMatrix(camera.combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawVisibleMap();
        drawVisibleBuildings();
        drawBuildPreview();
        drawPlayer();

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
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
            BeltRenderer.drawBackground(shapeRenderer, (Belt) factory.createBuilding(BuildingType.BELT, new Point(pos.x, pos.y),  rotation), Belt.getAnimationOffset(), 0.5f);
            return;
        }
        shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 0.5f);

        shapeRenderer.rect(pos.x, pos.y, width, height);
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
                switch (worldMap.getCell(x, y).getResourceType()) {
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
//                if (worldMap.getCell(x, y).getOccupiedBuilding() != null) {
//                    if ((worldMap.getCell(x, y).getOccupiedBuilding() instanceof Belt belt)) {
//                        BeltRenderer.draw(shapeRenderer, belt, Belt.getAnimationOffset());
//                    }
//                }
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
                    BeltRenderer.drawBackground(shapeRenderer, currentBelt, Belt.getAnimationOffset(), 1f);
                    continue;
                }
                used.add(current);
                shapeRenderer.setColor(Color.GREEN);
                shapeRenderer.rect(current.getX(), current.getY(), current.getWidth(), current.getHeight());
            }
        }
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                Building current = registry.getBuildingAt(new Point(x, y));
                if (current == null || used.contains(current))continue;
                if (current instanceof Belt currentBelt) {
                    BeltRenderer.drawItems(shapeRenderer, currentBelt, Belt.getAnimationOffset(), 1f);
                }
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

    public void addZoom(float amount) {
        camera.zoom += amount;
        camera.zoom = MathUtils.clamp(camera.zoom, 0.2f, 2.5f);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
