package io.github.craftorio.model.building.liquid;

import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.building.logistics.Pipe;
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

    public void rebuild(List<Pipe> allPipes) {
        for (Pipe pipe : allPipes) {
            pipe.savePrevFill();
            pipe.setNetwork(null);
        }

        networks.clear();

        Set<Pipe> visited = new HashSet<>();
        for (Pipe start : allPipes) {
            if (visited.contains(start)) {
                continue;
            }

            LiquidNetwork network = new LiquidNetwork();
            Queue<Pipe> queue = new LinkedList<>();
            queue.add(start);
            visited.add(start);

            LiquidType currLiquid = null;

            while (!queue.isEmpty()) {
                Pipe current = queue.poll();
                network.addMember(current);
                if (current.getPrevLiquidType() != null) {
                    currLiquid = current.getPrevLiquidType();
                }

                Point currentPos = new Point(current.getX(), current.getY());

                for (Direction side : Direction.values()) {
                    int nx = current.getX();
                    int ny = current.getY();

                    switch (side) {
                        case RIGHT -> nx++;
                        case LEFT -> nx--;
                        case UP -> ny++;
                        case DOWN -> ny--;
                    }

                    Building building = registry.getBuildingAt(nx, ny);
                    if (!(building instanceof Pipe neighbor)) {
                        continue;
                    }
                    if (visited.contains(neighbor)) {
                        continue;
                    }

                    Point neighborPos = new Point(nx, ny);
                    System.out.println(neighborPos + " " + current.getPrevLiquidType() + " " + neighbor.getPrevLiquidType());
                    boolean connected =
                        (current.canThroughLiquidIn(neighborPos)
                            && neighbor.canReceiveLiquidFrom(current, currentPos, current.getPrevLiquidType()))
                            || (neighbor.canThroughLiquidIn(currentPos)
                            && current.canReceiveLiquidFrom(neighbor, neighborPos, neighbor.getPrevLiquidType()));

                    if (connected && !(neighbor.getPrevLiquidType() != null && neighbor.getPrevLiquidType() != currLiquid)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }

            network.initFromPrevFill();
            networks.add(network);
        }
    }

    public void tick() {
        for (LiquidNetwork network : networks) {
            network.tick();
        }
    }
}
