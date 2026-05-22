package io.github.craftorio.model;

import io.github.craftorio.model.building.Belt;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.enemy.Enemy;
import io.github.craftorio.model.enemy.WaveSpawner;

public class SimulationEngine {
    private final BuildingRegistry registry;
    private final WaveSpawner waveSpawner;

    public SimulationEngine(BuildingRegistry registry, WaveSpawner waveSpawner) {
        this.registry = registry;
        this.waveSpawner = waveSpawner;
    }

    public void update() {
        registry.applyPendingChanges();
        for (Building building : registry.getBuildingsForTick()) {
            building.update();
        }

        for (Enemy enemy : waveSpawner.getEnemies()){
            enemy.update();
        }
    }
}
