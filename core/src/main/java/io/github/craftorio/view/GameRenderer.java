package io.github.craftorio.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.craftorio.model.World;

public class GameRenderer {
    private final World world;
    private final ShapeRenderer shapeRenderer;

    // The View needs a reference to the Model to know where to draw objects
    public GameRenderer(World world) {
        this.world = world;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render() {
        // Clear the screen and fill it with a dark gray color
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Start drawing geometric shapes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Get coordinates from the model and draw an orange square (50x50 pixels)
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.rect(world.playerX, world.playerY, 50, 50);

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose(); // Free memory when closing the game
    }
}
