package io.github.craftorio.model.ui;

import com.badlogic.gdx.utils.Array;
import io.github.craftorio.model.core.BuildingManager;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.Direction;

import java.awt.Point;

import static java.lang.Math.*;

public class BuildTool {
    private BuildingType selectedType = null;
    private Direction currentRotation = Direction.UP;
    private final Point startHoverPosition = new Point(-1, -1);
    private final Point hoverPosition = new Point(0, 0);

    private final BuildingManager buildingManager;

    public boolean eraseMode = false;

    public BuildTool(BuildingManager buildingManager){
        this.buildingManager = buildingManager;
    }

    public void selectBuilding(BuildingType type) {
        this.selectedType = type;
        this.currentRotation = Direction.UP;
    }

    public boolean tryBuild(){
        if (!isValidPlace())return false;

        for(Point point : getHoverPositions()){
            if(!buildingManager.tryPlaceBuilding(selectedType, point, currentRotation))
                return false;
        }
        return true;
    }

    public boolean isValidPlace(){
        if (!isActive())return false;
        for(Point point : getHoverPositions()){
            if(!buildingManager.isValidPlace(selectedType, point, currentRotation))
                return false;
        }
        return true;
    }

    public void clearSelection() {
        this.selectedType = null;
        updateStartHoverPosition(new Point(-1, -1));
    }

    public boolean isActive() {
        return selectedType != null || eraseMode;
    }

    public void updateStartHoverPosition(int gridX, int gridY) {startHoverPosition.setLocation(gridX, gridY);}
    public void updateHoverPosition(int gridX, int gridY) {hoverPosition.setLocation(gridX, gridY);}
    public void updateStartHoverPosition(Point p) {
        startHoverPosition.setLocation(p);
    }
    public void updateHoverPosition(Point p) {
        hoverPosition.setLocation(p);
    }

    public Point getStartHoverPosition() {
        return startHoverPosition;
    }

    public Point getHoverPosition() {
        return hoverPosition;
    }

    public void rotateRight() {
        if (isActive()) {
            currentRotation = currentRotation.next();
        }
    }

    public BuildingType getSelectedType() { return selectedType; }
    public Array<Point> getHoverPositions() {
        Array<Point> positions = new Array<>();
        if (startHoverPosition.x == -1 && startHoverPosition.y == -1){
            positions.add(hoverPosition);
            return positions;
        }
        int fin_x = hoverPosition.x, fin_y = hoverPosition.y;
        if (!eraseMode) {
            if(abs(startHoverPosition.x - fin_x) <= abs(startHoverPosition.y - fin_y)) fin_x = startHoverPosition.x;
            else fin_y = startHoverPosition.y;
        }

        int buildingWidth = selectedType.getWidth();
        int buildingHeight = selectedType.getHeight();

        if(currentRotation.to_degrees() % 180 != 0){
            int temp = buildingHeight;
            buildingHeight = buildingWidth;
            buildingWidth = temp;
        }

        int dx = buildingWidth * (startHoverPosition.x <= fin_x ? 1 : -1);
        int dy = buildingHeight * (startHoverPosition.y <= fin_y ? 1 : -1);

        if(selectedType == BuildingType.BELT){
            if (startHoverPosition.x < fin_x) currentRotation = Direction.RIGHT;
            if (startHoverPosition.x > fin_x) currentRotation = Direction.LEFT;
            if (startHoverPosition.y < fin_y) currentRotation = Direction.UP;
            if (startHoverPosition.y > fin_y) currentRotation = Direction.DOWN;
        }

        for(int i = startHoverPosition.x; (dx > 0 ? i <= fin_x : i >= fin_x); i += dx)
            for(int j = startHoverPosition.y; (dy > 0 ? j <= fin_y : j >= fin_y); j += dy) {
                positions.add(new Point(i, j));
            }
//        System.out.println(startHoverPosition + " " + hoverPosition + " " + positions.size);
//        System.out.println(abs(startHoverPosition.x - fin_x) + " " + abs(startHoverPosition.y - fin_y) + " " + positions.size);
        return positions;
    }
    public Direction getCurrentRotation() { return currentRotation; }

    public void tryErase() {
        for (Point point : getHoverPositions()) {
            buildingManager.tryRemoveBuilding(point);
        }
    }

    public void updSelectedType(BuildingType type) {
        selectedType = type;
    }
}
