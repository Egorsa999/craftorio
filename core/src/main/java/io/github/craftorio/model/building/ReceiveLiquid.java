package io.github.craftorio.model.building;

import io.github.craftorio.model.item.LiquidType;

import java.awt.*;

public interface ReceiveLiquid {
    float receiveLiquid(Building from, LiquidType type, float amount);
    boolean canReceiveLiquidFrom(Building from, Point point);
}
