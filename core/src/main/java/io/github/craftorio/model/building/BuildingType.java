package io.github.craftorio.model.building;

import io.github.craftorio.model.item.ItemType;

import java.util.Map;

public enum BuildingType {
    BELT("Belt", 1, 1, true, 50, Map.of(ItemType.COPPER_ORE, 1)),
    MINER("Miner", 2, 2, false, 100, Map.of(ItemType.COPPER_ORE, 10, ItemType.IRON_ORE, 5)),
    HORIZONTAL_MINER("Horizontal Miner", 1, 3, 1, 2, false, 100, Map.of(ItemType.COPPER_ORE, 15, ItemType.IRON_ORE, 10)),
    CORE("Core", 3, 3, false, 500, Map.of()),
    JUNCTION("Junction", 1, 1, false, 100, Map.of(ItemType.COPPER_ORE, 2)),
    ROUTER("Router", 1, 1, false, 100, Map.of(ItemType.COPPER_ORE, 3)),
    UNDERGROUND_BELT("Underground Belt", 1, 1, false, 100, Map.of(ItemType.COPPER_ORE, 10, ItemType.IRON_ORE, 5)),
    ASSEMBLER("Assembler", 2, 2, false, 100, Map.of(ItemType.COPPER_ORE, 20, ItemType.IRON_ORE, 10)),
    TURRET("Turret", 1, 1, false, 100, Map.of(ItemType.COPPER_ORE, 15)),
    LASER_TURRET("Laser Turret", 1, 1, false, 100, Map.of(ItemType.COPPER_ORE, 15, ItemType.IRON_ORE, 5)),
    WALL("Wall", 1, 1, false, 500, Map.of(ItemType.COPPER_ORE, 5)),
    PIPE("Pipe", 1, 1, true, 50, Map.of(ItemType.COPPER_ORE, 2)),
    LIQUID_JUNCTION("Liquid Junction", 1, 1, true, 50, Map.of(ItemType.COPPER_ORE, 4)),
    LIQUID_ROUTER("Liquid Router", 1, 1, true, 50, Map.of(ItemType.COPPER_ORE, 10)),
    UNDERGROUND_PIPE("Underground Pipe", 1, 1, false, 100, Map.of(ItemType.COPPER_ORE, 10, ItemType.IRON_ORE, 5)),
    PUMP("Pump", 1, 1, false, 50, Map.of(ItemType.COPPER_ORE, 10)),
    COAL_POWER_GENERATOR("Coal Generator", 1, 1, false, 200, Map.of(ItemType.COPPER_ORE, 15, ItemType.IRON_ORE, 15)),
    OIL_GENERATOR("Oil Generator", 2, 2, false, 200, Map.of(ItemType.COPPER_ORE, 15, ItemType.IRON_ORE, 15)),
    POWER_POLE("Power Node", 1, 1, false, 100, Map.of(ItemType.COPPER_ORE, 2)),
    FURNACE("Furnance", 2, 2, false, 100, Map.of(ItemType.COPPER_ORE, 2)),
    CHEMICAL_PLANT("Chemical Plant", 2, 2, false, 100, Map.of(ItemType.COPPER_ORE, 2)),
//    ROCKET("Rocket", 4, 4, false, 5000, Map.of(ItemType.STONE, 500, ItemType.STEEL, 300, ItemType.PLASTIC, 200));
    ROCKET("Rocket", 4, 4, false, 5000, Map.of(ItemType.COPPER_ORE, 1));
    ACCUMULATOR("Accumulator", 1, 1, false, 100, Map.of(ItemType.COPPER_ORE, 2));



    private final String displayName;
    private final int width;
    private final int height;
    private final int collisionWidth;
    private final int collisionHeight;
    private final int maxHP;
    private final boolean walkable;
    private final Map<ItemType, Integer> cost;

    BuildingType(String displayName, int width, int height, boolean walkable,  int maxHP, Map<ItemType, Integer> cost) {
        this.displayName = displayName;
        this.width = width;
        this.height = height;
        this.walkable = walkable;
        this.collisionHeight = height;
        this.collisionWidth = width;
        this.maxHP = maxHP;
        this.cost = cost;
    }

    BuildingType(String displayName, int width, int height, int collisionWidth, int collisionHeight, boolean walkable, int maxHP, Map<ItemType, Integer> cost){
        this.displayName = displayName;
        this.width = width;
        this.height = height;
        this.walkable = walkable;
        this.collisionHeight = collisionHeight;
        this.collisionWidth = collisionWidth;
        this.maxHP = maxHP;
        this.cost = cost;
    }

    public String getDisplayName() { return displayName; }
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

    public Map<ItemType, Integer> getCost() {
        return cost;
    }
}
