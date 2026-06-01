package io.github.craftorio.model.item;

import java.awt.Color;

public enum LiquidType {
    WATER("Water", new Color(92, 92, 255)),
    OIL("Oil", new Color(64, 64, 64)),
    ROCKET_FUEL("Rocket Fuel", new Color(255, 170, 68));

    private final String name;
    private final Color color;

    LiquidType(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
    public String getName() {
        return name;
    }
}
