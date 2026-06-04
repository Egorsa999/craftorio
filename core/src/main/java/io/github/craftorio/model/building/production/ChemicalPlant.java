package io.github.craftorio.model.building.production;

import io.github.craftorio.BalanceConfig;
import io.github.craftorio.GameConfig;
import io.github.craftorio.model.building.*;
import io.github.craftorio.model.building.power.PowerConnectable;
import io.github.craftorio.model.building.power.PowerConsumer;
import io.github.craftorio.model.building.power.PowerNode;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.model.item.LiquidType;
import io.github.craftorio.model.item.Recipe;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChemicalPlant  extends DamageableBuilding implements ThroughLiquid, ReceiveLiquid, PowerConsumer, PowerConnectable, Craftable {

    private final PowerNode powerNode;
    private final CraftModule craftModule;

    private final ArrayList<Point> throughDelta = new ArrayList<>();
    private int lastThrough = 0;

    private final float maxPowerPerTick = BalanceConfig.CHEMICAL_PLANT_POWER_CONSUMPTION * GameConfig.TICK_TIME;

    private final float LIQUID_THROUGHPUT = 2.0f;

    public ChemicalPlant(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.CHEMICAL_PLANT);

        this.powerNode = new PowerNode(this, registry);
        this.craftModule = new CraftModule(0, 100.0f, List.of(Recipe.ROCKET_FUEL, Recipe.PLASTIC), true, maxPowerPerTick);

        throughDelta.add(new Point(+0, +2));
        throughDelta.add(new Point(+1, +2));
        throughDelta.add(new Point(+2, +1));
        throughDelta.add(new Point(+2, +0));
        throughDelta.add(new Point(+1, -1));
        throughDelta.add(new Point(+0, -1));
        throughDelta.add(new Point(-1, +0));
        throughDelta.add(new Point(-1, +1));
    }

    @Override
    public void update() {
        super.update();
        craftModule.update();

        Map<LiquidType, Float> outputs = craftModule.getOutputLiquids();
        for (Map.Entry<LiquidType, Float> entry : outputs.entrySet()) {
            if (entry.getValue() > 0) {
                float throughed = throughLiquid(entry.getKey(), Math.min(entry.getValue(), LIQUID_THROUGHPUT));
                if (throughed > 0) {
                    outputs.put(entry.getKey(), outputs.get(entry.getKey()) - throughed);
                    if (outputs.get(entry.getKey()) == 0) {
                        outputs.remove(entry.getKey());
                    }
                    break;
                }
            }
        }
    }

    @Override
    public float receiveLiquid(Building building, LiquidType id, float amount) {
        return craftModule.receiveLiquid(id, amount);
    }

    @Override
    public boolean canReceiveLiquidFrom(Building building, Point point, LiquidType type) {
        return true;
    }

    @Override
    public float throughLiquid(LiquidType type, float amount) {
        for (int iterate = 0; iterate <= throughDelta.size(); iterate++) {
            lastThrough++;
            lastThrough %= throughDelta.size();
            int x = getX() + throughDelta.get(lastThrough).x;
            int y = getY() + throughDelta.get(lastThrough).y;
            Building nextBuilding = registry.getBuildingAt(x, y);
            if (nextBuilding instanceof ReceiveLiquid building) {
                float received = building.receiveLiquid(this, type, amount);
                if (received > 0) {
                    return received;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean canThroughLiquidIn(Point point) {
        return true;
    }

    @Override
    public PowerNode getPowerNode() {
        return powerNode;
    }

    @Override
    public float getRequiredPower() {
        return craftModule.isCrafting() ? maxPowerPerTick : 0f;
    }

    @Override
    public void setSatisfactionRatio(float ratio) {
        craftModule.setSatisfactionRatio(ratio);
    }

    @Override
    public CraftModule getCraftModule() {
        return craftModule;
    }

    @Override
    public String getBuildingName() {
        return "Chemical Plant";
    }
}
