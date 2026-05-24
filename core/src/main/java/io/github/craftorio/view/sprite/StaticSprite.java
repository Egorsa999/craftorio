package io.github.craftorio.view.sprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class StaticSprite implements GameSprite {
    private final TextureRegion region;

    public StaticSprite(TextureRegion region) {
        this.region = region;
    }

    @Override
    public TextureRegion getFirstFrame() { return region; }

    @Override
    public TextureRegion getKeyFrame(float stateTime) { return region; }
}
