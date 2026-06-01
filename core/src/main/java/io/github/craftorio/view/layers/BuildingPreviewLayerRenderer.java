package io.github.craftorio.view.layers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.building.logistics.Belt;
import io.github.craftorio.model.building.logistics.Pipe;
import io.github.craftorio.ui.PreviewState;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.TextureRenderer;
import io.github.craftorio.view.VisibleBounds;
import io.github.craftorio.view.renderer.BeltRenderer;
import io.github.craftorio.view.renderer.PipeRenderer;

import java.awt.Point;
import java.util.function.Supplier;

public class BuildingPreviewLayerRenderer implements LayerRenderer {

    private final TextureLoad textures;
    private final Supplier<PreviewState> stateSupplier;

    public BuildingPreviewLayerRenderer(Supplier<PreviewState> stateSupplier, TextureLoad textures) {
        this.stateSupplier = stateSupplier;
        this.textures = textures;
    }

    @Override
    public void render(SpriteBatch batch, VisibleBounds bounds, float stateTime) {
        PreviewState state = stateSupplier.get();
        if (!state.isActive()) return;

        if (state.isEraseMode()) {
            Point start = state.isDragging() ? state.dragStart() : state.hoverPosition();
            Point hover = state.hoverPosition();

            float xl = Math.min(start.x, hover.x);
            float xr = Math.max(start.x, hover.x);
            float yl = Math.min(start.y, hover.y);
            float yr = Math.max(start.y, hover.y);

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

        Array<Point> positions = state.positions();
        BuildingType type = state.selectedType();
        Direction rotation = state.currentRotation();

        Color colorFilter = state.isValidPlace() ? new Color(1f, 1f, 1f, 0.5f) : new Color(1f, 0f, 0f, 0.5f);

        for (int i = 0; i < positions.size; i++) {
            Point pos = positions.get(i);

            if (type == BuildingType.BELT && state.ghostBuilding() instanceof Belt belt) {
                belt.setAnchor(pos.x, pos.y);
                BeltRenderer.drawBackground(colorFilter, batch, textures.getConveyorTextures(), belt, stateTime, 0.5f);
                continue;
            }

            if (type == BuildingType.PIPE && state.ghostBuilding() instanceof Pipe pipe) {
                pipe.setAnchor(pos.x, pos.y);
                Color previewTint = colorFilter.cpy().mul(0.5f, 0.5f, 0.5f, 0.5f);
                PipeRenderer.draw(
                    previewTint,
                    batch,
                    textures.getConduitBottomTextures(),
                    textures.getConduitTopTextures(),
                    pipe,
                    stateTime
                );
                continue;
            }

            TextureRenderer.drawBuilding(
                batch, textures.get(type),
                (float)pos.x, (float)pos.y,
                type.getWidth(), type.getHeight(),
                rotation, colorFilter, 0f
            );
        }
    }
}
