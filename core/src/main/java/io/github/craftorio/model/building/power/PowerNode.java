package io.github.craftorio.model.building.power;

import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.core.BuildingRegistry;

import java.util.*;

public class PowerNode {
    PowerNetwork network;
    private final Set<PowerNode> connections = new HashSet<>();

    private Building owner;
    private BuildingRegistry registry;

    public PowerNode(Building owner, BuildingRegistry registry) {
        this.owner = owner;
        this.registry = registry;

        this.network = new PowerNetwork();
        this.network.addComponent(this);
    }

    private void connectTo(PowerNode other) {
        if (this.connections.contains(other)) return;

        this.connections.add(other);
        other.connections.add(this);

        PowerNetwork.merge(this.network, other.network);
    }

    public void connect() {
        double radius = 5.0;
        double radiusSq = radius * radius;

        boolean isNewPole = owner instanceof PowerPole;

        PowerConnectable ownerConnectable = (PowerConnectable) owner;
        Map<PowerNetwork, PowerConnectable> bestPerNetwork = new HashMap<>();
        Map<PowerNetwork, Double> bestDistancesSq = new HashMap<>();

        for (Building building : registry.getBuildingsForTick()) {
            if (building == owner) continue;

            if (building instanceof PowerConnectable targetConnectable) {

                if (!isNewPole && !(building instanceof PowerPole)) {
                    continue;
                }

                double dx = building.getCenterX() - owner.getCenterX();
                double dy = building.getCenterY() - owner.getCenterY();
                double distSq = dx * dx + dy * dy;

                if (distSq <= radiusSq) {
                    PowerNetwork targetNetwork = targetConnectable.getPowerNode().getNetwork();

                    if (targetNetwork == null || targetNetwork == this.network) continue;

                    boolean isTargetPole = targetConnectable instanceof PowerPole;
                    boolean hasExisting = bestPerNetwork.containsKey(targetNetwork);
                    boolean shouldReplace = false;

                    if (!hasExisting) {
                        shouldReplace = true;
                    } else {
                        boolean isExistingPole = bestPerNetwork.get(targetNetwork) instanceof PowerPole;

                        if (isTargetPole && !isExistingPole) {
                            shouldReplace = true;
                        } else if (isTargetPole == isExistingPole) {
                            if (distSq < bestDistancesSq.get(targetNetwork)) {
                                shouldReplace = true;
                            }
                        }
                    }

                    if (shouldReplace) {
                        bestDistancesSq.put(targetNetwork, distSq);
                        bestPerNetwork.put(targetNetwork, targetConnectable);
                    }
                }
            }
        }

        for (PowerConnectable target : bestPerNetwork.values()) {
            ownerConnectable.getPowerNode().connectTo(target.getPowerNode());
        }
    }

    public void disconnect() {
        Set<PowerNode> neighbors = new HashSet<>(this.connections);

        for (PowerNode neighbor : neighbors) {
            neighbor.connections.remove(this);
            this.connections.remove(neighbor);
        }
        this.network.removeComponent(this);

        recalculateNetworks(neighbors);

        for (PowerNode neighbor : neighbors) {
            neighbor.connect();
        }
    }

    private void recalculateNetworks(Set<PowerNode> startingNodes) {
        Set<PowerNode> visitedGlobally = new HashSet<>();

        for (PowerNode startNode : startingNodes) {
            if (visitedGlobally.contains(startNode)) {
                continue;
            }

            Set<PowerNode> connectedComponent = new HashSet<>();
            Queue<PowerNode> queue = new LinkedList<>();

            queue.add(startNode);
            visitedGlobally.add(startNode);
            connectedComponent.add(startNode);

            while (!queue.isEmpty()) {
                PowerNode current = queue.poll();

                for (PowerNode neighbor : current.getConnections()) {
                    if (!visitedGlobally.contains(neighbor)) {
                        visitedGlobally.add(neighbor);
                        connectedComponent.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }

            PowerNetwork newNetwork = new PowerNetwork();
            for (PowerNode node : connectedComponent) {
                node.getNetwork().removeComponent(node);
                newNetwork.addComponent(node);
            }
        }
    }

    public Building getOwner() {
        return owner;
    }

    public Set<PowerNode> getConnections() {
        return Collections.unmodifiableSet(connections);
    }

    public void setNetwork(PowerNetwork network) {
        this.network = network;

        if (network == null) {
            if (owner instanceof PowerProducer p) p.setLoadRatio(0f);
            if (owner instanceof PowerConsumer c) c.setSatisfactionRatio(0f);
        }
    }

    public PowerNetwork getNetwork() {
        return network;
    }
}
