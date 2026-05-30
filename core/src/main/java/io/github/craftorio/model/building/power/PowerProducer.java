package io.github.craftorio.model.building.power;

public interface PowerProducer {
    float getPotentialOutput();

    void setLoadRatio(float ratio);
}
