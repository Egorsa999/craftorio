package io.github.craftorio.ui;

import com.badlogic.gdx.utils.Array;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.BuildingFactory;
import io.github.craftorio.model.core.BuildingManager;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.model.building.logistics.UndergroundBelt;
import io.github.craftorio.model.building.logistics.UndergroundPipe;

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

    private Point lastUndergroundStart = null;
    private Point lastUndergroundEnd = null;

    private final BuildingManager buildingManager;
    private final BuildingFactory factory;
    private Building ghostBuilding = null;
    private final Inventory inventory;

    private boolean isPaused;

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

        if (dx == 0 && dy == 0) return;

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
        lastUndergroundStart = null;
        lastUndergroundEnd = null;
    }

    public void stopDrag() {
        isDragging = false;
        lastUndergroundStart = null;
        lastUndergroundEnd = null;
    }

    public boolean tryBuild() {
        if (!isValidPlace()) return false;
        HashMap<ItemType, Integer> map = new HashMap<>();
        Array<Point> targets = getTargetPositions();

        boolean isUnderground = selectedType == BuildingType.UNDERGROUND_BELT || selectedType == BuildingType.UNDERGROUND_PIPE;

        if (isUnderground && !eraseMode) {
            if (targets.size < 2) return false;
            Point p1 = targets.get(0);
            Point p2 = targets.get(1);

            Direction dir = Direction.UP;
            if (p2.x > p1.x) dir = Direction.RIGHT;
            else if (p2.x < p1.x) dir = Direction.LEFT;
            else if (p2.y > p1.y) dir = Direction.UP;
            else if (p2.y < p1.y) dir = Direction.DOWN;

            if (lastUndergroundEnd != null && !lastUndergroundEnd.equals(p2)) {
                if (buildingManager.tryRemoveBuilding(lastUndergroundEnd)) {
                    inventory.add(selectedType.getCost());
                }
            }

            if (lastUndergroundStart == null || !lastUndergroundStart.equals(p1)) {
                if (buildingManager.tryPlaceBuilding(selectedType, p1, dir)) {
                    lastUndergroundStart = p1;
                    inventory.take(selectedType.getCost());
                }
            }

            if (!p1.equals(p2) && (lastUndergroundEnd == null || !lastUndergroundEnd.equals(p2))) {
                if (buildingManager.tryPlaceBuilding(selectedType, p2, dir)) {
                    lastUndergroundEnd = p2;
                    inventory.take(selectedType.getCost());
                }
            }

            if (lastUndergroundStart != null && lastUndergroundEnd != null) {
                Building b1 = buildingManager.getRegistry().getBuildingAt(lastUndergroundStart.x, lastUndergroundStart.y);
                Building b2 = buildingManager.getRegistry().getBuildingAt(lastUndergroundEnd.x, lastUndergroundEnd.y);

                int distance = Math.abs(lastUndergroundStart.x - lastUndergroundEnd.x) + Math.abs(lastUndergroundStart.y - lastUndergroundEnd.y);

                if (b1 instanceof UndergroundBelt u1 && b2 instanceof UndergroundBelt u2) {
                    u1.link(u2, distance, dir);
                } else if (b1 instanceof UndergroundPipe p1Obj && b2 instanceof UndergroundPipe p2Obj) {
                    p1Obj.link(p2Obj, distance, dir);
                }
            }
            return true;
        }

        for (Point point : targets) {
            for (Map.Entry<ItemType, Integer> entry : selectedType.getCost().entrySet()) {
                map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        for (Point point : targets) {
            if (!buildingManager.tryPlaceBuilding(selectedType, point, currentRotation)) return false;
        }
        inventory.take(map);
        return true;
    }

    public void tryErase() {
        for (Point point : getTargetPositions()) {
            Building building = buildingManager.getRegistry().getBuildingAt(point.x, point.y);

            if (building == null) continue;

            if (building instanceof UndergroundBelt uBelt) {
                UndergroundBelt partner = uBelt.getLinkedBelt();
                if (partner != null) {
                    if (buildingManager.tryRemoveBuilding(new Point(partner.getX(), partner.getY()))) {
                        inventory.add(partner.type.getCost());
                    }
                }
            } else if (building instanceof UndergroundPipe uPipe) {
                UndergroundPipe partner = uPipe.getLinkedPipe();
                if (partner != null) {
                    if (buildingManager.tryRemoveBuilding(new Point(partner.getX(), partner.getY()))) {
                        inventory.add(partner.type.getCost());
                    }
                }
            }

            if (buildingManager.tryRemoveBuilding(point)) {
                inventory.add(building.type.getCost());
            }
        }
    }

    public boolean isValidPlace() {
        if (!isActive() || eraseMode) return false;
        HashMap<ItemType, Integer> map = new HashMap<>();
        Array<Point> targets = getTargetPositions();

        Direction dir = currentRotation;
        boolean isUnderground = selectedType == BuildingType.UNDERGROUND_BELT || selectedType == BuildingType.UNDERGROUND_PIPE;

        if (isUnderground) {
            if (targets.size < 2) return false;

            Point p1 = targets.get(0);
            Point p2 = targets.get(1);
            if (p2.x > p1.x) dir = Direction.RIGHT;
            else if (p2.x < p1.x) dir = Direction.LEFT;
            else if (p2.y > p1.y) dir = Direction.UP;
            else if (p2.y < p1.y) dir = Direction.DOWN;
        }

        for (Point point : targets) {
            if (isUnderground) {
                if (point.equals(lastUndergroundStart) || point.equals(lastUndergroundEnd)) {
                    continue;
                }
            }

            for (Map.Entry<ItemType, Integer> entry : selectedType.getCost().entrySet()) {
                map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }

            if (!buildingManager.isValidPlace(selectedType, point, dir)) return false;
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
            return positions;
        }

        boolean isUnderground = selectedType == BuildingType.UNDERGROUND_BELT || selectedType == BuildingType.UNDERGROUND_PIPE;

        if (isUnderground) {
            positions.add(new Point(startDragPosition));
            if (!startDragPosition.equals(hoverPosition)) {
                int dx = Math.abs(hoverPosition.x - startDragPosition.x);
                int dy = Math.abs(hoverPosition.y - startDragPosition.y);
                if (dx > dy) {
                    positions.add(new Point(hoverPosition.x, startDragPosition.y));
                } else {
                    positions.add(new Point(startDragPosition.x, hoverPosition.y));
                }
            }
            return positions;
        }

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
        return positions;
    }

    private void autoRotateBelt() {
        int dx = hoverPosition.x - startDragPosition.x;
        int dy = hoverPosition.y - startDragPosition.y;

        if (dx == 0 && dy == 0) return;

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
            return new PreviewState(false, false, false, null, null, null, null, null, false, null, isPaused);
        }
        return new PreviewState(
            true, eraseMode, isDragging, startDragPosition, hoverPosition,
            getTargetPositions(), selectedType, currentRotation,
            isValidPlace(), ghostBuilding, isPaused
        );
    }

    public void setPause(boolean isPaused) {
        this.isPaused = isPaused;
    }
}
