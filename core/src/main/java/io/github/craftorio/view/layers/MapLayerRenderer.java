package io.github.craftorio.view.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.craftorio.model.core.WorldMap;
import io.github.craftorio.model.generator.ResourceType;
import io.github.craftorio.model.generator.TerrainType;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.TextureRenderer;
import io.github.craftorio.view.VisibleBounds;

public class MapLayerRenderer implements LayerRenderer{

    private final WorldMap worldMap;
    private final TextureLoad textures;

    public MapLayerRenderer(WorldMap worldMap, TextureLoad textureLoad) {
        this.worldMap = worldMap;
        this.textures = textureLoad;
    }

    @Override
    public void render(SpriteBatch batch, VisibleBounds bounds, float stateTime) {
        for (int x = bounds.startX(); x < bounds.endX(); x++) {
            for (int y = bounds.startY(); y < bounds.endY(); y++) {
                TerrainType terrainType = worldMap.getCell(x, y).getTerrainType();
                TextureRenderer.draw(batch, textures.get(terrainType), x, y, 1, 1, 0, null, stateTime);
                ResourceType type = worldMap.getCell(x, y).getResourceType();
                if (type != ResourceType.NONE) {
                    TextureRenderer.draw(batch, textures.get(type), x, y, 1, 1, 0, null, stateTime);
                }
            }
        }
    }
}
