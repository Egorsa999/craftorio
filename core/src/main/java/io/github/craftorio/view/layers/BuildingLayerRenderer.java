package io.github.craftorio.view.layers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.DamageableBuilding;
import io.github.craftorio.model.building.defense.Turret;
import io.github.craftorio.model.building.defense.LaserTurret;
import io.github.craftorio.model.building.logistics.Belt;
import io.github.craftorio.model.building.logistics.Pipe;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.TextureRenderer;
import io.github.craftorio.view.VisibleBounds;

import java.util.HashSet;
import java.util.Set;

public class BuildingLayerRenderer implements LayerRenderer {

    private final BuildingRegistry registry;
    private final TextureLoad textures;
    private final Set<Building> renderedBuildingsThisFrame = new HashSet<>();

    public BuildingLayerRenderer(BuildingRegistry registry, TextureLoad textures) {
        this.registry = registry;
        this.textures = textures;
    }

    @Override
    public void render(SpriteBatch batch, VisibleBounds bounds, float stateTime) {
        renderedBuildingsThisFrame.clear();
        for (int x = bounds.startX(); x < bounds.endX(); x++) {
            for (int y = bounds.startY(); y < bounds.endY(); y++) {

                Building current = registry.getBuildingAt(x, y);
                if (current == null || !renderedBuildingsThisFrame.add(current)) continue;

                if (current instanceof Belt || current instanceof Pipe) {
                    continue;
                }
                Color colorFilter = new Color(1f, 1f, 1f, 1f);

                if (current instanceof DamageableBuilding damageableCurrent){
                    if (damageableCurrent.isReceivingDamage())
                        colorFilter = new Color(1.0f, 0.3f, 0.3f, 1.0f);
                    else{
                        float hpPercent = (float) damageableCurrent.getHP() / damageableCurrent.type.getMaxHP();
                        float brightness = 0.3f + (0.7f * hpPercent);
                        colorFilter = new Color(brightness, brightness, brightness, 1.0f);
                    }
                }

                if (current instanceof Turret turret) {
                    TextureRenderer.draw(
                        batch, textures.get(current.type),
                        (float)current.anchor.x, (float)current.anchor.y,
                        current.type.getWidth(), current.type.getHeight(),
                        turret.getRotationDeg(), colorFilter, stateTime
                    );
                    continue;
                }

                if (current instanceof LaserTurret laserTurret) {
                    TextureRenderer.draw(
                        batch, textures.get(current.type),
                        (float)current.anchor.x, (float)current.anchor.y,
                        current.type.getWidth(), current.type.getHeight(),
                        laserTurret.getRotationDeg(), colorFilter, stateTime
                    );
                    continue;
                }

                float width = current.type.getWidth();
                float height = current.type.getHeight();
                TextureRenderer.drawBuilding(
                    batch, textures.get(current.type),
                    (float)current.anchor.x, (float)current.anchor.y,
                    width, height,
                    current.direction,
                    colorFilter, stateTime
                );
            }
        }
    }
}
