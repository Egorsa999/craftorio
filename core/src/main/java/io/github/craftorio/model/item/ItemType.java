package io.github.craftorio.model.item;

public enum ItemType {
    IRON_ORE("Iron ore"),
    COPPER_ORE("Copper ore"),
    COAL("Coal");

    private final String name;
    ItemType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
