package io.github.craftorio.model.enemy;

import com.badlogic.gdx.utils.Pool;
import io.github.craftorio.model.BuildingRegistry;
import io.github.craftorio.model.WorldMap;

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

    private final FlowData bufferA;
    private final FlowData bufferB;
    private volatile FlowData activeData;

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

        this.bufferA = new FlowData(width, height);
        this.bufferB = new FlowData(width, height);
        this.activeData = bufferA;
    }

    public synchronized void updateFlowField() {
        FlowData workData = (activeData == bufferA) ? bufferB : bufferA;

        calculateIntegrationField(workData);
        calculateVectorField(workData);

        activeData = workData;
    }

    public Point getFlowDirection(int x, int y) {
        FlowData current = activeData;
        if (x >= 0 && x < current.width && y >= 0 && y < current.height) {
            return new Point(current.flowX[x][y], current.flowY[x][y]);
        } else {
            return new Point(0, 0);
        }
    }

    private void calculateIntegrationField(FlowData data) {
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

                    if (!worldMap.getCell(nx, ny).getTerrainType().getWalkability()) {
                        continue;
                    }

                    int moveCost = COST[i];

                    if (registry.getBuildingAt(nx, ny) != null) {
                        moveCost += BUILDING_PENALTY;
                    }

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

    private void calculateVectorField(FlowData data) {
        int w = data.width;
        int h = data.height;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if ((x == coreX && y == coreY) || data.distances[x][y] == Integer.MAX_VALUE) {
                    data.flowX[x][y] = 0;
                    data.flowY[x][y] = 0;
                    continue;
                }

                int minDistance = data.distances[x][y];
                byte bestDx = 0;
                byte bestDy = 0;

                for (int i = 0; i < 8; i++) {
                    int nx = x + DX[i];
                    int ny = y + DY[i];

                    if (nx >= 0 && nx < w && ny >= 0 && ny < h) {
                        if (data.distances[nx][ny] < minDistance) {
                            minDistance = data.distances[nx][ny];
                            bestDx = (byte) DX[i];
                            bestDy = (byte) DY[i];
                        }
                    }
                }

                data.flowX[x][y] = bestDx;
                data.flowY[x][y] = bestDy;
            }
        }
    }
}
