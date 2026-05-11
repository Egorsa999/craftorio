package io.github.craftorio.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.craftorio.model.building.Direction;

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

    public static void drawBuilding(SpriteBatch batch, GameSprite sprite,
                                    float gridX, float gridY,
                                    float baseWidth, float baseHeight,
                                    Direction direction, Color colorFilter, float statetime) {

        TextureRegion texture = sprite.getKeyFrame(statetime);
        if (colorFilter != null) batch.setColor(colorFilter);

        boolean isHorizontal = (direction == Direction.RIGHT || direction == Direction.LEFT);
        float currentWidth = isHorizontal ? baseHeight : baseWidth;
        float currentHeight = isHorizontal ? baseWidth : baseHeight;

        float centerX = gridX + (currentWidth / 2f);
        float centerY = gridY + (currentHeight / 2f);

        float originX = baseWidth / 2f;
        float originY = baseHeight / 2f;

        batch.draw(texture,
            centerX - originX, centerY - originY,
            originX, originY,
            baseWidth, baseHeight,
            1f, 1f,
            direction.to_degrees());

        batch.setColor(Color.WHITE);
    }
}
