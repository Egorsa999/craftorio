package io.github.craftorio.model.item;

import java.awt.Color;

public enum LiquidType {
    WATER(new Color(92, 92, 255)),
    OIL(new Color(64, 64, 64));

    private final Color color;

    LiquidType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
