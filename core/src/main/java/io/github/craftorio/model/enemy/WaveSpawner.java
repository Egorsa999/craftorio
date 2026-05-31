package io.github.craftorio.model.enemy;

import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.core.WorldMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WaveSpawner {

    private final List<Enemy> activeEnemies = new ArrayList<>();

    private final List<Wave> waves = new ArrayList<>();

    private final PathFinder pathFinder;
    private final BuildingRegistry registry;
    private final WorldMap worldMap;

    private final Random random = new Random();

    private final int MIN_OFFSET = 60;
    private final int SPREAD_RADIUS = 200;
    private final int SPAWN_DEPTH = 20;

    public WaveSpawner(PathFinder pathFinder, BuildingRegistry registry, WorldMap worldMap) {
        this.pathFinder = pathFinder;
        this.registry = registry;
        this.worldMap = worldMap;

        this.waves.add(new Wave.Builder(SpawnDirection.EAST)
            .addEnemies(EnemyType.BASIC_ENEMY, 10).build());

        this.waves.add(new Wave.Builder(SpawnDirection.EAST)
            .addEnemies(EnemyType.BASIC_ENEMY, 15)
            .addEnemies(EnemyType.FAT_ENEMY, 3).build());

        this.waves.add(new Wave.Builder(SpawnDirection.EAST)
            .addEnemies(EnemyType.BASIC_ENEMY, 1000)
            .addEnemies(EnemyType.FAT_ENEMY, 100)
            .addEnemies(EnemyType.FAST_ENEMY, 400).build());
    }



    public void spawnWave(int waveIndex) {
        if (waveIndex < 0 || waveIndex >= waves.size()) {
            System.err.println("Волны с номером " + waveIndex + " не существует!");
            return;
        }

        Wave wave = waves.get(waveIndex);
        List<int[]> spawnPool = calculateSpawnPool(wave.getDirection(), 3f);

        if (spawnPool.isEmpty()) {
            System.err.println("Нет валидных клеток для спавна волны " + waveIndex);
            return;
        }

        for (Map.Entry<EnemyType, Integer> entry : wave.getEnemies().entrySet()) {
            EnemyType type = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {
                int[] coords = spawnPool.get(random.nextInt(spawnPool.size()));

                float exactX = coords[0] + random.nextFloat();
                float exactY = coords[1] + random.nextFloat();

                activeEnemies.add(new Enemy(exactX, exactY, type, pathFinder, registry, activeEnemies, worldMap));
            }
        }
    }

    public void update() {
        activeEnemies.removeIf(Enemy::isDead);
    }

    private List<int[]> calculateSpawnPool(SpawnDirection dir, float maxHitbox) {
        List<int[]> pool = new ArrayList<>();

        int minX = 0, maxX = 0, minY = 0, maxY = 0;


        int coreX = worldMap.getCoreX();
        int coreY = worldMap.getCoreY();

        switch (dir) {
            case EAST:
                minX = Math.round(registry.getMaxX()) + MIN_OFFSET;
                maxX = minX + SPAWN_DEPTH;
                minY = coreY - (SPREAD_RADIUS / 2);
                maxY = coreY + (SPREAD_RADIUS / 2);
                break;
            case WEST:
                maxX = Math.round(registry.getMinX()) - MIN_OFFSET;
                minX = maxX - SPAWN_DEPTH;
                minY = coreY - (SPREAD_RADIUS / 2);
                maxY = coreY + (SPREAD_RADIUS / 2);
                break;
            case NORTH:
                minY = Math.round(registry.getMaxY()) + MIN_OFFSET;
                maxY = minY + SPAWN_DEPTH;
                minX = coreX - (SPREAD_RADIUS / 2);
                maxX = coreX + (SPREAD_RADIUS / 2);
                break;
            case SOUTH:
                maxY = Math.round(registry.getMinY()) - MIN_OFFSET;
                minY = maxY - SPAWN_DEPTH;
                minX = coreX - (SPREAD_RADIUS / 2);
                maxX = coreX + (SPREAD_RADIUS / 2);
                break;
        }

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (isAreaWalkable(x, y, maxHitbox)) {
                    pool.add(new int[]{x, y});
                }
            }
        }

        return pool;
    }

    private boolean isAreaWalkable(int centerX, int centerY, float requiredHitbox) {
        int radius = (int) Math.ceil(requiredHitbox / 2.0f) + 1;

        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                int checkX = centerX + i;
                int checkY = centerY + j;

                var cell = worldMap.getCell(checkX, checkY);

                if (cell == null || !cell.getTerrainType().getWalkability()) {
                    return false;
                }
            }
        }

        return true; // Вся зона вокруг точки абсолютно чистая
    }

    public void spawnEnemy(float x, float y, EnemyType type){
        activeEnemies.add(new Enemy(x, y, type, pathFinder, registry, activeEnemies, worldMap));
    }

    public List<Enemy> getActiveEnemies() {
        return activeEnemies;
    }

    public PathFinder getPathFinder() {
        return this.pathFinder;
    }
}
