package io.github.craftorio.model.enemy;

import com.badlogic.gdx.utils.Pool;
import io.github.craftorio.model.core.BuildingRegistry;
import io.github.craftorio.model.core.WorldMap;
import io.github.craftorio.model.building.Building;
import io.github.craftorio.model.building.DamageableBuilding;

import java.awt.*;
import java.util.PriorityQueue;

public class PathFinder {
    private int coreX, coreY;
    private WorldMap worldMap;
    private BuildingRegistry registry;

    private static final int COST_ORTHOGONAL = 10;
    private static final int COST_DIAGONAL = 14;
    private static final int BUILDING_PENALTY = 500;

    private static final int[] DX = {-1, 0, 1, -1, 1, -1, 0, 1};
    private static final int[] DY = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] COST = {
        COST_DIAGONAL, COST_ORTHOGONAL, COST_DIAGONAL,
        COST_ORTHOGONAL, COST_ORTHOGONAL,
        COST_DIAGONAL, COST_ORTHOGONAL, COST_DIAGONAL
    };

    public enum SizeClass {
        SMALL,
        HEAVY
    }

    public static class FlowData {
        public final int width, height;
        public final int[][] distances;
        public final byte[][] flowX;
        public final byte[][] flowY;

        public FlowData(int width, int height) {
            this.width = width;
            this.height = height;
            this.distances = new int[width][height];
            this.flowX = new byte[width][height];
            this.flowY = new byte[width][height];
        }
    }

    public static class LayeredFlowData {
        public final FlowData small;
        public final FlowData heavy;

        public LayeredFlowData(int width, int height) {
            this.small = new FlowData(width, height);
            this.heavy = new FlowData(width, height);
        }
    }

    private final LayeredFlowData bufferA;
    private final LayeredFlowData bufferB;
    private volatile LayeredFlowData activeData;

    private final Pool<Node> nodePool = new Pool<Node>(11000, 15000) {
        @Override
        protected Node newObject() {
            return new Node();
        }
    };

    private static class Node implements Comparable<Node> {
        int x, y, cost;

        public Node set(int x, int y, int cost) {
            this.x = x; this.y = y; this.cost = cost;
            return this;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.cost, other.cost);
        }
    }

    public PathFinder(int coreX, int coreY, WorldMap worldMap, BuildingRegistry registry) {
        this.coreX = coreX;
        this.coreY = coreY;
        this.worldMap = worldMap;
        this.registry = registry;

        int width = worldMap.getWidth();
        int height = worldMap.getHeight();

        this.bufferA = new LayeredFlowData(width, height);
        this.bufferB = new LayeredFlowData(width, height);
        this.activeData = bufferA;
    }

    public synchronized void updateFlowField() {
        LayeredFlowData workData = (activeData == bufferA) ? bufferB : bufferA;

        calculateIntegrationField(workData.small, SizeClass.SMALL);
        calculateVectorField(workData.small, SizeClass.SMALL);

        calculateIntegrationField(workData.heavy, SizeClass.HEAVY);
        calculateVectorField(workData.heavy, SizeClass.HEAVY);

        activeData = workData;
    }

    public Point getFlowDirection(int x, int y, SizeClass sizeClass) {
        LayeredFlowData current = activeData;
        FlowData layer = (sizeClass == SizeClass.HEAVY) ? current.heavy : current.small;

        if (x >= 0 && x < layer.width && y >= 0 && y < layer.height) {
            return new Point(layer.flowX[x][y], layer.flowY[x][y]);
        } else {
            return new Point(0, 0);
        }
    }

    private void calculateIntegrationField(FlowData data, SizeClass size) {
        int w = data.width;
        int h = data.height;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                data.distances[x][y] = Integer.MAX_VALUE;
            }
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();

        data.distances[coreX][coreY] = 0;
        openSet.add(nodePool.obtain().set(coreX, coreY, 0));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.cost > data.distances[current.x][current.y]) {
                nodePool.free(current);
                continue;
            }

            for (int i = 0; i < 8; i++) {
                int nx = current.x + DX[i];
                int ny = current.y + DY[i];

                if (nx >= 0 && nx < w && ny >= 0 && ny < h) {

                    int cellPenalty = getCellPenalty(nx, ny, size);

                    if (cellPenalty == -1) {
                        continue;
                    }

                    int moveCost = COST[i] + cellPenalty;
                    int newCost = current.cost + moveCost;

                    if (newCost < data.distances[nx][ny]) {
                        data.distances[nx][ny] = newCost;
                        openSet.add(nodePool.obtain().set(nx, ny, newCost));
                    }
                }
            }
            nodePool.free(current);
        }
    }

    private void calculateVectorField(FlowData data, SizeClass size) {
        int w = data.width;
        int h = data.height;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if ((x == coreX && y == coreY) || data.distances[x][y] == Integer.MAX_VALUE) {
                    data.flowX[x][y] = 0;
                    data.flowY[x][y] = 0;
                    continue;
                }

                int h1 = x * 0x85ebca6b ^ y * 0xc2b2ae35;
                h1 ^= h1 >>> 13;
                h1 *= 0xc2b2ae35;
                h1 ^= h1 >>> 16;
                int cellHash = Math.abs(h1);

                int startIndex = cellHash % 8;
                int currentDist = data.distances[x][y];

                int minPerceivedDist = Integer.MAX_VALUE;
                byte bestDx = 0;
                byte bestDy = 0;

                for (int i = 0; i < 8; i++) {
                    int dirIndex = (startIndex + i) % 8;
                    int nx = x + DX[dirIndex];
                    int ny = y + DY[dirIndex];

                    if (nx >= 0 && nx < w && ny >= 0 && ny < h) {

                        if (DX[dirIndex] != 0 && DY[dirIndex] != 0) {
                            if (getCellPenalty(x + DX[dirIndex], y, size) == -1 ||
                                getCellPenalty(x, y + DY[dirIndex], size) == -1) {
                                continue;
                            }
                        }

                        int neighborDist = data.distances[nx][ny];

                        if (neighborDist < currentDist) {
                            int dirHash = Math.abs(cellHash ^ (dirIndex * 0x9E3779B9));
                            int noise = dirHash % 24;
                            int perceivedDist = neighborDist + noise;

                            if (perceivedDist < minPerceivedDist) {
                                minPerceivedDist = perceivedDist;
                                bestDx = (byte) DX[dirIndex];
                                bestDy = (byte) DY[dirIndex];
                            }
                        }
                    }
                }

                data.flowX[x][y] = bestDx;
                data.flowY[x][y] = bestDy;
            }
        }
    }

    private int getCellPenalty(int targetX, int targetY, SizeClass size) {
        int padding = (size == SizeClass.HEAVY) ? 1 : 0;
        int maxPenalty = 0;

        for (int dx = -padding; dx <= padding; dx++) {
            for (int dy = -padding; dy <= padding; dy++) {
                int cx = targetX + dx;
                int cy = targetY + dy;

                if (cx < 0 || cx >= worldMap.getWidth() || cy < 0 || cy >= worldMap.getHeight()) {
                    return -1;
                }

                if (!worldMap.getCell(cx, cy).getTerrainType().getWalkability()) {
                    return -1;
                }

                Building building = registry.getBuildingAt(cx, cy);
                if (building != null) {
                    if (building instanceof DamageableBuilding dBuilding) {
                        maxPenalty = Math.max(maxPenalty, dBuilding.getHP());
                    } else {
                        maxPenalty = Math.max(maxPenalty, BUILDING_PENALTY);
                    }
                }
            }
        }

        return maxPenalty;
    }
}
