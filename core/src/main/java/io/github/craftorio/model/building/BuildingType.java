package io.github.craftorio.model.building;

public enum BuildingType {
    BELT(1, 1),
    MINER(2, 2);


    private final int width;
    private final int height;

    BuildingType(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
