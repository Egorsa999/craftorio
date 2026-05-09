package io.github.craftorio.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.generator.ResourceType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class TextureLoad {
    private final Map<ResourceType, GameSprite> resourceSprites = new EnumMap<>(ResourceType.class);
    private final Map<BuildingType, GameSprite> buildingSprites = new EnumMap<>(BuildingType.class);
    private final HashMap<String, GameSprite> StringSprites = new HashMap<>();
    private final HashMap<Integer, GameSprite> conveyorTextures = new HashMap<>();
    private final TextureAtlas atlas;

    public TextureLoad(TextureAtlas atlas) {
        this.atlas = atlas;

        loadSprites();
    }

    private void loadSprites() {
        resourceSprites.put(ResourceType.IRON, load("iron"));
        resourceSprites.put(ResourceType.COPPER, load("copper"));
        resourceSprites.put(ResourceType.NONE, load("ground"));
        buildingSprites.put(BuildingType.MINER, load("blank"));
        StringSprites.put("player", load("player"));
        StringSprites.put("blank", load("blank"));
        conveyorTextures.put(0, load("conveyor0"));
        conveyorTextures.put(1, load("conveyor1"));
        conveyorTextures.put(2, load("conveyor2"));
        conveyorTextures.put(3, load("conveyor3"));
        conveyorTextures.put(4, load("conveyor4"));
    }

    private GameSprite load(String regionName) {
        return new GameSprite(atlas.findRegion(regionName));
    }

    private GameSprite loadAnimated(String baseName) {
        Array<TextureRegion> frames = loadFrames(baseName);
        if (frames.size == 0) return load(baseName);
        return new GameSprite(new Animation<>(0.1f, frames, Animation.PlayMode.LOOP));
    }

    private Array<TextureRegion> loadFrames(String baseName) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; atlas.findRegion(baseName + "_" + i) != null; i++) {
            frames.add(atlas.findRegion(baseName + "_" + i));
        }
        return frames;
    }

    public HashMap<Integer, GameSprite> getConveyorTextures() {return conveyorTextures;}

    public GameSprite get(ResourceType type) {
        return resourceSprites.get(type);
    }

    public GameSprite get(BuildingType type) {
        return buildingSprites.get(type);
    }

    public GameSprite get(String type) {
        return StringSprites.get(type);
    }
}
