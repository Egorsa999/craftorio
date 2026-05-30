package io.github.craftorio.model.building.production;

import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.DamageableBuilding;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.building.ReceiveLiquid;
import io.github.craftorio.model.building.ThroughLiquid;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.LiquidType;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class Pump extends DamageableBuilding implements ThroughLiquid {
    private static final float PRODUCTION_RATE = 5f;

    public Pump(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.PUMP);
    }

    @Override
    public float throughLiquid(LiquidType type, float amount) {
        return 0f;
    }

    @Override
    public boolean canThroughLiquidIn(Point point) {
        int x = getX();
        int y = getY();
        return point.x == x + 1 && point.y == y
            || point.x == x - 1 && point.y == y
            || point.x == x && point.y == y + 1
            || point.x == x && point.y == y - 1;
    }

    @Override
    public void update() {
        super.update();

        Point sourcePoint = new Point(getX(), getY());
        Set<ReceiveLiquid> targets = new HashSet<>();

        for (Direction side : Direction.values()) {
            Building building = getNeighborBuilding(side);
            if (!(building instanceof ReceiveLiquid receiver)) {
                continue;
            }
            if (!receiver.canReceiveLiquidFrom(this, sourcePoint)) {
                continue;
            }
            targets.add(receiver);
        }

        if (targets.isEmpty()) {
            return;
        }

        float amountPerTarget = PRODUCTION_RATE / targets.size();
        for (ReceiveLiquid receiver : targets) {
            receiver.receiveLiquid(this, LiquidType.WATER, amountPerTarget);
        }
    }

    private Building getNeighborBuilding(Direction side) {
        int nx = getX();
        int ny = getY();

        switch (side) {
            case RIGHT -> nx++;
            case LEFT -> nx--;
            case UP -> ny++;
            case DOWN -> ny--;
        }

        return registry.getBuildingAt(nx, ny);
    }
}
