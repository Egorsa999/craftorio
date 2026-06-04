package io.github.craftorio.view.ui;

import io.github.craftorio.model.building.BuildingType;

public class BuildingInfoProvider {

    // Сделали метод public, чтобы забирать только текст описания
    public static String getDescription(BuildingType type) {
        switch (type) {
            case BELT: return "Transports items efficiently across the factory floor.";
            case MINER: return "Extracts solid resources from the ground directly below it.";
            case HORIZONTAL_MINER: return "Is capable of mining stone directly from walls. Must be placed with the drill facing the wall to operate.";
            case CORE: return "The heart of your base. Protect it at all costs.";
            case JUNCTION: return "Allows two conveyor belts to cross paths without mixing items.";
            case ROUTER: return "Distributes incoming items evenly across multiple outputs.";
            case UNDERGROUND_BELT: return "Moves items beneath obstacles over a set distance, keeping your factory floor organized.";
            case ASSEMBLER: return "Automatically crafts advanced items from basic components.";
            case TURRET: return "Defends your factory by shooting hostile entities.";
            case LASER_TURRET: return "Fires high-energy beams at hostile entities. Requires a continuous power supply instead of ammunition.";
            case WALL: return "A sturdy defensive structure to block enemy movement.";
            case PIPE: return "Transports liquids across the factory.";
            case LIQUID_JUNCTION: return "Allows two liquid pipes to cross paths without mixing their contents.";
            case LIQUID_ROUTER: return "Distributes incoming liquids evenly across multiple connected output pipes.";
            case UNDERGROUND_PIPE: return "Transports liquids beneath obstacles, perfect for crossing existing pipelines or belts.";
            case PUMP: return "Moves liquids through pipes, ensuring flow pressure.";
            case COAL_POWER_GENERATOR: return "Generates power from Coal.";
            case POWER_POLE: return "Connects buildings to the power grid.";
            case FURNACE: return "Smelts raw ores into usable plates and materials.";
            case CHEMICAL_PLANT: return "Processes liquids into advanced chemical products.";
            case OIL_GENERATOR: return "Generates power by burning Oil. Requires a steady liquid flow.";
            default: return "A standard factory structure.";
        }
    }
}
