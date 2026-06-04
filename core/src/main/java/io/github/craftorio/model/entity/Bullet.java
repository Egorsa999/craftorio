package io.github.craftorio.model.entity;

import com.badlogic.gdx.math.MathUtils;
import io.github.craftorio.model.enemy.Enemy;
import io.github.craftorio.model.item.ItemType;

import java.util.List;

public class Bullet {
    protected float x, y;
    protected float velocityX;
    protected float velocityY;
    protected float speed;
    protected int damage;

    protected float rotationDeg;
    protected boolean isDead = false;

    protected float distanceTraveled = 0f;
    protected float maxDistance;
    protected ItemType ammoType;

    public Bullet(float startX, float startY, float targetX, float targetY, float speed, int damage, float maxDistance, ItemType ammoType) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.damage = damage;
        this.maxDistance = maxDistance;
        this.ammoType = ammoType;

        float dx = targetX - startX;
        float dy = targetY - startY;

        float angleRad = (float) Math.atan2(dy, dx);

        this.velocityX = (float) Math.cos(angleRad) * speed;
        this.velocityY = (float) Math.sin(angleRad) * speed;

        this.rotationDeg = angleRad * MathUtils.radiansToDegrees - 90f;
    }

    public void update(List<Enemy> enemies) {
        if (isDead) return;

        this.x += velocityX;
        this.y += velocityY;

        distanceTraveled += speed;
        if (distanceTraveled > maxDistance) {
            isDead = true;
            return;
        }

        checkCollisions(enemies);
    }

    protected void checkCollisions(List<Enemy> enemies) {
        float hitRadius = 0.5f;

        for (Enemy enemy : enemies) {
            if (enemy.isDead()) continue;

            float dx = enemy.getX() - this.x;
            float dy = enemy.getY() - this.y;
            float dist = (float) Math.hypot(dx, dy);

            if (dist < hitRadius) {
                enemy.receiveDamage(damage);
                isDead = true;
                return;
            }
        }
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getRotationDeg() { return rotationDeg; }
    public boolean isDead() { return isDead; }
    public ItemType getAmmoType() { return ammoType; }
}
