package io.github.craftorio.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.craftorio.model.building.Belt;

public class BeltRenderer {

    public static void draw(SpriteBatch batch, TextureRegion conveyorTexture, Belt belt, float globalOffset, float transparency) {
        float x = belt.getX();
        float y = belt.getY();

        batch.setColor(1f, 1f, 1f, transparency);

        float rotation = 0f;
        switch (belt.direction) {
            case RIGHT:
                rotation = 0f;
                break;
            case UP:
                rotation = 90f;
                break;
            case LEFT:
                rotation = 180f;
                break;
            case DOWN:
                rotation = 270f;
                break;
        }

        batch.draw(conveyorTexture, x, y, 0.5f, 0.5f, 1f, 1f, 1f, 1f, rotation);

        batch.setColor(Color.WHITE);
    }
}
