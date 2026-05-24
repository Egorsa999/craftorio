package io.github.craftorio.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.model.building.logistics.Belt;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.view.layers.LayerRenderer;
import io.github.craftorio.view.VisibleBounds;
import io.github.craftorio.view.sprite.GameSprite;
import io.github.craftorio.view.TextureLoad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BeltRenderer implements LayerRenderer {
    private static final float ITEM_SIZE = 0.6f;
    private static final float HALF_LOGIC_SIZE = 1f / 6f;

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

    public static void drawBackground(Color colorFilter, SpriteBatch batch, HashMap<Integer, GameSprite> texture, Belt belt, float stateTime, float transparency) {
        float x = belt.getX();
        float y = belt.getY();
        if (colorFilter != null) batch.setColor(colorFilter);
        batch.draw(texture.get(belt.getBeltType()).getKeyFrame(stateTime), x, y, 0.5f, 0.5f, 1f, 1f, belt.getReflection(), 1f, -belt.getRotation());

        batch.setColor(Color.WHITE);
    }

    public static void drawItems(SpriteBatch batch, TextureLoad textureLoad, Belt belt, float globalOffset, float transparency) {
        float x = belt.getX();
        float y = belt.getY();

        ArrayList<Integer> order = new ArrayList<>();
        for (int i = 0; i < belt.getItemId().size(); i++) {
            order.add(i);
        }

        if (belt.direction == Direction.RIGHT || belt.direction == Direction.DOWN) {
            Collections.reverse(order);
        }

        for (int i : order) {
            ItemType type = belt.getItemId().get(i);
            float p = belt.getItemProgress().get(i);
            Direction fromDir = belt.getItemFrom().get(i);

            float center_p = p + HALF_LOGIC_SIZE;

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
            batch.setColor(Color.WHITE);
            batch.draw(textureLoad.get(type).getFirstFrame(), drawX, drawY, ITEM_SIZE, ITEM_SIZE);
        }

        batch.setColor(Color.WHITE);
    }

    @Override
    public void render(SpriteBatch batch, VisibleBounds bounds, float stateTime) {

    }
}
