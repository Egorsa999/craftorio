package io.github.craftorio.view;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

//accepts TextureRegion and Animation at the same time
public class GameSprite {
    private final TextureRegion staticRegion;
    private final Animation<TextureRegion> animation;
    private final boolean isAnimated;
    public GameSprite(TextureRegion region) {
        this.staticRegion = region;
        this.animation = null;
        this.isAnimated = false;
    }

    public GameSprite(Animation<TextureRegion> animation) {
        this.staticRegion = null;
        this.animation = animation;
        this.isAnimated = true;
    }

    public TextureRegion getFirstFrame(){
        return getKeyFrame(0);
    }
    public TextureRegion getKeyFrame(float stateTime) {
        if (isAnimated) {
            return animation.getKeyFrame(stateTime, true);
        }
        return staticRegion;
    }
}
