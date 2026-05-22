package io.github.craftorio.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import io.github.craftorio.GameConfig;
import io.github.craftorio.model.Player;
import io.github.craftorio.model.BuildingManager;
import io.github.craftorio.model.building.*;
import io.github.craftorio.model.ui.BuildTool;
import io.github.craftorio.view.CameraManager;
import io.github.craftorio.view.ui.AssemblerUI;

import java.awt.Point;

public class InputController extends InputAdapter {
    private final Player player;
    private final CameraManager camera;
    private final BuildTool buildTool;
    private final BuildingManager buildingManager;
    private final BuildingFactory factory;
    private AssemblerUI assemblerUI;

    private final Vector3 tempCoords = new Vector3();


    private int lastMouseX = 0;
    private int lastMouseY = 0;

    public InputController(Player player, CameraManager camera, BuildTool buildTool, BuildingManager buildingManager,
                           BuildingFactory factory, AssemblerUI assemblerUI) {
        this.player = player;
        this.camera = camera;
        this.buildTool = buildTool;
        this.buildingManager = buildingManager;
        this.factory = factory;
        this.assemblerUI = assemblerUI;
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
        if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT) {
            buildTool.eraseMode = true;
            buildTool.selectBuilding(BuildingType.BELT);
            mouseMoved(lastMouseX, lastMouseY);
            return true;
        }
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
        if (keycode == Input.Keys.NUM_4) {
            buildTool.selectBuilding(BuildingType.ASSEMBLER);
            mouseMoved(lastMouseX, lastMouseY);
            return true;
        }
        if (keycode == Input.Keys.NUM_6) {
            buildTool.selectBuilding(BuildingType.JUNCTION);
            mouseMoved(lastMouseX, lastMouseY);
            return true;
        }
        if (keycode == Input.Keys.NUM_7) {
            buildTool.selectBuilding(BuildingType.ROUTER);
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
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT) {
            buildTool.eraseMode = false;
            buildTool.updSelectedType(null);
            buildTool.updateStartHoverPosition(new Point(-1, -1));
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
            buildTool.updateStartHoverPosition(getCurrentHoveredCoords());
            return true;
        }
        if (button == Input.Buttons.LEFT) {
            Vector3 worldCoords = camera.getCamera().unproject(new Vector3(screenX, screenY, 0));

            int gridX = (int) (worldCoords.x);
            int gridY = (int) (worldCoords.y);

            System.out.println(gridX + " " + gridY);

            Building clickedBuilding = buildingManager.getRegistry().getBuildingAt(new Point(gridX, gridY));

            if (clickedBuilding instanceof Assembler) {
                assemblerUI.show((Assembler) clickedBuilding);
                return true;
            }

            return false;
        }
        return true;
    }
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        mouseMoved(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && buildTool.isActive()) {
            if (buildTool.eraseMode) {
                buildTool.tryErase();
            } else {
                buildTool.tryBuild();
            }
            buildTool.updateStartHoverPosition(-1, -1);
        }
        return true;
    }

    private Point getCurrentHoveredCoords(){
        BuildingType type = buildTool.getSelectedType();
        Direction rotation = buildTool.getCurrentRotation();

        int width = factory.calculateRenderWidth(type, rotation);
        int height = factory.calculateRenderHeight(type, rotation);

        float halfWidth = (width) / 2f;
        float halfHeight = (height) / 2f;

        int gridX = MathUtils.round((tempCoords.x - halfWidth));
        int gridY = MathUtils.round((tempCoords.y - halfHeight));

        return new Point(gridX, gridY);
    }
    private void updateHoveredGrid() {
        if (!buildTool.isActive()) return;

        tempCoords.set(lastMouseX, lastMouseY, 0);
        camera.unproject(tempCoords);
        buildTool.updateHoverPosition(getCurrentHoveredCoords());
    }
}
