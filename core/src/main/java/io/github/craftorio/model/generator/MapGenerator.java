package io.github.craftorio.model.generator;

import com.badlogic.gdx.math.MathUtils;
import io.github.craftorio.model.Cell;
import io.github.craftorio.model.WorldMap;

public class MapGenerator {
    static public void generateMap(WorldMap worldMap) {
        int width = worldMap.getWidth();
        int height = worldMap.getHeight();

        float seed = MathUtils.random(1000f);

        float frequency = 0.2f;
        float threshold = 0.98f;

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                float noise = calculateNoise(c * frequency, r * frequency, seed);

                Cell.resourceType type = Cell.resourceType.NONE;

                if (noise > threshold) {
                    type = Cell.resourceType.IRON;
                } else if (noise < -threshold) {
                    type = Cell.resourceType.COPPER;
                }

                worldMap.getCell(r, c).updateResourceType(type);
            }
        }
    }

    private static float calculateNoise(float x, float y, float seed) {
        float val = MathUtils.sin(x + seed) + MathUtils.cos(y + seed);
        val += MathUtils.sin(x * 0.5f) * MathUtils.cos(y * 0.5f);
        return val / 2.0f;
    }
}
