package io.github.craftorio.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import io.github.craftorio.model.Player;
import io.github.craftorio.model.BuildingManager;
import io.github.craftorio.model.building.BuildingFactory;
import io.github.craftorio.model.building.BuildingType; // Замените на ваш импорт
import io.github.craftorio.model.building.Direction; // Замените на ваш импорт
import io.github.craftorio.model.ui.BuildTool;
import io.github.craftorio.view.CameraManager;

import java.awt.Point;

public class InputController extends InputAdapter {
    private final Player player;
    private final CameraManager camera;
    private final BuildTool buildTool;
    private final BuildingManager buildingManager;
    private final BuildingFactory factory;

    private final Vector3 tempCoords = new Vector3();


    private int lastMouseX = 0;
    private int lastMouseY = 0;

    public InputController(Player player, CameraManager camera, BuildTool buildTool, BuildingManager buildingManager,
                           BuildingFactory factory) {
        this.player = player;
        this.camera = camera;
        this.buildTool = buildTool;
        this.buildingManager = buildingManager;
        this.factory = factory;
    }

    public void update(float delta) {
        mouseMoved(lastMouseX, lastMouseY); // mega crutch
        touchDragged(lastMouseX, lastMouseY, 67); // mega crutch2
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
        }
        else {
            player.stop();
        }

        if (dZoom != 0) {
            camera.addZoom(dZoom * delta * 2.0f);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.NUM_1) {
            buildTool.selectBuilding(BuildingType.MINER);
            mouseMoved(lastMouseX, lastMouseY);
            return true;
        }
        if (keycode == Input.Keys.NUM_2) {
            buildTool.selectBuilding(BuildingType.BELT);
            mouseMoved(lastMouseX, lastMouseY);
            return true;
        }
        if (keycode == Input.Keys.NUM_3) {
            buildTool.selectBuilding(BuildingType.HORIZONTAL_MINER);
            mouseMoved(lastMouseX, lastMouseY);
            return true;
        }
        if (keycode == Input.Keys.ESCAPE) {
            buildTool.clearSelection();
            return true;
        }

        if (keycode == Input.Keys.R) {
            buildTool.rotateRight();
            updateHoveredGrid();
            return true;
        }


        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        lastMouseX = screenX;
        lastMouseY = screenY;

        if (buildTool.isActive()) {
            updateHoveredGrid();
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && buildTool.isActive()) {
            buildTool.tryBuild();
        }
        if (button == Input.Buttons.RIGHT){
            buildingManager.tryRemoveBuilding(new Point(MathUtils.floor(tempCoords.x),
                MathUtils.floor(tempCoords.y)));
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        System.out.println(screenX + " " + screenY);
        mouseMoved(screenX, screenY);
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && buildTool.isActive()) {
            buildTool.tryBuild();

            return true;
        }
        return false;
    }


    private void updateHoveredGrid() {
        if (!buildTool.isActive()) return;

        tempCoords.set(lastMouseX, lastMouseY, 0);
        camera.unproject(tempCoords);


        BuildingType type = buildTool.getSelectedType();
        Direction rotation = buildTool.getCurrentRotation();

        int width = factory.calculateRenderWidth(type, rotation);
        int height = factory.calculateRenderHeight(type, rotation);

        float halfWidth = (width) / 2f;
        float halfHeight = (height) / 2f;

        int gridX = MathUtils.round((tempCoords.x - halfWidth));
        int gridY = MathUtils.round((tempCoords.y - halfHeight));

        buildTool.updateHoverPosition(gridX, gridY);
    }

}
