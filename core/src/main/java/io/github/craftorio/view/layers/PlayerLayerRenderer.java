package io.github.craftorio.view.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.entity.Player;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.TextureRenderer;
import io.github.craftorio.view.VisibleBounds;

public class PlayerLayerRenderer implements LayerRenderer{

    private final Player player;
    private final TextureLoad textures;

    public PlayerLayerRenderer(Player player, TextureLoad textures) {
        this.player = player;
        this.textures = textures;
    }

    @Override
    public void render(SpriteBatch batch, VisibleBounds bounds, float stateTime) {
        String baseName = "player";
        if (player.isMoving()) baseName += "_run";
        else baseName += "_idle";

        boolean flipX = false;

        if (player.getDirection() == Direction.LEFT)flipX = true;

        switch (player.getDirection()){
            case UP:
                baseName += "_up";
                break;
            case DOWN:
                baseName += "_down";
                break;
            case LEFT, RIGHT:
                baseName += "_side";
                break;
        }

        float x = player.playerX - (1f / 2f);
        float y = player.playerY - (1f / 2f);

        float drawWidth = 1f;

        if (flipX) {
            x += 1f;
            drawWidth = -1f;
        }


        TextureRenderer.draw(
            batch, textures.get(baseName),
            x, y,
            drawWidth, 1f,
            0, null,
            stateTime
        );
    }
}
