package io.github.craftorio.view.layers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import io.github.craftorio.model.building.BuildingFactory;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.building.logistics.Belt;
import io.github.craftorio.model.ui.BuildTool;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.TextureRenderer;
import io.github.craftorio.view.VisibleBounds;
import io.github.craftorio.view.renderer.BeltRenderer;

import java.awt.*;

public class BuildingPreviewLayerRenderer implements LayerRenderer{

    private final BuildTool buildTool;
    private final TextureLoad textures;
    private final BuildingFactory factory;

    public BuildingPreviewLayerRenderer(BuildTool buildTool, TextureLoad textures, BuildingFactory factory) {
        this.buildTool = buildTool;
        this.textures = textures;
        this.factory = factory;
    }


    @Override
    public void render(SpriteBatch batch, VisibleBounds bounds, float stateTime) {
        if (!buildTool.isActive()) return;
        if (buildTool.eraseMode) {
            float xl = Math.min(buildTool.getStartHoverPosition().x, buildTool.getHoverPosition().x);
            float xr = Math.max(buildTool.getStartHoverPosition().x, buildTool.getHoverPosition().x);
            float yl = Math.min(buildTool.getStartHoverPosition().y, buildTool.getHoverPosition().y);
            float yr = Math.max(buildTool.getStartHoverPosition().y, buildTool.getHoverPosition().y);
            if (xl == -1) return;
//            System.out.println("ERASE: " + buildTool.getStartHoverPosition() + " " + buildTool.getHoverPosition());
            Color colorFilter = new Color(1f, 0f, 0f, 0.5f);
            batch.setColor(colorFilter);
            batch.draw(
                textures.getBlank().getFirstFrame(),
                xl, yl,
                xr - xl + 1, yr - yl + 1
            );
            batch.setColor(Color.WHITE);
            return;
        }

        Array<Point> positions = buildTool.getHoverPositions();
        BuildingType type = buildTool.getSelectedType();
        Direction rotation = buildTool.getCurrentRotation();

        float width = type.getWidth();
        float height = type.getHeight();


        Color colorFilter = new Color(1f, 1f, 1f, 0.5f);
        if (!buildTool.isValidPlace())colorFilter = new Color(1f, 0f, 0f, 0.5f);
        for (Point pos : positions){
            if (type == BuildingType.BELT) {
                BeltRenderer.drawBackground(colorFilter, batch, textures.getConveyorTextures(), (Belt) factory.createBuilding(BuildingType.BELT, new Point(pos.x, pos.y), rotation), stateTime, 0.5f);
                continue;
            }
            TextureRenderer.drawBuilding(
                batch, textures.get(type),
                (float)pos.getX(), (float)pos.getY(),
                type.getWidth(), type.getHeight(),
                rotation, colorFilter,
                0f
            );
        }
    }
}
