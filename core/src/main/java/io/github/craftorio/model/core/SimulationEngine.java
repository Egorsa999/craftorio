package io.github.craftorio.model.core;

import io.github.craftorio.model.entity.Bullet;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.enemy.Enemy;
import io.github.craftorio.model.enemy.WaveSpawner;

import java.util.List;

public class SimulationEngine {
    private final BuildingRegistry registry;
    private final WaveSpawner waveSpawner;
    private final List<Bullet> bullets = new java.util.ArrayList<>();

    public SimulationEngine(BuildingRegistry registry, WaveSpawner waveSpawner) {
        this.registry = registry;
        this.waveSpawner = waveSpawner;
    }

    public void spawnBullet(Bullet b) {
        bullets.add(b);
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void update() {
        registry.applyPendingChanges();
        for (Building building : registry.getBuildingsForTick()) {
            building.update();
        }

        for (Enemy enemy : waveSpawner.getEnemies()){
            enemy.update();
        }

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update(waveSpawner.getEnemies());

            if (b.isDead()) {
                bullets.remove(i);
            }
        }
    }

    public List<Enemy> getEnemies() {
        return waveSpawner.getEnemies();
    }
}
