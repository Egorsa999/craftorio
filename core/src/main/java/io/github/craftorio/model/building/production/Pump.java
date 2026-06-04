package io.github.craftorio.model.building.production;

import io.github.craftorio.BalanceConfig;
import io.github.craftorio.GameConfig;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.building.DamageableBuilding;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.building.ReceiveLiquid;
import io.github.craftorio.model.building.ThroughLiquid;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.core.WorldMap;
import io.github.craftorio.model.item.LiquidType;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class Pump extends DamageableBuilding implements ThroughLiquid {
    private static final float PRODUCTION_RATE = BalanceConfig.PUMP_PRODUCTION_RATE_PER_SECOND * GameConfig.TICK_TIME;
    private final float PRODUCTION_RATIO;
    private final LiquidType liquidType;

    public Pump(BuildingRegistry registry, Point anchor, Direction direction, WorldMap worldMap) {
        super(registry, anchor, direction, BuildingType.PUMP);
        this.liquidType = worldMap.getCell(anchor.x, anchor.y).getTerrainType().getLiquidType();
        this.PRODUCTION_RATIO = worldMap.getCell(anchor.x, anchor.y).getTerrainType().getRatio();
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
            if (!receiver.canReceiveLiquidFrom(this, sourcePoint, liquidType)) {
                continue;
            }
            targets.add(receiver);
        }

        if (targets.isEmpty()) {
            return;
        }

        float amountPerTarget = (PRODUCTION_RATIO * PRODUCTION_RATE) / targets.size();
        for (ReceiveLiquid receiver : targets) {
            receiver.receiveLiquid(this, liquidType, amountPerTarget);
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
