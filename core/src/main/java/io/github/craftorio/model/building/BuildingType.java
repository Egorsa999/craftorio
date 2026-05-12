package io.github.craftorio.model.building;

public enum BuildingType {
    BELT(1, 1, true),
    MINER(2, 2, false),
    HORIZONTAL_MINER(1, 3, 1, 2, false),
    CORE(3, 3, false);


    private final int width;
    private final int height;
    private final int collisionWidth;
    private final int collisionHeight;
    private final boolean walkable;

    BuildingType(int width, int height, boolean walkable) {
        this.width = width;
        this.height = height;
        this.walkable = walkable;
        this.collisionHeight = height;
        this.collisionWidth = width;
    }

    BuildingType(int width, int height, int collisionWidth, int collisionHeight, boolean walkable){
        this.width = width;
        this.height = height;
        this.walkable = walkable;
        this.collisionHeight = collisionHeight;
        this.collisionWidth = collisionWidth;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean getWalkable() {return walkable;}
    public int getCollisionHeight() {
        return collisionHeight;
    }

    public int getCollisionWidth() {
        return collisionWidth;
    }
}
