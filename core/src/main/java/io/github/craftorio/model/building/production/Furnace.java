package io.github.craftorio.model.building.production;

import io.github.craftorio.model.building.*;
import io.github.craftorio.model.building.power.PowerConnectable;
import io.github.craftorio.model.building.power.PowerConsumer;
import io.github.craftorio.model.building.power.PowerNode;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.model.item.Recipe;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Furnace extends DamageableBuilding implements ThroughItem, ReceiveItem, PowerConsumer, PowerConnectable, Craftable {
    private final PowerNode powerNode;
    private final CraftModule craftModule;

    private final ArrayList<Point> throughDelta = new ArrayList<>();
    private int lastThrough = 0;

    private final float maxPowerPerTick = 150.0f / 60.0f;

    public Furnace(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.FURNACE);

        this.powerNode = new PowerNode(this, registry);
        this.craftModule = new CraftModule(50, 0.0f, List.of(Recipe.IRON_INGOT));

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

        Map<ItemType, Integer> outputs = craftModule.getOutputItems();
        for (Map.Entry<ItemType, Integer> entry : outputs.entrySet()) {
            if (entry.getValue() > 0) {
                if (throughItem(entry.getKey())) {
                    outputs.put(entry.getKey(), outputs.get(entry.getKey()) - 1);
                    if (outputs.get(entry.getKey()) == 0) {
                        outputs.remove(entry.getKey());
                    }
                    break;
                }
            }
        }
    }

    @Override
    public boolean receiveItem(Building building, ItemType id) {
        return craftModule.receiveItem(id);
    }

    @Override
    public boolean canReceiveItemFrom(Building building, Point point) {
        return true;
    }

    @Override
    public boolean throughItem(ItemType type) {
        for (int iterate = 0; iterate <= throughDelta.size(); iterate++) {
            lastThrough++;
            lastThrough %= throughDelta.size();
            int x = getX() + throughDelta.get(lastThrough).x;
            int y = getY() + throughDelta.get(lastThrough).y;
            Building nextBuilding = registry.getBuildingAt(x, y);
            if (nextBuilding instanceof ReceiveItem building) {
                if (building.receiveItem(this, type)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canThroughItemIn(Point point) {
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
        return "Furnace";
    }
}
