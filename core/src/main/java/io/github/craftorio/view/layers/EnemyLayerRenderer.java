package io.github.craftorio.view.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.enemy.Enemy;
import io.github.craftorio.model.enemy.WaveSpawner;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.TextureRenderer;
import io.github.craftorio.view.VisibleBounds;

public class EnemyLayerRenderer implements LayerRenderer{

    private final WaveSpawner waveSpawner;
    private final TextureLoad textures;

    public EnemyLayerRenderer(WaveSpawner waveSpawner, TextureLoad textures) {
        this.waveSpawner = waveSpawner;
        this.textures = textures;
    }


    @Override
    public void render(SpriteBatch batch, VisibleBounds bounds, float stateTime) {
        for (Enemy enemy : waveSpawner.getActiveEnemies()){
            float drawWidth = enemy.getType().getTextureSize();
            float drawHeight = drawWidth;

            float x = enemy.getX() - drawWidth/2f;
            float y = enemy.getY() - drawWidth/2f;



            if (enemy.getDirection() == Direction.LEFT) {
                x += drawWidth;
                drawWidth *= -1f;
            }

            TextureRenderer.draw(batch, textures.get("slime"), x, y, drawWidth, drawHeight,
                0, null, stateTime);
        }
    }
}
