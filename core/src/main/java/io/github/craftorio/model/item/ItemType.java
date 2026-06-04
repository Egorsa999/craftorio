package io.github.craftorio.model.item;

public enum ItemType {
    IRON_ORE("Iron ore"),
    COPPER_ORE("Copper ore"),
    COAL("Coal"),
    BULLET("Bullet"),
    STONE("Stone"),
    IRON_INGOT("Iron ingot"),
    COPPER_INGOT("Copper ingot"),
    STEEL("Steel"),
    PLASTIC("Plastic"),
    STEEL_BULLET("Steel bullet"),
    GLASS("Glass"),
    CHIP("Chip");

    private final String name;
    ItemType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
