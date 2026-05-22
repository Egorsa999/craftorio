package io.github.craftorio.model.enemy;

import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.WorldMap;

import java.util.ArrayList;
import java.util.List;

public class WaveSpawner {
    private List<Enemy> enemies = new ArrayList<>();
    private PathFinder pathFinder;
    private BuildingRegistry registry;
    private WorldMap worldMap;


    public void addEnemy(float x, float y){
        enemies.add(new Enemy(x, y, 1/20f, pathFinder, registry, enemies, worldMap));
    }

    public List<Enemy> getEnemies(){
        return enemies;
    }

    public WaveSpawner(PathFinder pathFinder, BuildingRegistry registry, WorldMap worldMap) {
        this.pathFinder = pathFinder;
        this.registry = registry;
        this.worldMap = worldMap;
    }


}
