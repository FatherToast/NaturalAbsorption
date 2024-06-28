package fathertoast.naturalabsorption.api.impl;

import fathertoast.naturalabsorption.api.IAbsorptionAccessor;
import fathertoast.naturalabsorption.common.core.hearts.AbsorptionHelper;
import net.minecraft.world.entity.player.Player;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AbsorptionAccessor implements IAbsorptionAccessor {
    
    @Override
    public double getMaxAbsorption( Player player ) {
        return AbsorptionHelper.getMaxAbsorption( player );
    }
    
    @Override
    public double getSteadyStateMaxAbsorption( Player player ) {
        return AbsorptionHelper.getSteadyStateMaxAbsorption( player );
    }
    
    @Override
    public double getNaturalAbsorption( Player player ) {
        return AbsorptionHelper.getNaturalAbsorption( player );
    }
    
    @Override
    public double getBaseNaturalAbsorption( Player player ) {
        return AbsorptionHelper.getBaseNaturalAbsorption( player );
    }
    
    @Override
    public void setBaseNaturalAbsorption( Player player, boolean updateActualAbsorption, double value ) {
        AbsorptionHelper.setBaseNaturalAbsorption( player, updateActualAbsorption, value );
    }
    
    @Override
    public void addBaseNaturalAbsorption( Player player, boolean updateActualAbsorption, double value ) {
        AbsorptionHelper.addBaseNaturalAbsorption( player, updateActualAbsorption, value );
    }
    
    @Override
    public void applyDeathPenalty( Player player ) { AbsorptionHelper.applyDeathPenalty( player ); }
    
    @Override
    public double getEquipmentAbsorption( Player player ) {
        return AbsorptionHelper.getEquipmentAbsorption( player );
    }
    
    @Override
    public double getEnchantmentAbsorption( Player player ) {
        return AbsorptionHelper.getEnchantmentAbsorption( player );
    }
    
    @Override
    public double getArmorReplacementAbsorption( Player player ) {
        return AbsorptionHelper.getArmorReplacementAbsorption( player );
    }
}