package io.github.craftorio.model.building;

public enum BuildingType {
    BELT(1, 1),
    MINER(2, 2),
    HORIZONTAL_MINER(1, 3, 1, 2);


    private final int width;
    private final int height;
    private final int collisionWidth;
    private final int collisionHeight;

    BuildingType(int width, int height) {
        this.width = width;
        this.height = height;
        this.collisionHeight = height;
        this.collisionWidth = width;
    }

    BuildingType(int width, int height, int collisionWidth, int collisionHeight){
        this.width = width;
        this.height = height;
        this.collisionHeight = collisionHeight;
        this.collisionWidth = collisionWidth;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getCollisionHeight() {
        return collisionHeight;
    }

    public int getCollisionWidth() {
        return collisionWidth;
    }
}
