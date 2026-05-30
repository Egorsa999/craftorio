package io.github.craftorio.model.building.power;

public interface Battery {
    float getEnergyStored();
    float getCapacity();

    float getMaxChargeRate();

    float getMaxDischargeRate();

    float charge(float amount);

    float discharge(float requestedAmount);
}
