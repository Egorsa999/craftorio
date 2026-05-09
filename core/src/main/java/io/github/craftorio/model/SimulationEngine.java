package io.github.craftorio.model;

import io.github.craftorio.model.building.Belt;
import io.github.craftorio.model.building.Building;

public class SimulationEngine {
    private final BuildingRegistry registry;

    public SimulationEngine(BuildingRegistry registry) {
        this.registry = registry;
    }

    public void update() {
        registry.applyPendingChanges();
        for (Building building : registry.getBuildingsForTick()) {
            building.update();
        }
    }
}
