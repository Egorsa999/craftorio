package io.github.craftorio.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureRenderer {

    public static void draw(SpriteBatch batch, GameSprite sprite, float x, float y, float width, float height, float rotation,
                            Color colorFilter, float statetime) {
        TextureRegion texture = sprite.getKeyFrame(statetime);
        if(colorFilter != null) batch.setColor(colorFilter);
        float originX = width / 2f;
        float originY = height / 2f;

        batch.draw(
            texture,
            x, y,
            originX, originY,
            width, height,
            1f, 1f,
            rotation
        );
        batch.setColor(Color.WHITE);
    }
}
