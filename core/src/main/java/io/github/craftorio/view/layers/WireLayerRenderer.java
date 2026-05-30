package io.github.craftorio.view.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.power.PowerConnectable;
import io.github.craftorio.model.building.power.PowerNode;
import io.github.craftorio.model.building.power.PowerPole;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.view.VisibleBounds;

public class WireLayerRenderer implements ShapeLayerRenderer{

    private final BuildingRegistry registry;

    public WireLayerRenderer(BuildingRegistry registry){

        this.registry = registry;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, VisibleBounds bounds, float stateTime) {
        shapeRenderer.setColor(1.0f, 1.0f, 0.0f, 0.8f);
        float wireThickness = 0.125f;


        for (Building building : registry.getBuildingsForTick()) {

            if (building instanceof PowerConnectable cBuilding) {
                PowerNode node = cBuilding.getPowerNode();

                float startX = building.getCenterX();
                float startY = building.getCenterY();

                for (PowerNode neighbor : node.getConnections()) {

                    if (node.hashCode() > neighbor.hashCode()) {

                        Building targetBuilding = neighbor.getOwner();
                        float endX = targetBuilding.getCenterX();
                        float endY = targetBuilding.getCenterY();

                        switch (neighbor.getNetwork().getStatus()) {
                            case IDLE:
                                shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.5f);
                                break;
                            case POWERED:
                                shapeRenderer.setColor(0.2f, 0.8f, 1.0f, 0.8f);
                                break;
                            case DEFICIT:
                                shapeRenderer.setColor(1.0f, 0.6f, 0.0f, 0.8f);
                                break;
                            case BLACKOUT:
                                shapeRenderer.setColor(1.0f, 0.1f, 0.1f, 0.8f);
                                break;
                        }

                        shapeRenderer.rectLine(
                            startX, startY,
                            endX, endY, wireThickness
                        );
                    }
                }
            }
        }
    }
}
