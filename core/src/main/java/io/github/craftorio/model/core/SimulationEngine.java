package io.github.craftorio.model.core;

import io.github.craftorio.model.entity.Bullet;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.enemy.Enemy;

import java.util.ArrayList;
import java.util.List;

public class SimulationEngine {
    private final List<Bullet> bullets = new ArrayList<>();

    public SimulationEngine() {}

    public void spawnBullet(Bullet b) {
        bullets.add(b);
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void update() {
        GameContext.current.registry.applyPendingChanges();

        for (Building building : GameContext.current.registry.getBuildingsForTick()) {
            building.update();
        }

        for (Enemy enemy : GameContext.current.waveSpawner.getEnemies()){
            enemy.update();
        }

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update(GameContext.current.waveSpawner.getEnemies());

            if (b.isDead()) {
                bullets.remove(i);
            }
        }
    }

    public List<Enemy> getEnemies() {
        return GameContext.current.waveSpawner.getEnemies();
    }
}
