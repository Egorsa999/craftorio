package io.github.craftorio.model.building.logistics;

import io.github.craftorio.GameConfig;
import io.github.craftorio.model.building.*;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.ItemType;

import java.awt.*;
import java.util.ArrayList;

public class Belt extends DamageableBuilding implements ThroughItem, ReceiveItem {
    private static final float speed = 1f * GameConfig.TICK_TIME;
    private static final float ItemSize = 1f / 3f;
    private static float animationOffset = 0.0f;

    private ArrayList<ItemType> itemId;
    private ArrayList<Float> itemProgress;
    private ArrayList<Direction> itemFrom;

    private int beltType;
    private float rotation;
    private float reflection;

    public Belt(BuildingRegistry registry, Point anchor, Direction direction) {
        super(registry, anchor, direction, BuildingType.BELT);
        this.direction = direction;
        this.itemId = new ArrayList<>();
        this.itemProgress = new ArrayList<>();
        this.itemFrom = new ArrayList<>();

        this.beltType = 0;
        this.rotation = dirToInt(direction) * 90f;
        this.reflection = 1f;

        updateType();
    }

    @Override
    public void setAnchor(int x, int y){
        super.setAnchor(x, y);
        updateType();
    }

    public static void updateAnimationOffset(float delta) {
        animationOffset += speed * delta;
        animationOffset %= 1;
    }

    public static float getAnimationOffset() {
        return animationOffset;
    }

    private Building getUpperBuilding() {
        int nextCol = getX();
        int nextRow = getY();

        switch (direction) {
            case RIGHT: nextCol++; break;
            case LEFT:  nextCol--; break;
            case UP:    nextRow++; break;
            case DOWN:  nextRow--; break;
        }

        return registry.getBuildingAt(nextCol, nextRow);
    }

    private Building getDownBuilding() {
        int nextCol = getX();
        int nextRow = getY();

        switch (direction) {
            case RIGHT: nextCol--; break;
            case LEFT:  nextCol++; break;
            case UP:    nextRow--; break;
            case DOWN:  nextRow++; break;
        }

        return registry.getBuildingAt(nextCol, nextRow);
    }

    private Building getLeftBuilding() {
        int nextCol = getX();
        int nextRow = getY();

        switch (direction) {
            case RIGHT: nextRow++; break;
            case LEFT:  nextRow--; break;
            case UP:    nextCol--; break;
            case DOWN:  nextCol++; break;
        }

        return registry.getBuildingAt(nextCol, nextRow);
    }

    private Building getRightBuilding() {
        int nextCol = getX();
        int nextRow = getY();

        switch (direction) {
            case RIGHT: nextRow--; break;
            case LEFT:  nextRow++; break;
            case UP:    nextCol++; break;
            case DOWN:  nextCol--; break;
        }

        return registry.getBuildingAt(nextCol, nextRow);
    }

    private int dirToInt(Direction dir) {
        switch (dir) {
            case UP:    return 0;
            case RIGHT: return 1;
            case DOWN:  return 2;
            case LEFT:  return 3;
        }
        return 0;
    }

    private Direction getDirectionFrom(Building sender) {
        if (sender == null) return this.direction;

        if (registry.getBuildingAt(getX() - 1, getY()) == sender) return Direction.RIGHT;
        if (registry.getBuildingAt(getX() + 1, getY()) == sender) return Direction.LEFT;
        if (registry.getBuildingAt(getX(), getY() - 1) == sender) return Direction.UP;
        if (registry.getBuildingAt(getX(), getY() + 1) == sender) return Direction.DOWN;

        return this.direction;
    }

