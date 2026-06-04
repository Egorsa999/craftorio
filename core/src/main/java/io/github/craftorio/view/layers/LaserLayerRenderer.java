package io.github.craftorio.view.layers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.defense.LaserTurret;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.view.VisibleBounds;

public class LaserLayerRenderer implements ShapeLayerRenderer {

    private final BuildingRegistry registry;

    public LaserLayerRenderer(BuildingRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void render(ShapeRenderer renderer, VisibleBounds bounds, float stateTime) {
        for (int x = bounds.startX(); x < bounds.endX(); x++) {
            for (int y = bounds.startY(); y < bounds.endY(); y++) {
                Building current = registry.getBuildingAt(x, y);

                if (current instanceof LaserTurret laserTurret) {
                    if (laserTurret.isFiring() && laserTurret.getCurrentTarget() != null && !laserTurret.getCurrentTarget().isDead()) {
                        float satisfaction = laserTurret.getSatisfactionRatio();

                        if (satisfaction <= 0.01f) continue;

                        float startX = laserTurret.getX() + 0.5f;
                        float startY = laserTurret.getY() + 0.5f;
                        float targetX = laserTurret.getCurrentTarget().getX();
                        float targetY = laserTurret.getCurrentTarget().getY();

                        Color laserColor;
                        if (satisfaction < 0.33f) {
                            laserColor = new Color(1f, 0f, 0f, 0.8f);       // Красный
                        } else if (satisfaction < 0.66f) {
                            laserColor = new Color(1f, 1f, 0f, 0.8f);       // Желтый
                        } else {
                            laserColor = new Color(0f, 1f, 1f, 0.8f);       // Голубой (Cyan)
                        }

                        float currentWidth = 0.2f * satisfaction;

                        renderer.setColor(laserColor);
                        renderer.rectLine(startX, startY, targetX, targetY, currentWidth);
                    }
                }
            }
        }
    }
}
