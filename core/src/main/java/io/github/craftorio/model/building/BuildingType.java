package io.github.craftorio.model.building;

public enum BuildingType {
    BELT(1, 1, true, 50),
    MINER(2, 2, false, 100),
    HORIZONTAL_MINER(1, 3, 1, 2, false, 100),
    CORE(3, 3, false, 500),
    JUNCTION(1, 1, false, 100),
    ROUTER(1, 1, false, 100),
    ASSEMBLER(2, 2, false, 100),
    TURRET(1, 1, false, 100),
    WALL(1, 1, false, 500),
    PIPE(1, 1, true, 50),
    PUMP(1, 1, false, 50);


    private final int width;
    private final int height;
    private final int collisionWidth;
    private final int collisionHeight;
    private final int maxHP;
    private final boolean walkable;

    BuildingType(int width, int height, boolean walkable) {
        this.width = width;
        this.height = height;
        this.walkable = walkable;
        this.collisionHeight = height;
        this.collisionWidth = width;
        this.maxHP = -1;
    }

    BuildingType(int width, int height, boolean walkable, int maxHP) {
        this.width = width;
        this.height = height;
        this.walkable = walkable;
        this.collisionHeight = height;
        this.collisionWidth = width;
        this.maxHP = maxHP;
    }

    BuildingType(int width, int height, int collisionWidth, int collisionHeight, boolean walkable){
        this.width = width;
        this.height = height;
        this.walkable = walkable;
        this.collisionHeight = collisionHeight;
        this.collisionWidth = collisionWidth;
        this.maxHP = -1;
    }

    BuildingType(int width, int height, int collisionWidth, int collisionHeight, boolean walkable, int maxHP){
        this.width = width;
        this.height = height;
        this.walkable = walkable;
        this.collisionHeight = collisionHeight;
        this.collisionWidth = collisionWidth;
        this.maxHP = maxHP;
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

    public int getMaxHP() {
        return maxHP;
    }
}
