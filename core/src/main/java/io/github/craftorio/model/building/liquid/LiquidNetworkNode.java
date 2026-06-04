package io.github.craftorio.model.building.liquid;

import io.github.craftorio.model.building.Direction;
import io.github.craftorio.model.building.ReceiveLiquid;
import io.github.craftorio.model.building.ThroughLiquid;
import io.github.craftorio.model.item.LiquidType;

public interface LiquidNetworkNode extends ReceiveLiquid, ThroughLiquid {
    int getSubNetworksCount();

    LiquidNetwork getNetwork(int index);
    void setNetwork(int index, LiquidNetwork network);

    float getPrevFill(int index);
    LiquidType getPrevLiquidType(int index);
    void savePrevFill();
    void setCurrentFill(int index, float fill);
    float getCapacity(int index);

    int getIndexForDirection(Direction dir);

    LiquidNetworkNode getLinkedNode(int index);
}
