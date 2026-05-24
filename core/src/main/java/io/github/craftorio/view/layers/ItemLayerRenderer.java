package io.github.craftorio.view.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.logistics.Belt;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.VisibleBounds;
import io.github.craftorio.view.renderer.BeltRenderer;


public class ItemLayerRenderer implements LayerRenderer{

    private final BuildingRegistry registry;
    private final TextureLoad textures;

    public ItemLayerRenderer(BuildingRegistry registry, TextureLoad textures) {
        this.registry = registry;
        this.textures = textures;
    }

    @Override
    public void render(SpriteBatch batch, VisibleBounds bounds, float stateTime) {
        for (int x = bounds.startX(); x < bounds.endX(); x++) {
            for (int y = bounds.endY() - 1; y >= bounds.startY(); y--) {
                Building current = registry.getBuildingAt(x, y);
                if (current instanceof Belt belt) {
                    BeltRenderer.drawItems(batch, textures, belt, Belt.getAnimationOffset(), 1f);
                }
            }
        }
    }
}
