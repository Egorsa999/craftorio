package io.github.craftorio.view;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import io.github.craftorio.model.ItemType;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.generator.ResourceType;
import io.github.craftorio.model.generator.TerrainType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class TextureLoad {
    private final Map<ResourceType, GameSprite> resourceSprites = new EnumMap<>(ResourceType.class);
    private final Map<BuildingType, GameSprite> buildingSprites = new EnumMap<>(BuildingType.class);
    private final HashMap<String, GameSprite> StringSprites = new HashMap<>();
    private final HashMap<Integer, GameSprite> conveyorTextures = new HashMap<>();
    private final HashMap<ItemType, GameSprite> itemSprites = new HashMap<>();
    private final HashMap<TerrainType, GameSprite> terrarianSprites = new HashMap<>();
    private final TextureAtlas atlas;

    public TextureLoad(TextureAtlas atlas) {
        this.atlas = atlas;

        loadSprites();
    }

    private void loadSprites() {
        resourceSprites.put(ResourceType.IRON, load("ore-iron"));
        resourceSprites.put(ResourceType.COPPER, load("ore-copper"));
        resourceSprites.put(ResourceType.COAL, load("ore-coal"));
        resourceSprites.put(ResourceType.NONE, load("blank"));

        terrarianSprites.put(TerrainType.GRASS, load("grass"));
        terrarianSprites.put(TerrainType.SAND, load("sand"));
        terrarianSprites.put(TerrainType.WALL, load("wall"));
        terrarianSprites.put(TerrainType.WATER, load("water"));

        buildingSprites.put(BuildingType.MINER, load("miner"));
        buildingSprites.put(BuildingType.HORIZONTAL_MINER, load("horizontal_miner"));

        StringSprites.put("player", load("player"));
        StringSprites.put("blank", load("blank"));

        StringSprites.put("player_idle_side", loadAnimated("player_idle_side", 0.15f));
        StringSprites.put("player_idle_up", loadAnimated("player_idle_up", 0.15f));
        StringSprites.put("player_idle_down", loadAnimated("player_idle_down", 0.15f));
        StringSprites.put("player_run_side", loadAnimated("player_run_side", 0.1f));
        StringSprites.put("player_run_up", loadAnimated("player_run_up", 0.1f));
        StringSprites.put("player_run_down", loadAnimated("player_run_down", 0.1f));

        conveyorTextures.put(0, loadAnimated("conveyor-0", 0.1f));
        conveyorTextures.put(1, loadAnimated("conveyor-1", 0.1f));
        conveyorTextures.put(2, loadAnimated("conveyor-2", 0.1f));
        conveyorTextures.put(3, loadAnimated("conveyor-3", 0.1f));
        conveyorTextures.put(4, loadAnimated("conveyor-4", 0.1f));

        itemSprites.put(ItemType.IRON_ORE, load("item-iron"));
        itemSprites.put(ItemType.COAL, load("item-coal"));
        itemSprites.put(ItemType.COPPER_ORE, load("item-copper"));
    }

    private GameSprite load(String regionName) {
        return new GameSprite(atlas.findRegion(regionName));
    }

    private GameSprite loadAnimated(String baseName, float frameDuration) {
        Array<TextureAtlas.AtlasRegion> frames = atlas.findRegions(baseName);
        if (frames.size == 0) return load(baseName);
        return new GameSprite(new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP));
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

    public GameSprite get(TerrainType type) {
        return terrarianSprites.get(type);
    }

    public GameSprite get(ItemType type) {
        return itemSprites.get(type);
    }

    public GameSprite get(BuildingType type) {
        return buildingSprites.get(type);
    }

    public GameSprite get(String type) {
        return StringSprites.get(type);
    }
}
