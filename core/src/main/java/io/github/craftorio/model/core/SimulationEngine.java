package io.github.craftorio.model.core;

import io.github.craftorio.model.building.liquid.LiquidNetworkManager;
import io.github.craftorio.model.building.liquid.LiquidNetworkNode;
import io.github.craftorio.model.building.power.PowerConnectable;
import io.github.craftorio.model.building.power.PowerNetwork;
import io.github.craftorio.model.enemy.WaveSpawner;
import io.github.craftorio.model.entity.Bullet;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.enemy.Enemy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimulationEngine {
    private final List<Bullet> bullets = new ArrayList<>();
    private final BuildingRegistry registry;
    private final WaveSpawner waveSpawner;
    private final LiquidNetworkManager liquidNetworkManager;

    public SimulationEngine(BuildingRegistry registry, WaveSpawner waveSpawner, LiquidNetworkManager liquidNetworkManager) {
        this.registry = registry;
        this.waveSpawner = waveSpawner;
        this.liquidNetworkManager = liquidNetworkManager;
    }

    public void spawnBullet(Bullet b) {
        bullets.add(b);
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void update() {
        registry.applyPendingChanges();

        if (registry.consumeLiquidNetworksDirty()) {
            List<LiquidNetworkNode> nodes = new ArrayList<>();
            for (Building building : registry.getBuildingsForTick()) {
                if (building instanceof LiquidNetworkNode node) {
                    nodes.add(node);
                }
            }
            liquidNetworkManager.rebuild(nodes);
        }

        Set<PowerNetwork> networks = new HashSet<>();
        for (Building building : registry.getBuildingsForTick()) {
            building.update();
            if (building instanceof PowerConnectable cBuilding)
                networks.add(cBuilding.getPowerNode().getNetwork());
        }

        for (PowerNetwork network : networks){
            network.update();
        }

        liquidNetworkManager.tick();

        for (Enemy enemy : waveSpawner.getActiveEnemies()){
            enemy.update();
        }

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update(waveSpawner.getActiveEnemies());

            if (b.isDead()) {
                bullets.remove(i);
            }
        }

        waveSpawner.update();
    }

    public List<Enemy> getEnemies() {
        return waveSpawner.getActiveEnemies();
    }
}
