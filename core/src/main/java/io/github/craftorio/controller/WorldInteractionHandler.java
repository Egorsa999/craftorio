package io.github.craftorio.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.production.Assembler;
import io.github.craftorio.model.building.production.CraftModule;
import io.github.craftorio.model.building.production.Craftable;
import io.github.craftorio.model.core.BuildingManager;
import io.github.craftorio.view.CameraManager;
import io.github.craftorio.view.ui.CraftingUI;

public class WorldInteractionHandler extends InputAdapter {
    private final CameraManager camera;
    private final BuildingManager buildingManager;
    private final CraftingUI craftingUI;

    public WorldInteractionHandler(CameraManager camera, BuildingManager buildingManager, CraftingUI craftingUI) {
        this.camera = camera;
        this.buildingManager = buildingManager;
        this.craftingUI = craftingUI;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            Vector3 worldCoords = camera.getCamera().unproject(new Vector3(screenX, screenY, 0));
            int gridX = (int) worldCoords.x;
            int gridY = (int) worldCoords.y;

            Building clickedBuilding = buildingManager.getRegistry().getBuildingAt(gridX, gridY);

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
            return true;
        }
        return false;
    }
}
