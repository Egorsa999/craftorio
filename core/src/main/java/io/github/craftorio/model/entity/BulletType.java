package io.github.craftorio.model.entity;

import io.github.craftorio.BalanceConfig;
import io.github.craftorio.model.item.ItemType;

public enum BulletType {
    COPPER(ItemType.COPPER_ORE, BalanceConfig.COPPER_ORE_SPEED, BalanceConfig.COPPER_ORE_DAMAGE) {
        @Override
        public Bullet create(float startX, float startY, float targetX, float targetY, float range) {
            return new Bullet(startX, startY, targetX, targetY, getSpeed(), getDamage(), range, getItemType());
        }
    },
    STANDARD(ItemType.BULLET, BalanceConfig.BULLET_SPEED, BalanceConfig.BULLET_DAMAGE) {
        @Override
        public Bullet create(float startX, float startY, float targetX, float targetY, float range) {
            return new Bullet(startX, startY, targetX, targetY, getSpeed(), getDamage(), range, getItemType());
        }
    },
    PIERCING(ItemType.IRON_ORE, BalanceConfig.PIERCING_BULLET_SPEED, BalanceConfig.PIERCING_BULLET_DAMAGE) {
        @Override
        public Bullet create(float startX, float startY, float targetX, float targetY, float range) {
            return new PiercingBullet(startX, startY, targetX, targetY, getSpeed(), getDamage(), range, getItemType());
        }
    };

    private final ItemType itemType;
    private final float speed;
    private final int damage;

    BulletType(ItemType itemType, float speed, int damage) {
        this.itemType = itemType;
        this.speed = speed;
        this.damage = damage;
    }

    public ItemType getItemType() { return itemType; }
    public float getSpeed() { return speed; }
    public int getDamage() { return damage; }

    public abstract Bullet create(float startX, float startY, float targetX, float targetY, float range);

    public static BulletType fromItemType(ItemType type) {
        for (BulletType bulletType : values()) {
            if (bulletType.getItemType() == type) {
                return bulletType;
            }
        }
        return null;
    }
}
