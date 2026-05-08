package io.github.craftorio.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.craftorio.model.building.Belt;

public class BeltRenderer {
    public static void draw(ShapeRenderer shapeRenderer, Belt belt, float globalOffset, float transperecy) {
        float x = belt.getX();
        float y = belt.getY();

        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, transperecy);
        shapeRenderer.rect(x, y, 1, 1);

        shapeRenderer.setColor(Color.LIME);
        float thick = 0.20f;

        switch (belt.direction) {
            case RIGHT:
                shapeRenderer.rect(x + 1f - thick, y, thick, 1);
                break;
            case LEFT:
                shapeRenderer.rect(x, y, thick, 1);
                break;
            case UP:
                shapeRenderer.rect(x, y + 1f - thick, 1, thick);
                break;
            case DOWN:
                shapeRenderer.rect(x, y, 1, thick);
                break;
        }
    }
}
