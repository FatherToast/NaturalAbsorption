package fathertoast.naturalabsorption.api.impl;

import fathertoast.naturalabsorption.api.IAbsorptionAccessor;
import fathertoast.naturalabsorption.common.health.AbsorptionHelper;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

public class AbsorptionAccessor implements IAbsorptionAccessor {

    @Override
    public void addNaturalAbsorption(@Nonnull PlayerEntity player, boolean updateActualAbsorption, double amount) {
        AbsorptionHelper.addNaturalAbsorption(player, updateActualAbsorption, amount);
    }

    @Override
    public double getNaturalAbsorption(@Nonnull PlayerEntity player) {
        return AbsorptionHelper.getNaturalAbsorption(player);
    }

    @Override
    public void addEquipmentAbsorption(@Nonnull PlayerEntity player, boolean updateActualAbsorption, double amount) {
        AbsorptionHelper.addEquipmentAbsorption(player, updateActualAbsorption, amount);
    }

    @Override
    public double getEquipmentAbsorption(@Nonnull PlayerEntity player) {
        return AbsorptionHelper.getEquipmentAbsorption(player);
    }

    @Override
    public double getSteadyStateMaxAbsorption(@Nonnull PlayerEntity player) {
        return AbsorptionHelper.getSteadyStateMaxAbsorption(player);
    }

    @Override
    public void changeAbsorptionModifier(@Nonnull PlayerEntity player, boolean natural, UUID modifierId, double amount) {
        AbsorptionHelper.changeAbsorptionModifier(player, natural, modifierId, amount);
    }
}
