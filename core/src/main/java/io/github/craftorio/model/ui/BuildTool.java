package io.github.craftorio.model.ui;

import io.github.craftorio.model.BuildingManager;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.Direction;

import java.awt.Point;

public class BuildTool {
    private BuildingType selectedType = null;
    private Direction currentRotation = Direction.UP;
    private final Point hoverPosition = new Point(0, 0);

    private final BuildingManager buildingManager;

    public BuildTool(BuildingManager buildingManager){
        this.buildingManager = buildingManager;
    }

    public void selectBuilding(BuildingType type) {
        this.selectedType = type;
        this.currentRotation = Direction.UP;
    }

    public boolean tryBuild(){
        if (!isActive())return false;

        return buildingManager.tryPlaceBuilding(selectedType, hoverPosition, currentRotation);
    }

    public boolean isValidPlace(){
        if (!isActive())return false;
        return buildingManager.isValidPlace(selectedType, hoverPosition, currentRotation);
    }

    public void clearSelection() {
        this.selectedType = null;
    }

    public boolean isActive() {
        return selectedType != null;
    }

    public void updateHoverPosition(int gridX, int gridY) {
        hoverPosition.setLocation(gridX, gridY);
    }

    public void rotateRight() {
        if (isActive()) {
            currentRotation = currentRotation.next();
        }
    }

    public BuildingType getSelectedType() { return selectedType; }
    public Point getHoverPosition() { return hoverPosition; }
    public Direction getCurrentRotation() { return currentRotation; }
}
