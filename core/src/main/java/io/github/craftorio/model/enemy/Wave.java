package io.github.craftorio.model.enemy;

import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.enemy.Enemy;
import io.github.craftorio.model.enemy.EnemyType;
import io.github.craftorio.model.enemy.SpawnDirection;

import java.util.HashMap;
import java.util.Map;

public class Wave {

    private final SpawnDirection direction;
    private final Map<EnemyType, Integer> enemies;

    public Wave(SpawnDirection direction, Map<EnemyType, Integer> enemies){
        this.direction = direction;
        this.enemies = enemies;
    }

    public Wave addEnemies(EnemyType type, int number){
        enemies.put(type, enemies.getOrDefault(type, 0) + number);
        return this;
    }

    public SpawnDirection getDirection() {
        return direction;
    }

    public Map<EnemyType, Integer> getEnemies() {
        return enemies;
    }

    public static class Builder{
        private final Map<EnemyType, Integer> enemies = new HashMap<>();
        private final SpawnDirection direction;

        public Builder(SpawnDirection direction){
            this.direction = direction;
        }

        public Builder addEnemies(EnemyType type, int number){
            enemies.put(type, enemies.getOrDefault(type, 0) + number);
            return this;
        }

        public Wave build(){
            return new Wave(direction, enemies);
        }
    }

}
