package io.github.craftorio.view.sprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface GameSprite {
    TextureRegion getFirstFrame();
    TextureRegion getKeyFrame(float stateTime);
}
