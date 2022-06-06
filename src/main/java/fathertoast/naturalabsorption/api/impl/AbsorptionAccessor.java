package fathertoast.naturalabsorption.api.impl;

import fathertoast.naturalabsorption.api.IAbsorptionAccessor;
import fathertoast.naturalabsorption.common.health.AbsorptionHelper;
import net.minecraft.entity.player.PlayerEntity;

public class AbsorptionAccessor implements IAbsorptionAccessor {

    @Override
    public void setNaturalAbsorption(PlayerEntity player, boolean updateActualAbsorption, double value) {
        AbsorptionHelper.setNaturalAbsorption(player, updateActualAbsorption, value);
    }

    @Override
    public void addNaturalAbsorption(PlayerEntity player, boolean updateActualAbsorption, double amount) {
        AbsorptionHelper.addNaturalAbsorption(player, updateActualAbsorption, amount);
    }

    @Override
    public void removeNaturalAbsorption(PlayerEntity player, double amount) {
        AbsorptionHelper.removeNaturalAbsorption(player, amount);
    }

    @Override
    public double getNaturalAbsorption(PlayerEntity player) {
        return AbsorptionHelper.getNaturalAbsorption(player);
    }

    @Override
    public void setEquipmentAbsorption(PlayerEntity player, boolean updateActualAbsorption, double value) {
        AbsorptionHelper.setEquipmentAbsorption(player, updateActualAbsorption, value);
    }

    @Override
    public void addEquipmentAbsorption(PlayerEntity player, boolean updateActualAbsorption, double amount) {
        AbsorptionHelper.addEquipmentAbsorption(player, updateActualAbsorption, amount);
    }

    @Override
    public void removeEquipmentAbsorption(PlayerEntity player, double amount) {
        AbsorptionHelper.removeEquipmentAbsorption(player, amount);
    }

    @Override
    public double getEquipmentAbsorption(PlayerEntity player) {
        return AbsorptionHelper.getEquipmentAbsorption(player);
    }

    @Override
    public double getSteadyStateMaxAbsorption(PlayerEntity player) {
        return AbsorptionHelper.getSteadyStateMaxAbsorption(player);
    }
}
