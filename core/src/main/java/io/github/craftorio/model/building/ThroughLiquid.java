package io.github.craftorio.model.building;

import io.github.craftorio.model.item.LiquidType;

import java.awt.*;

public interface ThroughLiquid {
    float throughLiquid(LiquidType type, float amount);
    boolean canThroughLiquidIn(Point point);
}