    private void updateType() {
        Building u = getUpperBuilding();
        Building l = getLeftBuilding();
        Building r = getRightBuilding();
        Building d = getDownBuilding();

        // type 3
        if (d instanceof ThroughItem dd && l instanceof ThroughItem ll && r instanceof ThroughItem rr) {
            if (ll.canThroughItemIn(new Point(getX(), getY())) && rr.canThroughItemIn(new Point(getX(), getY())) && dd.canThroughItemIn(new Point(getX(), getY()))) {
                beltType = 3;
                rotation = dirToInt(direction) * 90f;
                reflection = 1f;
                return;
            }
        }

        // type 2
        if (d instanceof ThroughItem dd && dd.canThroughItemIn(new Point(getX(), getY()))) {
            if (r instanceof ThroughItem rr) {
                if (rr.canThroughItemIn(new Point(getX(), getY()))) {
                    beltType = 2;
                    rotation = dirToInt(direction) * 90f;
                    reflection = 1f;
                    return;
                }
            }
            if (l instanceof ThroughItem ll) {
                if (ll.canThroughItemIn(new Point(getX(), getY()))) {
                    beltType = 2;
                    rotation = dirToInt(direction) * 90f;
                    reflection = -1f;
                    return;
                }
            }
        }

        // type 4
        if (l instanceof ThroughItem ll && r instanceof ThroughItem rr) {
            if (ll.canThroughItemIn(new Point(getX(), getY())) && rr.canThroughItemIn(new Point(getX(), getY()))) {
                beltType = 4;
                rotation = dirToInt(direction) * 90f;
                reflection = 1f;
                return;
            }
        }

        // type 1
        if (l instanceof ThroughItem ll) {
            if (ll.canThroughItemIn(new Point(getX(), getY()))) {
                beltType = 1;
                rotation = dirToInt(direction) * 90f;
                reflection = 1f;
                return;
            }
        }
        if (r instanceof ThroughItem rr) {
            if (rr.canThroughItemIn(new Point(getX(), getY()))) {
                beltType = 1;
                rotation = dirToInt(direction) * 90f;
                reflection = -1f;
                return;
            }
        }

        // type 0
        beltType = 0;
        rotation = dirToInt(direction) * 90f;
        reflection = 1f;
    }

    @Override
    public void update() {
        super.update();
        updateType();
        for (int i = 0; i < itemId.size(); i++) {
            float progressWill = itemProgress.get(i) + speed;

            if (i > 0) {
                float progressPrev = itemProgress.get(i - 1);
                if (progressPrev - ItemSize < progressWill) {
                    progressWill = progressPrev - ItemSize;
                }
            } else {
                Building nextBuilding = getUpperBuilding();
                if (nextBuilding instanceof Belt nextBelt) {
                    if (!nextBelt.itemProgress.isEmpty()) {
                        float tailProgress = nextBelt.itemProgress.getLast();
                        if (progressWill - 1.0f + ItemSize > tailProgress) {
                            progressWill = 1.0f + tailProgress - ItemSize;
                        }
                    }
                }
            }

            progressWill = Math.max(itemProgress.get(i), progressWill);

            if (progressWill >= 1.0f) {
                if (throughItem(itemId.get(i))) {
                    itemId.remove(i);
                    itemProgress.remove(i);
                    itemFrom.remove(i);
                    i--;
                    continue;
                } else {
                    progressWill = 1.0f;
                }
            }
            if (progressWill - itemProgress.get(i) <= speed / 1.5f) continue;
            itemProgress.set(i, progressWill);
        }
    }

    @Override
    public boolean throughItem(ItemType item) {
        Building nextBuilding = getUpperBuilding();
        if (nextBuilding instanceof ReceiveItem building) {
            return building.receiveItem(this, item);
        }
        return false;
    }

    @Override
    public boolean canThroughItemIn(Point point) {
        int nextCol = getX();
        int nextRow = getY();

        switch (direction) {
            case RIGHT: nextCol++; break;
            case LEFT:  nextCol--; break;
            case UP:    nextRow++; break;
            case DOWN:  nextRow--; break;
        }

        return (new Point(nextCol, nextRow)).equals(point);
    }

    @Override
    public boolean receiveItem(Building sender, ItemType id) {
        if (sender != null && sender == getUpperBuilding()) return false;

        Direction travelDir = getDirectionFrom(sender);
        float startProgress = 0.0f;

        if (itemId.isEmpty()) {
            itemId.add(id);
            itemProgress.add(startProgress);
            itemFrom.add(travelDir);
            return true;
        }

        int insertIdx = itemId.size();
        for (int i = 0; i < itemProgress.size(); i++) {
            if (startProgress > itemProgress.get(i)) {
                insertIdx = i;
                break;
            }
        }

        if (insertIdx > 0 && itemProgress.get(insertIdx - 1) - ItemSize < startProgress) return false;
        if (insertIdx < itemProgress.size() && startProgress - ItemSize < itemProgress.get(insertIdx)) return false;

        itemId.add(insertIdx, id);
        itemProgress.add(insertIdx, startProgress);
        itemFrom.add(insertIdx, travelDir);
        return true;
    }

    @Override
    public boolean canReceiveItemFrom(Building building, Point point) {
        return building != null && building != getUpperBuilding();
    }

    public ArrayList<ItemType> getItemId() { return this.itemId; }
    public ArrayList<Float> getItemProgress() { return this.itemProgress; }
    public ArrayList<Direction> getItemFrom() { return this.itemFrom; }

    public Integer getBeltType() {
        return beltType;
    }

    public Float getRotation() {
        return rotation;
    }

    public Float getReflection() {
        return reflection;
    }
}
