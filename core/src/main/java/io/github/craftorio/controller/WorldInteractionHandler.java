package io.github.craftorio.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.production.Craftable;
import io.github.craftorio.model.building.production.Rocket;
import io.github.craftorio.model.core.BuildingManager;
import io.github.craftorio.view.CameraManager;
import io.github.craftorio.view.ui.CraftingUI;
import io.github.craftorio.view.ui.RocketUI;

public class WorldInteractionHandler extends InputAdapter {
    private final CameraManager camera;
    private final BuildingManager buildingManager;
    private final CraftingUI craftingUI;
    private final RocketUI rocketUI;

    private Building hoveredBuilding = null;

    public WorldInteractionHandler(CameraManager camera, BuildingManager buildingManager, CraftingUI craftingUI, RocketUI rocketUI) {
        this.camera = camera;
        this.buildingManager = buildingManager;
        this.craftingUI = craftingUI;
        this.rocketUI = rocketUI;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 worldCoords = camera.getCamera().unproject(new Vector3(screenX, screenY, 0));
        int gridX = (int) worldCoords.x;
        int gridY = (int) worldCoords.y;

        hoveredBuilding = buildingManager.getRegistry().getBuildingAt(gridX, gridY);
        return false;
    }

    public Building getHoveredBuilding() {
        return hoveredBuilding;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            Vector3 worldCoords = camera.getCamera().unproject(new Vector3(screenX, screenY, 0));
            int gridX = (int) worldCoords.x;
            int gridY = (int) worldCoords.y;

            Building clickedBuilding = buildingManager.getRegistry().getBuildingAt(gridX, gridY);

            if (clickedBuilding instanceof Rocket) {
                rocketUI.show((Rocket) clickedBuilding);
                return true;
            }

            if (clickedBuilding instanceof Craftable) {
                craftingUI.show((Craftable) clickedBuilding);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            craftingUI.close();
            rocketUI.close();
            return true;
        }
        return false;
    }
}
