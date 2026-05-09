package io.github.craftorio.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    private static Direction getOpposite(Direction d) {
        switch (d) {
            case UP: return Direction.DOWN;
            case DOWN: return Direction.UP;
            case LEFT: return Direction.RIGHT;
            case RIGHT: return Direction.LEFT;
        }
        return Direction.DOWN;
    }

    public static void drawBackground(ShapeRenderer shapeRenderer, Belt belt, float globalOffset, float transparency) {
        float x = belt.getX();
        float y = belt.getY();

        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, transparency);
        shapeRenderer.rect(x, y, 1, 1);

        shapeRenderer.setColor(0f, 1f, 0f, transparency);
        float thick = 0.20f;
        switch (belt.direction) {
            case RIGHT: shapeRenderer.rect(x + 1f - thick, y, thick, 1); break;
            case LEFT:  shapeRenderer.rect(x, y, thick, 1); break;
            case UP:    shapeRenderer.rect(x, y + 1f - thick, 1, thick); break;
            case DOWN:  shapeRenderer.rect(x, y, 1, thick); break;
        }
    }

    public static void drawItems(ShapeRenderer shapeRenderer, Belt belt, float globalOffset, float transparency) {
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

            if (type == ItemType.IRON_ORE) shapeRenderer.setColor(0.85f, 0.85f, 0.85f, transparency);
            else if (type == ItemType.COPPER_ORE) shapeRenderer.setColor(0.9f, 0.5f, 0.2f, transparency);
            else shapeRenderer.setColor(0.2f, 0.6f, 0.9f, transparency);

            shapeRenderer.rect(drawX, drawY, ITEM_SIZE, ITEM_SIZE);
        }

        batch.draw(conveyorTexture, x, y, 0.5f, 0.5f, 1f, 1f, 1f, 1f, rotation);

        batch.setColor(Color.WHITE);
    }
}
