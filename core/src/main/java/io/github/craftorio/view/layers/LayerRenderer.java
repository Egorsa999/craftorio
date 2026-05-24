package io.github.craftorio.view.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.craftorio.view.VisibleBounds;

public interface LayerRenderer {
    void render(SpriteBatch batch, VisibleBounds bounds, float stateTime);
}
