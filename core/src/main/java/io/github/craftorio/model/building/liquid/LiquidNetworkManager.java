package io.github.craftorio.model.building.liquid;

import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.item.LiquidType;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class LiquidNetworkManager {
    private final List<LiquidNetwork> networks = new ArrayList<>();
    private final BuildingRegistry registry;

    public LiquidNetworkManager(BuildingRegistry registry) {
        this.registry = registry;
    }

    public List<LiquidNetwork> getNetworks() {
        return networks;
    }

    private Direction getOpposite(Direction dir) {
        return switch (dir) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }

    public void rebuild(List<LiquidNetworkNode> allNodes) {
        for (LiquidNetworkNode node : allNodes) {
            node.savePrevFill();
            for (int i = 0; i < node.getSubNetworksCount(); i++) {
                node.setNetwork(i, null);
            }
        }

        networks.clear();
        Set<String> visited = new HashSet<>();

        for (LiquidNetworkNode start : allNodes) {
            for (int i = 0; i < start.getSubNetworksCount(); i++) {
                String startKey = start.hashCode() + "_" + i;
                if (visited.contains(startKey)) continue;

                LiquidNetwork network = new LiquidNetwork();
                Queue<LiquidNetwork.NodeEntry> queue = new LinkedList<>();
                queue.add(new LiquidNetwork.NodeEntry(start, i));
                visited.add(startKey);

                LiquidType currLiquid = null;

                while (!queue.isEmpty()) {
                    LiquidNetwork.NodeEntry current = queue.poll();
                    network.addMember(current.node, current.index);

                    if (current.node.getPrevLiquidType(current.index) != null) {
                        currLiquid = current.node.getPrevLiquidType(current.index);
                    }

                    Building currentBuilding = (Building) current.node;
                    Point currentPos = new Point(currentBuilding.getX(), currentBuilding.getY());

                    for (Direction side : Direction.values()) {
                        if (current.node.getIndexForDirection(side) != current.index) continue;

                        int nx = currentBuilding.getX();
                        int ny = currentBuilding.getY();

                        switch (side) {
                            case RIGHT -> nx++;
                            case LEFT -> nx--;
                            case UP -> ny++;
                            case DOWN -> ny--;
                        }

                        Building neighborBuilding = registry.getBuildingAt(nx, ny);
                        if (!(neighborBuilding instanceof LiquidNetworkNode neighbor)) continue;

                        int nIndex = neighbor.getIndexForDirection(getOpposite(side));
                        if (nIndex == -1) continue;

                        Point neighborPos = new Point(nx, ny);

                        boolean connected =
                            (current.node.canThroughLiquidIn(neighborPos) && neighbor.canReceiveLiquidFrom(currentBuilding, currentPos, current.node.getPrevLiquidType(current.index)))
                                || (neighbor.canThroughLiquidIn(currentPos) && current.node.canReceiveLiquidFrom(neighborBuilding, neighborPos, neighbor.getPrevLiquidType(nIndex)));

                        if (!connected) continue;

                        String nKey = neighbor.hashCode() + "_" + nIndex;
                        if (visited.contains(nKey)) continue;

                        LiquidType nt = neighbor.getPrevLiquidType(nIndex);
                        if (nt != null && nt != currLiquid && currLiquid != null) continue;

                        visited.add(nKey);
                        queue.add(new LiquidNetwork.NodeEntry(neighbor, nIndex));
                    }

                    LiquidNetworkNode linked = current.node.getLinkedNode(current.index);
                    if (linked != null) {
                        int lIndex = 0;
                        String lKey = linked.hashCode() + "_" + lIndex;
                        if (!visited.contains(lKey)) {
                            LiquidType lt = linked.getPrevLiquidType(lIndex);
                            if (!(lt != null && lt != currLiquid && currLiquid != null)) {
                                visited.add(lKey);
                                queue.add(new LiquidNetwork.NodeEntry(linked, lIndex));
                            }
                        }
                    }
                }

                network.initFromPrevFill();
                networks.add(network);
            }
        }
    }

    public void tick() {
        for (LiquidNetwork network : networks) {
            network.tick();
        }
    }
}
