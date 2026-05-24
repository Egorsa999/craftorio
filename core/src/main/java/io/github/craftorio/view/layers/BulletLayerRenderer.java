package io.github.craftorio.view.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.craftorio.model.core.GameContext;
import io.github.craftorio.model.entity.Bullet;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.TextureRenderer;
import io.github.craftorio.view.VisibleBounds;

import java.util.List;

public class BulletLayerRenderer implements  LayerRenderer{

    private final List<Bullet> bullets;
    private final TextureLoad textures;

    public BulletLayerRenderer(List<Bullet> bullets, TextureLoad textures) {
        this.bullets = bullets;
        this.textures = textures;
    }

    @Override
    public void render(SpriteBatch batch, VisibleBounds bounds, float stateTime) {
        for (Bullet b : bullets) {
            float bulletSize = 0.5f;
            TextureRenderer.draw(
                batch, textures.get("bullet"),
                b.getX() - (bulletSize / 2f), b.getY() - (bulletSize / 2f),
                bulletSize, bulletSize,
                b.getRotationDeg(),
                null, stateTime
            );
        }
    }
}
