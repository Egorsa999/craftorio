package io.github.craftorio.view.sprite;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimatedSprite implements GameSprite {
    private final Animation<TextureRegion> animation;

    public AnimatedSprite(Animation<TextureRegion> animation) {
        this.animation = animation;
    }

    @Override
    public TextureRegion getFirstFrame() {
        return animation.getKeyFrame(0);
    }

    @Override
    public TextureRegion getKeyFrame(float stateTime) {
        return animation.getKeyFrame(stateTime, true);
    }
}
