package io.github.craftorio.model.entity;

import io.github.craftorio.model.enemy.Enemy;
import io.github.craftorio.model.item.ItemType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PiercingBullet extends Bullet {

    private final Set<Enemy> damagedEnemies = new HashSet<>();

    public PiercingBullet(float startX, float startY, float targetX, float targetY, float speed, int damage, float maxDistance, ItemType ammoType) {
        super(startX, startY, targetX, targetY, speed, damage, maxDistance, ammoType);
    }

    @Override
    protected void checkCollisions(List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (enemy.isDead() || damagedEnemies.contains(enemy)) continue;

            float dx = enemy.getX() - this.x;
            float dy = enemy.getY() - this.y;
            float dist = (float) Math.hypot(dx, dy);

            if (dist < enemy.getHitRadius()) {
                enemy.receiveDamage(damage);
                damagedEnemies.add(enemy);
            }
        }
    }
}
