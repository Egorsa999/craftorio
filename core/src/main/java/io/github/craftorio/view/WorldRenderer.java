package io.github.craftorio.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import io.github.craftorio.model.*;
import io.github.craftorio.model.building.*;
import io.github.craftorio.model.enemy.Enemy;
import io.github.craftorio.model.enemy.PathFinder;
import io.github.craftorio.model.enemy.WaveSpawner;
import io.github.craftorio.model.generator.ResourceType;
import io.github.craftorio.model.generator.TerrainType;
import io.github.craftorio.model.ui.BuildTool;
import io.github.craftorio.view.renderer.BeltRenderer;

import javax.swing.*;
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
    private final WaveSpawner waveSpawner;
    private final PathFinder pathFinder;
    private SimulationEngine engine;

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;


    private final TextureLoad Textures;

    private float stateTime = 0f;

    private final Set<Building> renderedBuildingsThisFrame = new HashSet<>();

    public WorldRenderer(TextureLoad Textures, CameraManager cameraManager, Player player, WorldMap worldMap,
                         BuildTool buildTool, BuildingFactory factory, BuildingRegistry registry, WaveSpawner waveSpawner, PathFinder pathFinder) {
        this.cameraManager = cameraManager;
        this.player = player;
        this.worldMap = worldMap;
        this.buildTool = buildTool;
        this.factory = factory;
        this.registry = registry;
        this.waveSpawner = waveSpawner;
        this.pathFinder = pathFinder;

        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        this.Textures = Textures;

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
    }

    public void setSimulationEngine(SimulationEngine simulationEngine) {
        this.engine = simulationEngine;
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
        drawVisibleEnemies(bounds);
        drawPlayer();
        drawBullets();

        drawBuildPreviewTextures();

        batch.end();
    }

    private void drawBullets() {
        for (io.github.craftorio.model.Bullet b : engine.getBullets()) {
            float bulletSize = 0.5f;
            TextureRenderer.draw(
                batch, Textures.get("bullet"),
                b.getX() - (bulletSize / 2f), b.getY() - (bulletSize / 2f),
                bulletSize, bulletSize,
                b.getRotationDeg(),
                null, stateTime
            );
        }
    }

    private void drawItems(VisibleBounds bounds) {
        for (int x = bounds.startX; x < bounds.endX; x++) {
            for (int y = bounds.endY - 1; y >= bounds.startY; y--) {
                Building current = registry.getBuildingAt(x, y);
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
                Building current = registry.getBuildingAt(x, y);

                if (current == null || !renderedBuildingsThisFrame.add(current)) continue;

                if (current instanceof Belt belt) {
                    BeltRenderer.drawBackground(null, batch, Textures.getConveyorTextures(), belt, stateTime, 1f);
                    continue;
                }

                if (current instanceof Turret turret) {
                    TextureRenderer.draw(
                        batch, Textures.get(current.type),
                        (float)current.anchor.x, (float)current.anchor.y,
                        current.type.getWidth(), current.type.getHeight(),
                        turret.getRotationDeg(), null, stateTime
                    );
                    continue;
                }

                float width = current.type.getWidth();
                float height = current.type.getHeight();

                TextureRenderer.drawBuilding(
                    batch, Textures.get(current.type),
                    (float)current.anchor.x, (float)current.anchor.y,
                    width, height,
                    current.direction,
                    null, stateTime
                );
            }
        }
    }

    public void drawVisibleEnemies(VisibleBounds bounds) {
        for (Enemy enemy : waveSpawner.getEnemies()){
            float x = enemy.getX() - 1/2f;
            float y = enemy.getY() - 1/2f;

            float drawWidth = PLAYER_SIZE;

            if (enemy.getDirection() == Direction.LEFT) {
                x += 1f;
                drawWidth *= -1f;
            }
            TextureRenderer.draw(batch, Textures.get("slime"), x, y, drawWidth, PLAYER_SIZE,
                0, null, stateTime);
        }

        Color colorFilter = new Color(1f, 1f, 1f, 0.2f);

        for (int x = bounds.startX; x < bounds.endX; x++) {
            for (int y = bounds.startY; y < bounds.endY; y++) {

                float rotaion = 1000;

                int dx = pathFinder.getFlowDirection(x, y).x;
                int dy =pathFinder.getFlowDirection(x, y).y;

                if (dx == 1 && dy == 0)rotaion = 0;
                else if (dx == 1 && dy == 1)rotaion = 45;
                else if (dx == 1 && dy == -1)rotaion = -45;
                else if (dx == -1 && dy == 0)rotaion = 180;
                else if (dx == -1 && dy == 1)rotaion = 135;
                else if (dx == -1 && dy == -1)rotaion = 225;
                else if (dx == 0 && dy == 1)rotaion = 90;
                else if (dx == 0 && dy == -1)rotaion = 270;

                if (rotaion == 1000)continue;

//                TextureRenderer.draw(
//                    batch, Textures.get("arrow"),
//                    x, y,
//                    1f, 1f,
//                    rotaion,
//                    colorFilter, 0f);
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
        if (buildTool.eraseMode) {
            float xl = Math.min(buildTool.getStartHoverPosition().x, buildTool.getHoverPosition().x);
            float xr = Math.max(buildTool.getStartHoverPosition().x, buildTool.getHoverPosition().x);
            float yl = Math.min(buildTool.getStartHoverPosition().y, buildTool.getHoverPosition().y);
            float yr = Math.max(buildTool.getStartHoverPosition().y, buildTool.getHoverPosition().y);
            if (xl == -1) return;
//            System.out.println("ERASE: " + buildTool.getStartHoverPosition() + " " + buildTool.getHoverPosition());
            Color colorFilter = new Color(1f, 0f, 0f, 0.5f);
            batch.setColor(colorFilter);
            batch.draw(
                Textures.getBlank().getFirstFrame(),
                xl, yl,
                xr - xl + 1, yr - yl + 1
            );
            batch.setColor(Color.WHITE);
            return;
        }

        Array<Point> positions = buildTool.getHoverPositions();
        BuildingType type = buildTool.getSelectedType();
        Direction rotation = buildTool.getCurrentRotation();

        float width = type.getWidth();
        float height = type.getHeight();


        Color colorFilter = new Color(1f, 1f, 1f, 0.5f);
        if (!buildTool.isValidPlace())colorFilter = new Color(1f, 0f, 0f, 0.5f);
        for (Point pos : positions){
            if (type == BuildingType.BELT) {
                BeltRenderer.drawBackground(colorFilter, batch, Textures.getConveyorTextures(), (Belt) factory.createBuilding(BuildingType.BELT, new Point(pos.x, pos.y), rotation), stateTime, 0.5f);
                continue;
            }
            TextureRenderer.drawBuilding(
                batch, Textures.get(type),
                (float)pos.getX(), (float)pos.getY(),
                type.getWidth(), type.getHeight(),
                rotation, colorFilter,
                0f
            );
        }
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

    private record VisibleBounds(int startX, int endX, int startY, int endY) {}
}
