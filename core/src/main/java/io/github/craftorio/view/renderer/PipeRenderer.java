package io.github.craftorio.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.craftorio.model.building.liquid.LiquidNetwork;
import io.github.craftorio.model.building.logistics.Pipe;
import io.github.craftorio.model.item.LiquidType;
import io.github.craftorio.view.sprite.GameSprite;

import java.util.HashMap;

public class PipeRenderer {
    private static final Color EMPTY_PIPE_TINT = new Color(30f / 255f, 30f / 255f, 30f / 255f, 0.3f);

    public static void draw(
        Color colorFilter,
        SpriteBatch batch,
        HashMap<Integer, GameSprite> bottomTextures,
        HashMap<Integer, GameSprite> topTextures,
        Pipe pipe,
        float stateTime
    ) {
        float x = pipe.getX();
        float y = pipe.getY();
        int pipeType = pipe.getPipeType();

        Color bottomTint = resolveBottomTint(pipe);
        if (colorFilter != null) {
            bottomTint = bottomTint.cpy().mul(colorFilter);
        }
        batch.setColor(bottomTint);
        batch.draw(
            bottomTextures.get(pipeType).getFirstFrame(),
            x, y,
            0.5f, 0.5f,
            1f, 1f,
            pipe.getReflection(), 1f,
            -pipe.getRotation()
        );

        if (colorFilter != null) {
            batch.setColor(colorFilter);
        } else {
            batch.setColor(Color.WHITE);
        }
        batch.draw(
            topTextures.get(pipeType).getFirstFrame(),
            x, y,
            0.5f, 0.5f,
            1f, 1f,
            pipe.getReflection(), 1f,
            -pipe.getRotation()
        );

        batch.setColor(Color.WHITE);
    }

    private static Color resolveBottomTint(Pipe pipe) {
        LiquidNetwork network = pipe.getNetwork();
        if (network == null) {
            return EMPTY_PIPE_TINT.cpy();
        }

        LiquidType liquidType = network.getLiquidType();
        if (liquidType == null) {
            return EMPTY_PIPE_TINT.cpy();
        }

        float fillRatio = network.getFillRatio();
        Color liquidColor = toGdxColor(liquidType.getColor());

        float alpha = 0.3f + (0.7f * fillRatio);

        return new Color(
            liquidColor.r,
            liquidColor.g,
            liquidColor.b,
            alpha
        );
    }
    private static Color toGdxColor(java.awt.Color color) {
        return new Color(
            color.getRed() / 255f,
            color.getGreen() / 255f,
            color.getBlue() / 255f,
            color.getAlpha() / 255f
        );
    }
}
