package io.github.craftorio.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import io.github.craftorio.model.building.BuildingFactory;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.ui.BuildTool;
import io.github.craftorio.view.CameraManager;

import java.awt.Point;

public class BuildInputHandler extends InputAdapter {
    private final BuildTool buildTool;
    private final CameraManager camera;
    private final BuildingFactory factory;
    private final Vector3 tempCoords = new Vector3();

    public BuildInputHandler(BuildTool buildTool, CameraManager camera, BuildingFactory factory) {
        this.buildTool = buildTool;
        this.camera = camera;
        this.factory = factory;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT) {
            buildTool.eraseMode = true;
            select(BuildingType.BELT);
            return true;
        }
        if (keycode == Input.Keys.NUM_1) return select(BuildingType.MINER);
        if (keycode == Input.Keys.NUM_2) return select(BuildingType.BELT);
        if (keycode == Input.Keys.NUM_3) return select(BuildingType.HORIZONTAL_MINER);
        if (keycode == Input.Keys.NUM_4) return select(BuildingType.ASSEMBLER);
        if (keycode == Input.Keys.NUM_5) return select(BuildingType.TURRET);
        if (keycode == Input.Keys.NUM_6) return select(BuildingType.JUNCTION);
        if (keycode == Input.Keys.NUM_7) return select(BuildingType.ROUTER);

        if (keycode == Input.Keys.ESCAPE && buildTool.isActive()) {
            buildTool.clearSelection();
            return true;
        }

        if (keycode == Input.Keys.R) {
            if (buildTool.isActive()) {
                buildTool.rotateRight();
                updateHoverPosition(Gdx.input.getX(), Gdx.input.getY());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT) {
            buildTool.eraseMode = false;
            buildTool.updSelectedType(null);
            buildTool.updateStartHoverPosition(new Point(-1, -1));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (buildTool.isActive()) {
            updateHoverPosition(screenX, screenY);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return mouseMoved(screenX, screenY);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && buildTool.isActive()) {
            updateHoverPosition(screenX, screenY);
            buildTool.updateStartHoverPosition(getCurrentHoveredCoords());
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && buildTool.isActive()) {
            if (buildTool.eraseMode) {
                buildTool.tryErase();
            } else {
                buildTool.tryBuild();
            }
            buildTool.updateStartHoverPosition(new Point(-1, -1));
            return true;
        }
        return false;
    }

    private boolean select(BuildingType type) {
        buildTool.selectBuilding(type);
        updateHoverPosition(Gdx.input.getX(), Gdx.input.getY());
        return true;
    }

    public void updateHoverPosition(int screenX, int screenY) {
        if (!buildTool.isActive()) return;
        tempCoords.set(screenX, screenY, 0);
        camera.unproject(tempCoords);
        buildTool.updateHoverPosition(getCurrentHoveredCoords());
    }

    private Point getCurrentHoveredCoords() {
        BuildingType type = buildTool.getSelectedType();
        Direction rotation = buildTool.getCurrentRotation();

        int width = factory.calculateRenderWidth(type, rotation);
        int height = factory.calculateRenderHeight(type, rotation);

        float halfWidth = width / 2f;
        float halfHeight = height / 2f;

        int gridX = MathUtils.round(tempCoords.x - halfWidth);
        int gridY = MathUtils.round(tempCoords.y - halfHeight);

        return new Point(gridX, gridY);
    }
}
