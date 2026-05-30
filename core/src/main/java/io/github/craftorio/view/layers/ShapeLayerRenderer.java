package io.github.craftorio.view.layers;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.craftorio.view.VisibleBounds;

public interface ShapeLayerRenderer {
    void render(ShapeRenderer shapeRenderer, VisibleBounds bounds, float stateTime);
}
