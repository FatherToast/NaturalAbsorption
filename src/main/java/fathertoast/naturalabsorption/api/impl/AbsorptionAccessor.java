package fathertoast.naturalabsorption.api.impl;

import fathertoast.naturalabsorption.api.IAbsorptionAccessor;
import fathertoast.naturalabsorption.common.core.hearts.AbsorptionHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AbsorptionAccessor implements IAbsorptionAccessor {
    
    @Override
    public double getMaxAbsorption( LivingEntity entity ) {
        return AbsorptionHelper.getMaxAbsorption( entity );
    }
    
    @Override
    public double getSteadyStateMaxAbsorption( LivingEntity entity ) {
        return AbsorptionHelper.getSteadyStateMaxAbsorption( entity );
    }
    
    @Override
    public double getEquipmentAbsorption( LivingEntity entity ) {
        return AbsorptionHelper.getEquipmentAbsorption( entity );
    }
    
    @Override
    public double getEnchantmentAbsorption( LivingEntity entity ) {
        return AbsorptionHelper.getEnchantmentAbsorption( entity );
    }
    
    @Override
    public double getArmorReplacementAbsorption( LivingEntity entity ) {
        return AbsorptionHelper.getArmorReplacementAbsorption( entity );
    }
    
    @Override
    public double getNaturalAbsorption( LivingEntity entity ) {
        return AbsorptionHelper.getNaturalAbsorption( entity );
    }
    
    @Override
    public double getBaseNaturalAbsorption( LivingEntity entity ) {
        return AbsorptionHelper.getBaseNaturalAbsorption( entity );
    }
    
    @Override
    public void setBaseNaturalAbsorption( LivingEntity entity, boolean updateActualAbsorption, double value ) {
        AbsorptionHelper.setBaseNaturalAbsorption( entity, updateActualAbsorption, value );
    }
    
    @Override
    public void addBaseNaturalAbsorption( LivingEntity entity, boolean updateActualAbsorption, double value ) {
        AbsorptionHelper.addBaseNaturalAbsorption( entity, updateActualAbsorption, value );
    }
    
    @Override
    public void applyDeathPenalty( Player player ) { AbsorptionHelper.applyDeathPenalty( player ); }
}