package io.github.craftorio.model.ui;

import com.badlogic.gdx.utils.Array;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.BuildingFactory;
import io.github.craftorio.model.core.BuildingManager;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.item.ItemType;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class BuildTool {
    private BuildingType selectedType = null;
    private Direction currentRotation = Direction.UP;
    public boolean eraseMode = false;

    private final Point hoverPosition = new Point(0, 0);
    private final Point startDragPosition = new Point(0, 0);
    private boolean isDragging = false;

    private final BuildingManager buildingManager;
    private final BuildingFactory factory;
    private Building ghostBuilding = null;
    private final Inventory inventory;

    public BuildTool(BuildingManager buildingManager, BuildingFactory factory, Inventory inventory){
        this.buildingManager = buildingManager;
        this.factory = factory;
        this.inventory = inventory;
    }

    public void selectBuilding(BuildingType type) {
        this.selectedType = type;
        this.currentRotation = Direction.UP;
        this.eraseMode = false;
        updateGhostBuilding();
    }

    public void clearSelection() {
        this.selectedType = null;
        this.eraseMode = false;
        this.ghostBuilding = null;
        stopDrag();
    }

    public void rotateRight() {
        if (isActive() && !eraseMode) {
            currentRotation = currentRotation.next();
            updateGhostBuilding();
        }
    }

    public boolean isActive() {
        return selectedType != null || eraseMode;
    }

    private void updateGhostBuilding() {
        if (selectedType != null) {
            ghostBuilding = factory.createBuilding(selectedType, new Point(0, 0), currentRotation);
        }
    }

    public void updateHoverPosition(Point p) {
        hoverPosition.setLocation(p);
        if (isDragging && selectedType == BuildingType.BELT) {
            autoRotateBelt();
        }
        if (isDragging && selectedType == BuildingType.PIPE) {
            autoRotatePipe();
        }
    }

    private void autoRotatePipe() {
        int dx = hoverPosition.x - startDragPosition.x;
        int dy = hoverPosition.y - startDragPosition.y;

        if (dx == 0 && dy == 0) {
            return;
        }

        Direction oldRotation = currentRotation;

        if (Math.abs(dx) >= Math.abs(dy)) {
            currentRotation = dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            currentRotation = dy > 0 ? Direction.UP : Direction.DOWN;
        }

        if (oldRotation != currentRotation) {
            updateGhostBuilding();
        }
    }

    public void startDrag() {
        startDragPosition.setLocation(hoverPosition);
        isDragging = true;
    }

    public void stopDrag() {
        isDragging = false;
    }

    public boolean tryBuild() {
        if (!isValidPlace()) return false;
        HashMap<ItemType, Integer> map = new HashMap<>();
        for (Point point : getTargetPositions()) {
            for (Map.Entry<ItemType, Integer> entry : selectedType.getCost().entrySet()) {
                map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
            if (!buildingManager.tryPlaceBuilding(selectedType, point, currentRotation)) return false;
        }
        inventory.take(map);
        return true;
    }

    public void tryErase() {
        for (Point point : getTargetPositions()) {
            buildingManager.tryRemoveBuilding(point);
        }
    }

    public boolean isValidPlace() {
        if (!isActive() || eraseMode) return false;
        HashMap<ItemType, Integer> map = new HashMap<>();
        for (Point point : getTargetPositions()) {
            for (Map.Entry<ItemType, Integer> entry : selectedType.getCost().entrySet()) {
                map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
            if (!buildingManager.isValidPlace(selectedType, point, currentRotation)) return false;
        }
        return inventory.canTake(map);
    }

    private Array<Point> getTargetPositions() {
        Array<Point> positions = new Array<>();
        if (!isDragging) {
            positions.add(new Point(hoverPosition));
            return positions;
        }

        if (eraseMode) {
            int minX = Math.min(startDragPosition.x, hoverPosition.x);
            int maxX = Math.max(startDragPosition.x, hoverPosition.x);
            int minY = Math.min(startDragPosition.y, hoverPosition.y);
            int maxY = Math.max(startDragPosition.y, hoverPosition.y);
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) positions.add(new Point(x, y));
            }
        } else {
            int startX = startDragPosition.x, startY = startDragPosition.y;
            int endX = hoverPosition.x, endY = hoverPosition.y;

            if (Math.abs(endX - startX) >= Math.abs(endY - startY)) endY = startY;
            else endX = startX;

            int stepX = selectedType.getWidth(), stepY = selectedType.getHeight();
            if (currentRotation.to_degrees() % 180 != 0) {
                int temp = stepX; stepX = stepY; stepY = temp;
            }

            int dx = endX >= startX ? stepX : -stepX;
            int dy = endY >= startY ? stepY : -stepY;

            for (int x = startX; (dx > 0 ? x <= endX : x >= endX); x += dx) {
                for (int y = startY; (dy > 0 ? y <= endY : y >= endY); y += dy) positions.add(new Point(x, y));
            }
        }
        return positions;
    }

    private void autoRotateBelt() {
        int dx = hoverPosition.x - startDragPosition.x;
        int dy = hoverPosition.y - startDragPosition.y;

        if (dx == 0 && dy == 0) {
            return;
        }

        Direction oldRotation = currentRotation;

        if (Math.abs(dx) >= Math.abs(dy)) {
            currentRotation = dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            currentRotation = dy > 0 ? Direction.UP : Direction.DOWN;
        }

        if (oldRotation != currentRotation) {
            updateGhostBuilding();
        }
    }

    public PreviewState getPreviewState() {
        if (!isActive()) {
            return new PreviewState(false, false, false, null, null, null, null, null, false, null);
        }
        return new PreviewState(
            true, eraseMode, isDragging, startDragPosition, hoverPosition,
            getTargetPositions(), selectedType, currentRotation,
            isValidPlace(), ghostBuilding
        );
    }
}
