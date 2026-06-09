package io.github.craftorio.model.enemy;

import io.github.craftorio.GameConfig;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.core.WorldMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WaveSpawner {

    private final List<Enemy> activeEnemies = new ArrayList<>();
    private final List<Wave> predefinedWaves = new ArrayList<>();

    private final PathFinder pathFinder;
    private final BuildingRegistry registry;
    private final WorldMap worldMap;

    private final Random random = new Random();

    private final int MIN_OFFSET = 60;
    private final int SPREAD_RADIUS = 200;
    private final int SPAWN_DEPTH = 20;

    private int currentWaveIndex = 0;
    private float timeSinceLastWave = 0;

    private final float NORMAL_WAVE_INTERVAL = 300f;
    private final float SUDDEN_DEATH_INTERVAL = 10f;

    private boolean isInfiniteMode = false;
    private int infiniteWaveCount = 0;

    private boolean isWaveActive = false;

    public WaveSpawner(PathFinder pathFinder, BuildingRegistry registry, WorldMap worldMap) {
        this.pathFinder = pathFinder;
        this.registry = registry;
        this.worldMap = worldMap;

        initPredefinedWaves();
    }

    private void initPredefinedWaves() {
        this.predefinedWaves.add(new Wave.Builder(SpawnDirection.EAST)
            .addEnemies(EnemyType.BASIC_ENEMY, 10).build());

        this.predefinedWaves.add(new Wave.Builder(SpawnDirection.WEST)
            .addEnemies(EnemyType.BASIC_ENEMY, 20)
            .addEnemies(EnemyType.FAT_ENEMY, 4).build());

        this.predefinedWaves.add(new Wave.Builder(SpawnDirection.NORTH)
            .addEnemies(EnemyType.BASIC_ENEMY, 30)
            .addEnemies(EnemyType.FAST_ENEMY, 15).build());

        this.predefinedWaves.add(new Wave.Builder(SpawnDirection.SOUTH)
            .addEnemies(EnemyType.BASIC_ENEMY, 45)
            .addEnemies(EnemyType.FAT_ENEMY, 15)
            .addEnemies(EnemyType.FAST_ENEMY, 20).build());

        this.predefinedWaves.add(new Wave.Builder(SpawnDirection.EAST)
            .addEnemies(EnemyType.BASIC_ENEMY, 150)
            .addEnemies(EnemyType.FAT_ENEMY, 30)
            .addEnemies(EnemyType.FAST_ENEMY, 70).build());
    }

    public void update() {
        activeEnemies.removeIf(Enemy::isDead);

        if (!GameConfig.SPAWN_ENEMY) return;

        if (!isInfiniteMode) {
            if (isWaveActive) {
                if (activeEnemies.isEmpty()) {
                    isWaveActive = false;
                    timeSinceLastWave = 0;
                }
            } else {
                timeSinceLastWave += GameConfig.TICK_TIME * 100;

                if (timeSinceLastWave >= NORMAL_WAVE_INTERVAL) {
                    if (currentWaveIndex < predefinedWaves.size()) {
                        spawnPredefinedWave(currentWaveIndex);
                        currentWaveIndex++;
                        isWaveActive = true;
                    } else {
                        isWaveActive = true;
                        isInfiniteMode = true;
                        spawnInfiniteWave();
                        timeSinceLastWave = 0;
                    }
                }
            }
        } else {
            timeSinceLastWave += GameConfig.TICK_TIME * 100;

            if (timeSinceLastWave >= SUDDEN_DEATH_INTERVAL) {
                spawnInfiniteWave();
                timeSinceLastWave = 0;
            }
        }
    }

    public boolean isPreparingForInfinite() {
        // Подготовка идет только когда волна НЕ активна и все сюжетные волны закончились
        return !isInfiniteMode && !isWaveActive && currentWaveIndex >= predefinedWaves.size();
    }

    private void spawnPredefinedWave(int index) {
        executeSpawn(predefinedWaves.get(index));
    }

    private void spawnInfiniteWave() {
        infiniteWaveCount++;

        SpawnDirection[] directions = SpawnDirection.values();
        SpawnDirection randomDir = directions[random.nextInt(directions.length)];

        int basicCount = 10 + (infiniteWaveCount * 2);
        int fatCount = 2 + infiniteWaveCount;
        int fastCount = 5 + (infiniteWaveCount * 2);

        Wave proceduralWave = new Wave.Builder(randomDir)
            .addEnemies(EnemyType.BASIC_ENEMY, basicCount)
            .addEnemies(EnemyType.FAT_ENEMY, fatCount)
            .addEnemies(EnemyType.FAST_ENEMY, fastCount)
            .build();

        executeSpawn(proceduralWave);
    }

    private void executeSpawn(Wave wave) {
        List<int[]> spawnPool = calculateSpawnPool(wave.getDirection(), 3f);

        if (spawnPool.isEmpty()) {
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

        return true;
    }

    public void spawnEnemy(float x, float y, EnemyType type){
        activeEnemies.add(new Enemy(x, y, type, pathFinder, registry, activeEnemies, worldMap));
    }


    public SpawnDirection getCurrentWaveDirection() {
        if (isInfiniteMode) {
            return null;
        }

        if (currentWaveIndex < predefinedWaves.size()) {
            return predefinedWaves.get(currentWaveIndex).getDirection();
        }

        return null;
    }

    public int getCurrentWaveNumber() {
        if (isInfiniteMode) {
            return predefinedWaves.size() + (isWaveActive ? infiniteWaveCount : infiniteWaveCount + 1);
        }

        return isWaveActive ? currentWaveIndex : currentWaveIndex + 1;
    }

    public float getTimeRemainingUntilNextWave() {
        float currentTargetInterval = isInfiniteMode ? SUDDEN_DEATH_INTERVAL : NORMAL_WAVE_INTERVAL;
        return Math.max(0, currentTargetInterval - timeSinceLastWave);
    }

    public boolean isInfiniteMode() {
        return isInfiniteMode;
    }

    // Геттер состояния волны
    public boolean isWaveActive() {
        return isWaveActive;
    }

    public List<Enemy> getActiveEnemies() {
        return activeEnemies;
    }

    public PathFinder getPathFinder() {
        return this.pathFinder;
    }
}
