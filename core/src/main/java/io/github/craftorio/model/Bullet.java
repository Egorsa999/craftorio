package io.github.craftorio.model;

import com.badlogic.gdx.math.MathUtils;
import io.github.craftorio.model.enemy.Enemy;

import java.util.List;

public class Bullet {
    private float x, y;
    private float velocityX;
    private float velocityY;
    private float speed;
    private int damage;

    private float rotationDeg;
    private boolean isDead = false;

    private float distanceTraveled = 0f;
    private static final float MAX_DISTANCE = 15f;

    public Bullet(float startX, float startY, float targetX, float targetY, float speed, int damage) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.damage = damage;

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
        if (distanceTraveled > MAX_DISTANCE) {
            isDead = true;
            return;
        }
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
}
