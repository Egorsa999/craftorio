package io.github.craftorio.model.building;

import io.github.craftorio.GameConfig;
import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.ItemType;

import java.awt.*;
import java.util.ArrayList;

public class Belt extends DamageableBuilding implements ThroughItem, ReceiveItem {
    private static final float speed = 1f * GameConfig.TICK_TIME;
    private static final float ItemSize = 1f / 3f;
    private static float animationOffset = 0.0f;

    private ArrayList<ItemType> itemId;
    private ArrayList<Float> itemProgress;
    private ArrayList<Direction> itemFrom;

    public int beltType;
    public float rotation;
    public float reflection;

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

    private Direction getRelativeDir(Direction myDir, Direction otherDir) {
        int myAngle = dirToInt(myDir);
        int otherAngle = dirToInt(otherDir);

        int diff = (otherAngle - myAngle + 4) % 4;

        switch (diff) {
            case 0: return Direction.UP;
            case 1: return Direction.RIGHT;
            case 2: return Direction.DOWN;
            case 3: return Direction.LEFT;
        }
        return Direction.UP;
    }

    private void updateType() {
        Building u = getUpperBuilding();
        Building l = getLeftBuilding();
        Building r = getRightBuilding();
        Building d = getDownBuilding();

        // type 3
        if (d instanceof ThroughItem dd && l instanceof ThroughItem ll && r instanceof ThroughItem rr) {
            if (ll.canThroughIn(new Point(getX(), getY())) && rr.canThroughIn(new Point(getX(), getY())) && dd.canThroughIn(new Point(getX(), getY()))) {
                beltType = 3;
                rotation = dirToInt(direction) * 90f;
                reflection = 1f;
                return;
            }
        }

        // type 2
        if (d instanceof ThroughItem dd && dd.canThroughIn(new Point(getX(), getY()))) {
            if (r instanceof ThroughItem rr) {
                if (rr.canThroughIn(new Point(getX(), getY()))) {
                    beltType = 2;
                    rotation = dirToInt(direction) * 90f;
                    reflection = 1f;
                    return;
                }
            }
            if (l instanceof ThroughItem ll) {
                if (ll.canThroughIn(new Point(getX(), getY()))) {
                    beltType = 2;
                    rotation = dirToInt(direction) * 90f;
                    reflection = -1f;
                    return;
                }
            }
        }

        // type 4
        if (l instanceof ThroughItem ll && r instanceof ThroughItem rr) {
            if (ll.canThroughIn(new Point(getX(), getY())) && rr.canThroughIn(new Point(getX(), getY()))) {
                beltType = 4;
                rotation = dirToInt(direction) * 90f;
                reflection = 1f;
                return;
            }
        }

        // type 1
        if (l instanceof ThroughItem ll) {
            if (ll.canThroughIn(new Point(getX(), getY()))) {
                beltType = 1;
                rotation = dirToInt(direction) * 90f;
                reflection = 1f;
                return;
            }
        }
        if (r instanceof ThroughItem rr) {
            if (rr.canThroughIn(new Point(getX(), getY()))) {
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
                if (throughItem(i, itemId.get(i), progressWill - 1.0f)) {
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

    public boolean throughItem(int index, ItemType id, Float progress) {
        Building nextBuilding = getUpperBuilding();
        if (nextBuilding instanceof Belt nextBelt) {
            return nextBelt.receiveItem(id, progress, this.direction);
        } else if (nextBuilding instanceof Junction nextJunc) {
            return nextJunc.receiveItem(id, progress, this.direction);
        } else if (nextBuilding instanceof Router nextRouter) {
            return nextRouter.receiveItem(id, progress, this.direction);
        } else if (nextBuilding instanceof ReceiveItem building) {
            return building.receiveItem(id, progress);
        }
        return false;
    }

    @Override
    public boolean throughItem(ItemType id, Float progress) { return false; }

    @Override
    public boolean canThroughIn(Point point) {
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
    public boolean receiveItem(ItemType id, Float progress) {
        return receiveItem(id, progress, this.direction);
    }

    @Override
    public boolean canReceiveFrom(Point point) {
        return !(registry.getBuildingAt(point).equals(getUpperBuilding()));
    }

    public boolean receiveItem(ItemType id, Float progress, Direction from) {
        if (itemId.isEmpty()) {
            itemId.add(id);
            itemProgress.add(progress);
            itemFrom.add(from);
            return true;
        }

        int insertIdx = itemId.size();
        for (int i = 0; i < itemProgress.size(); i++) {
            if (progress > itemProgress.get(i)) {
                insertIdx = i;
                break;
            }
        }

        if (insertIdx > 0 && itemProgress.get(insertIdx - 1) - ItemSize < progress) return false;
        if (insertIdx < itemProgress.size() && progress - ItemSize < itemProgress.get(insertIdx)) return false;

        itemId.add(insertIdx, id);
        itemProgress.add(insertIdx, progress);
        itemFrom.add(insertIdx, from);
        return true;
    }

    public ArrayList<ItemType> getItemId() { return this.itemId; }
    public ArrayList<Float> getItemProgress() { return this.itemProgress; }
    public ArrayList<Direction> getItemFrom() { return this.itemFrom; }
}
