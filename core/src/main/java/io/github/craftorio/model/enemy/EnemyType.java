package io.github.craftorio.model.enemy;

public enum EnemyType {
    FAT_ENEMY(300, 1/20f, 100, 90, 2.3f, 3f),
    BASIC_ENEMY(20, 1/20f, 15, 30, 0.75f, 1f),
    FAST_ENEMY(20, 1/12f, 15, 10, 0.75f, 1f);

    public float getTextureSize() {
        return textureSize;
    }

    public float getHitbox() {
        return hitbox;
    }

    public int getHp() {
        return hp;
    }

    public int getDamage() {
        return damage;
    }

    public int getCoolDown() {
        return coolDown;
    }

    public float getSpeed() {
        return speed;
    }

    private final float speed;
    private final int coolDown;
    private final int damage;
    private final int hp;
    private final float hitbox;
    private final float textureSize;

    EnemyType(int hp, float speed, int damage, int coolDown, float hitbox, float textureSize){
        this.hp = hp;
        this.speed = speed;
        this.damage = damage;
        this.coolDown = coolDown;
        this.hitbox = hitbox;
        this.textureSize = textureSize;
    }
}
