package io.github.craftorio.model.building;

import io.github.craftorio.model.BuildingRegistry;

import java.awt.*;

public abstract class DamageableBuilding extends Building implements Damagable {
    private int currentHP = 0;

    public DamageableBuilding(BuildingRegistry registry, Point anchor, Direction direction, BuildingType type) {
        super(registry, anchor, direction, type);
        currentHP = type.getMaxHP();
    }

    @Override
    public void receiveDamage(int power) {
        currentHP -= power;
        if (currentHP <= 0) {
            removeSelf();
        }
    }

    @Override
    public int getHP() {
        return this.currentHP;
    }
}
