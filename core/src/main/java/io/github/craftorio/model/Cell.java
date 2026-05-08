package io.github.craftorio.model;

import io.github.craftorio.model.building.Building;

public class Cell {
    public enum resourceType {NONE, IRON, COPPER};

    private final int row;
    private final int col;
    private resourceType resourceType;
    private boolean isOccupied;
    private Building occupiedBuilding;

    public Cell(int row, int col, resourceType resourceType) {
        this.row = row;
        this.col = col;
        this.resourceType = resourceType;
        this.occupiedBuilding = null;
        this.isOccupied = false;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public resourceType getResourceType() {
        return this.resourceType;
    }

    public boolean isOccupied() {
        return this.isOccupied;
    }

    public void updateOccupiedBuilding(Building building) {
        if (building == null) {
            this.isOccupied = false;
            this.occupiedBuilding = null;
        } else {
            this.isOccupied = true;
            this.occupiedBuilding = building;
        }
    }

    public Building getOccupiedBuilding() {
        return this.occupiedBuilding;
    }

    public void updateResourceType(resourceType resourceType) {
        this.resourceType = resourceType;
    }
}
