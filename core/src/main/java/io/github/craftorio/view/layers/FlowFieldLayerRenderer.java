package io.github.craftorio.view.layers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.craftorio.model.enemy.PathFinder;
import io.github.craftorio.model.enemy.WaveSpawner;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.TextureRenderer;
import io.github.craftorio.view.VisibleBounds;

public class FlowFieldLayerRenderer implements LayerRenderer{

    private final PathFinder pathFinder;
    private final TextureLoad textures;

    public FlowFieldLayerRenderer(WaveSpawner waveSpawner, TextureLoad textures) {
        this.pathFinder = waveSpawner.getPathFinder();
        this.textures = textures;


    }

    Color colorFilter = new Color(1f, 1f, 1f, 0.2f);

    @Override
    public void render(SpriteBatch batch, VisibleBounds bounds, float stateTime) {
        for (int x = bounds.startX(); x < bounds.endX(); x++) {
            for (int y = bounds.startY(); y < bounds.endY(); y++) {

                float rotaion = 1000;

                int dx = pathFinder.getFlowDirection(x, y).x;
                int dy = pathFinder.getFlowDirection(x, y).y;

                if (dx == 1 && dy == 0)rotaion = 0;
                else if (dx == 1 && dy == 1)rotaion = 45;
                else if (dx == 1 && dy == -1)rotaion = -45;
                else if (dx == -1 && dy == 0)rotaion = 180;
                else if (dx == -1 && dy == 1)rotaion = 135;
                else if (dx == -1 && dy == -1)rotaion = 225;
                else if (dx == 0 && dy == 1)rotaion = 90;
                else if (dx == 0 && dy == -1)rotaion = 270;

                if (rotaion == 1000)continue;

                TextureRenderer.draw(
                    batch, textures.get("arrow"),
                    x, y,
                    1f, 1f,
                    rotaion,
                    colorFilter, 0f);
            }
        }
    }
}
