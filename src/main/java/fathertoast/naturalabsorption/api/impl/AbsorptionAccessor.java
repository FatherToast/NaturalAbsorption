package fathertoast.naturalabsorption.api.impl;

import fathertoast.naturalabsorption.api.IAbsorptionAccessor;
import fathertoast.naturalabsorption.common.hearts.AbsorptionHelper;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AbsorptionAccessor implements IAbsorptionAccessor {
    
    @Override
    public double getMaxAbsorption( PlayerEntity player ) {
        return AbsorptionHelper.getMaxAbsorption( player );
    }
    
    @Override
    public double getSteadyStateMaxAbsorption( PlayerEntity player ) {
        return AbsorptionHelper.getSteadyStateMaxAbsorption( player );
    }
    
    @Override
    public double getNaturalAbsorption( PlayerEntity player ) {
        return AbsorptionHelper.getNaturalAbsorption( player );
    }
    
    @Override
    public double getBaseNaturalAbsorption( PlayerEntity player ) {
        return AbsorptionHelper.getBaseNaturalAbsorption( player );
    }
    
    @Override
    public void setBaseNaturalAbsorption( PlayerEntity player, boolean updateActualAbsorption, double value ) {
        AbsorptionHelper.setBaseNaturalAbsorption( player, updateActualAbsorption, value );
    }
    
    @Override
    public void addBaseNaturalAbsorption( PlayerEntity player, boolean updateActualAbsorption, double value ) {
        AbsorptionHelper.addBaseNaturalAbsorption( player, updateActualAbsorption, value );
    }
    
    @Override
    public void applyDeathPenalty( PlayerEntity player ) { AbsorptionHelper.applyDeathPenalty( player ); }
    
    @Override
    public double getEquipmentAbsorption( PlayerEntity player ) {
        return AbsorptionHelper.getEquipmentAbsorption( player );
    }
    
    @Override
    public double getEnchantmentAbsorption( PlayerEntity player ) {
        return AbsorptionHelper.getEnchantmentAbsorption( player );
    }
    
    @Override
    public double getArmorReplacementAbsorption( PlayerEntity player ) {
        return AbsorptionHelper.getArmorReplacementAbsorption( player );
    }
}