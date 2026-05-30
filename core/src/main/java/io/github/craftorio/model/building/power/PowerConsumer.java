package io.github.craftorio.model.building.power;

public interface PowerConsumer {
    float getRequiredPower();

    void setSatisfactionRatio(float ratio);
}
