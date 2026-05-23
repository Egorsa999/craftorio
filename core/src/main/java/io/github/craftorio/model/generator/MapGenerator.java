package io.github.craftorio.model.generator;

import com.badlogic.gdx.math.MathUtils;
import io.github.craftorio.model.core.WorldMap;
import java.util.ArrayList;
import java.util.List;

public class MapGenerator {

    private static class OreConfig {
        ResourceType type;
        FastNoiseLite noise;
        float threshold;

        OreConfig(ResourceType type, FastNoiseLite noise, float threshold) {
            this.type = type;
            this.noise = noise;
            this.threshold = threshold;
        }
    }

    private static FastNoiseLite createOreNoise(int seed, float frequency) {
        FastNoiseLite noise = new FastNoiseLite(seed);
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        noise.SetFractalOctaves(3);
        noise.SetFrequency(frequency);
        return noise;
    }

    public static void generateMap(WorldMap worldMap) {
        int width = worldMap.getWidth();
        int height = worldMap.getHeight();

        int baseSeed = MathUtils.random(99999);

        FastNoiseLite terrainNoise = new FastNoiseLite(baseSeed);
        terrainNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        terrainNoise.SetFrequency(0.01f);

        FastNoiseLite wallNoise = new FastNoiseLite(baseSeed + 1);
        wallNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        wallNoise.SetFrequency(0.02f);

        List<OreConfig> ores = new ArrayList<>();

        ores.add(new OreConfig(ResourceType.COPPER, createOreNoise(baseSeed + 2, 0.013f), 0.85f));
        ores.add(new OreConfig(ResourceType.IRON, createOreNoise(baseSeed + 3, 0.014f), 0.85f));
        ores.add(new OreConfig(ResourceType.COAL, createOreNoise(baseSeed + 4, 0.012f), 0.86f));

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                // Генерация ландшафта
                float terrainValue = (terrainNoise.GetNoise(x, y) + 1.0f) / 2.0f;

                if (terrainValue < 0.2f) {
                    worldMap.setTerrainType(x, y, TerrainType.WATER);
                } else if (terrainValue < 0.3f) {
                    worldMap.setTerrainType(x, y, TerrainType.SAND);
                } else {
                    worldMap.setTerrainType(x, y, TerrainType.GRASS);

                    float wallValue = (wallNoise.GetNoise(x, y) + 1.0f) / 2.0f;

                    if (wallValue > 0.95f) {
                        worldMap.setTerrainType(x, y, TerrainType.WALL);
                    }
                }

                TerrainType currentTerrain = worldMap.getCell(x, y).getTerrainType();

                if (currentTerrain == TerrainType.GRASS || currentTerrain == TerrainType.SAND) {

                    for (OreConfig ore : ores) {
                        float rawOreValue = (ore.noise.GetNoise(x, y) + 1.0f) / 2.0f;

                        if (rawOreValue > ore.threshold) {
                            worldMap.setResourceType(x, y, ore.type);

                            break;
                        }
                    }
                }
            }
        }
    }
}
