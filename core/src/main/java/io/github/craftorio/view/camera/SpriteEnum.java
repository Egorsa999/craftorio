package io.github.craftorio.model.sprite;

public enum SpriteEnum {
    IRON_ORE("iron", false),
    COPPER_ORE("copper", false),
    COAL_ORE("coal", false),
    STONE("stone", false),
    GROUND("ground", false),


    PLAYER("player", false),            // Animated

    WHITE_PIXEL("blank", false),

    private final String atlasRegionName;
    private final boolean isAnimated;

    SpriteEnum(String atlasRegionName, boolean isAnimated) {
        this.atlasRegionName = atlasRegionName;
        this.isAnimated = isAnimated;
    }

    public String getAtlasRegionName() {
        return atlasRegionName;
    }

    public boolean isAnimated() {
        return isAnimated;
    }
}
