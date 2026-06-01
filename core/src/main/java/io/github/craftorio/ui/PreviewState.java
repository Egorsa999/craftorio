package io.github.craftorio.ui;

import com.badlogic.gdx.utils.Array;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.Direction;

import java.awt.Point;

public record PreviewState(
    boolean isActive,
    boolean isEraseMode,
    boolean isDragging,
    Point dragStart,
    Point hoverPosition,
    Array<Point> positions,
    BuildingType selectedType,
    Direction currentRotation,
    boolean isValidPlace,
    Building ghostBuilding
) {}
