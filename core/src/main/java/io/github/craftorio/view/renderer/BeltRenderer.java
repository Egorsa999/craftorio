package io.github.craftorio.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.craftorio.model.ItemType;
import io.github.craftorio.model.building.Belt;
import io.github.craftorio.model.building.Direction;

public class BeltRenderer {
    private static final float ITEM_SIZE = 0.3f;

    private static float[] getEntry(Direction fromDir) {
        switch(fromDir) {
            case RIGHT: return new float[]{0.0f, 0.5f};
            case LEFT:  return new float[]{1.0f, 0.5f};
            case UP:    return new float[]{0.5f, 0.0f};
            case DOWN:  return new float[]{0.5f, 1.0f};
        }
        return new float[]{0.5f, 0.5f};
    }

    private static float[] getExit(Direction beltDir) {
        switch(beltDir) {
            case RIGHT: return new float[]{1.0f, 0.5f};
            case LEFT:  return new float[]{0.0f, 0.5f};
            case UP:    return new float[]{0.5f, 1.0f};
            case DOWN:  return new float[]{0.5f, 0.0f};
        }
        return new float[]{0.5f, 0.5f};
    }

    public static void drawBackground(SpriteBatch batch, TextureRegion texture, Belt belt, float globalOffset, float transparency) {
        float x = belt.getX();
        float y = belt.getY();

        batch.setColor(1f, 1f, 1f, transparency);

        float rotation = 0f;
        switch (belt.direction) {
            case UP:    rotation = 0f; break;
            case LEFT:  rotation = 90f; break;
            case DOWN:  rotation = 180f; break;
            case RIGHT: rotation = 270f; break;
        }

        batch.draw(texture, x, y, 0.5f, 0.5f, 1f, 1f, 1f, 1f, rotation);

        batch.setColor(Color.WHITE);
    }

    public static void drawItems(SpriteBatch batch, TextureRegion texture, Belt belt, float globalOffset, float transparency) {
        float x = belt.getX();
        float y = belt.getY();

        for (int i = belt.getItemId().size() - 1; i >= 0; i--) {
            ItemType type = belt.getItemId().get(i);
            float p = belt.getItemProgress().get(i);
            Direction fromDir = belt.getItemFrom().get(i);

            float center_p = p + (ITEM_SIZE / 2f);
            float[] En = getEntry(fromDir);
            float[] Ex = getExit(belt.direction);

            float cx, cy;

            if (center_p < 0.5f) {
                float t = center_p * 2f;
                cx = En[0] + t * (0.5f - En[0]);
                cy = En[1] + t * (0.5f - En[1]);
            } else {
                float t = (center_p - 0.5f) * 2f;
                cx = 0.5f + t * (Ex[0] - 0.5f);
                cy = 0.5f + t * (Ex[1] - 0.5f);
            }

            float drawX = x + cx - (ITEM_SIZE / 2f);
            float drawY = y + cy - (ITEM_SIZE / 2f);

            if (type == ItemType.IRON_ORE) {
                batch.setColor(0.85f, 0.85f, 0.85f, transparency);
            } else if (type == ItemType.COPPER_ORE) {
                batch.setColor(0.9f, 0.5f, 0.2f, transparency);
            } else {
                batch.setColor(0.2f, 0.6f, 0.9f, transparency);
            }

            batch.draw(texture, drawX, drawY, ITEM_SIZE, ITEM_SIZE);
        }

        batch.setColor(Color.WHITE);
    }
}
