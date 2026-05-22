package io.github.craftorio.model.building;

import io.github.craftorio.model.BuildingRegistry;

import java.awt.*;

public abstract class DamageableBuilding extends Building implements Damagable {
    private int currentHP = 0;

    private int flashTimer = 0;
    private static final int FLASH_DURATION = 5;

    public void update(){
        if (flashTimer > 0)flashTimer--;
    }

    public DamageableBuilding(BuildingRegistry registry, Point anchor, Direction direction, BuildingType type) {
        super(registry, anchor, direction, type);
        currentHP = type.getMaxHP();
    }

    public boolean isReceivingDamage(){
        return flashTimer > 0;
    }

    @Override
    public void receiveDamage(int power) {
        currentHP -= power;
        if (currentHP <= 0) {
            removeSelf();
        }
        flashTimer = FLASH_DURATION;
    }

    @Override
    public int getHP() {
        return this.currentHP;
    }
}
