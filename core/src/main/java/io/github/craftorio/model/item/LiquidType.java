package io.github.craftorio.model.item;

import java.awt.Color;

public enum LiquidType {
    WATER(new Color(0, 0, 255)),
    OIL(new Color(0, 0, 0));

    private final Color color;

    LiquidType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
