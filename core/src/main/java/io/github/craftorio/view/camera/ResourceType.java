package io.github.craftorio.view.camera;

public enum SpriteEnum {
    NONE("ground"),
    IRON("iron"),
    COPPER("copper")

    private final String atlasKey;
    ResourceType(String atlasKey) {  // Constructor called when creating each enum value
        this.atlasKey = atlasKey;
    }
}
