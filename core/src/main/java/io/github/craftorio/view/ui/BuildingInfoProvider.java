package io.github.craftorio.view.ui;

import io.github.craftorio.model.building.BuildingType;

public class BuildingInfoProvider {


    public static String getDetailedText(BuildingType type) {
        StringBuilder sb = new StringBuilder();

        // 1. Уникальное описание
        sb.append(getDescription(type)).append("\n\n");

        // 2. Характеристики здания
        sb.append("Size: ").append(type.getWidth()).append("x").append(type.getHeight()).append("\n");
        sb.append("Max HP: ").append(type.getMaxHP()).append("\n");

        if (type.getWalkable()) {
            sb.append("Walkable: Yes\n");
        } else {
            sb.append("Walkable: No\n");
        }

        return sb.toString();
    }

    private static String getDescription(BuildingType type) {
        switch (type) {
            case BELT: return "Transports items efficiently across the factory floor.";
            case MINER: return "Extracts solid resources from the ground directly below it.";
            case HORIZONTAL_MINER: return "Is capable of mining stone directly from walls. Must be placed with the drill facing the wall to operate.";
            case CORE: return "The heart of your base. Protect it at all costs.";
            case JUNCTION: return "Allows two conveyor belts to cross paths without mixing items.";
            case ROUTER: return "Distributes incoming items evenly across multiple outputs.";
            case ASSEMBLER: return "Automatically crafts advanced items from basic components.";
            case TURRET: return "Defends your factory by shooting hostile entities.";
            case WALL: return "A sturdy defensive structure to block enemy movement.";
            case PIPE: return "Transports liquids across the factory.";
            case PUMP: return "Moves liquids through pipes, ensuring flow pressure.";
            case COAL_POWER_GENERATOR: return "Accepts coal and burns it for 5 seconds, generating 100W of power during the process.";
            case POWER_POLE: return "Connects buildings to the power grid.";
            case FURNACE: return "Smelts raw ores into usable plates and materials.";
            case CHEMICAL_PLANT: return "Processes liquids into advanced chemical products.";
            default: return "A standard factory structure.";
        }
    }
}
