package io.github.craftorio.model.building.power;

import io.github.craftorio.model.building.Building;

import java.util.HashSet;
import java.util.Set;

public class PowerNetwork {
    private final Set<PowerNode> components = new HashSet<>();

    private final Set<PowerConsumer> consumers = new HashSet<>();
    private final Set<PowerProducer> producers = new HashSet<>();
    private final Set<Battery> batteries = new HashSet<>();

    private PowerStatus currentStatus = PowerStatus.IDLE;

    public PowerStatus getStatus() {
        return currentStatus;
    }

    public void addComponent(PowerNode component) {
        components.add(component);
        component.setNetwork(this);

        Building owner = component.getOwner();
        if (owner instanceof PowerConsumer c) consumers.add(c);
        if (owner instanceof PowerProducer p) producers.add(p);
        if (owner instanceof Battery b) batteries.add(b);
    }

    public void removeComponent(PowerNode component) {
        components.remove(component);
        component.setNetwork(null);

        Building owner = component.getOwner();

        if (owner instanceof PowerConsumer c) {
            consumers.remove(c);
            c.setSatisfactionRatio(0f);
        }

        if (owner instanceof PowerProducer p) {
            producers.remove(p);
            p.setLoadRatio(0f);
        }

        if (owner instanceof Battery b) {
            batteries.remove(b);
        }
    }

    public void update() {
        if (components.isEmpty()) return;

        float totalDemand = 0f;
        for (PowerConsumer c : consumers) totalDemand += c.getRequiredPower();

        float totalSupply = 0f;
        for (PowerProducer p : producers) totalSupply += p.getPotentialOutput();

        float totalBatteryChargeSpace = 0f;
        float totalBatteryDischargeAvailable = 0f;

        for (Battery b : batteries) {
            float space = b.getCapacity() - b.getEnergyStored();
            totalBatteryChargeSpace += Math.min(space, b.getMaxChargeRate());
            totalBatteryDischargeAvailable += Math.min(b.getEnergyStored(), b.getMaxDischargeRate());
        }

        float satisfactionRatio = 1.0f;
        float productionRatio = 1.0f;

        float netEnergy = totalSupply - totalDemand;



        if (netEnergy >= 0) {
            satisfactionRatio = 1.0f;

            if (netEnergy >= totalBatteryChargeSpace) {
                float actualNeeded = totalDemand + totalBatteryChargeSpace;
                productionRatio = totalSupply > 0 ? actualNeeded / totalSupply : 0f;
                chargeBatteries(totalBatteryChargeSpace, totalBatteryChargeSpace);
            } else {
                productionRatio = 1.0f;
                chargeBatteries(netEnergy, totalBatteryChargeSpace);
            }
            dischargeBatteries(0f, 1f);

        } else {
            productionRatio = 1.0f;
            float deficit = -netEnergy;

            if (totalBatteryDischargeAvailable >= deficit) {
                satisfactionRatio = 1.0f;
                dischargeBatteries(deficit, totalBatteryDischargeAvailable);
            } else {
                float totalAvailable = totalSupply + totalBatteryDischargeAvailable;
                satisfactionRatio = totalDemand > 0 ? totalAvailable / totalDemand : 0f;
                dischargeBatteries(totalBatteryDischargeAvailable, totalBatteryDischargeAvailable);
            }
            chargeBatteries(0f, 1f);
        }


        boolean noDemand = totalDemand <= 0.001f;


        boolean noActiveCharging = Math.min(totalSupply, totalBatteryChargeSpace) <= 0.001f;
        if (noDemand && noActiveCharging) {
            currentStatus = PowerStatus.IDLE;
        } else if (satisfactionRatio >= 0.999f) {
            currentStatus = PowerStatus.POWERED;
        } else if (satisfactionRatio > 0.001f) {
            currentStatus = PowerStatus.DEFICIT;
        } else {
            currentStatus = PowerStatus.BLACKOUT;
        }

        for (PowerConsumer c : consumers) c.setSatisfactionRatio(satisfactionRatio);
        for (PowerProducer p : producers) p.setLoadRatio(productionRatio);
    }

    private void chargeBatteries(float energyToDistribute, float totalCapacityInTick) {
        if (totalCapacityInTick <= 0 || energyToDistribute <= 0) return;

        float ratio = energyToDistribute / totalCapacityInTick;
        for (Battery b : batteries) {
            float space = b.getCapacity() - b.getEnergyStored();
            float amountToGive = Math.min(space, b.getMaxChargeRate()) * ratio;
            b.charge(amountToGive);
        }
    }

    private void dischargeBatteries(float energyToExtract, float totalAvailableInTick) {
        if (totalAvailableInTick <= 0 || energyToExtract <= 0) return;

        float ratio = energyToExtract / totalAvailableInTick;
        for (Battery b : batteries) {
            float amountToTake = Math.min(b.getEnergyStored(), b.getMaxDischargeRate()) * ratio;
            b.discharge(amountToTake);
        }
    }

    public Set<PowerNode> getComponents() {
        return components;
    }

    public static PowerNetwork merge(PowerNetwork net1, PowerNetwork net2) {
        if (net1 == net2) {
            return net1;
        }

        PowerNetwork larger = net1.components.size() >= net2.components.size() ? net1 : net2;
        PowerNetwork smaller = net1.components.size() < net2.components.size() ? net1 : net2;

        for (PowerNode comp : new HashSet<>(smaller.getComponents())) {
            smaller.removeComponent(comp);
            larger.addComponent(comp);
        }

        return larger;
    }
}
