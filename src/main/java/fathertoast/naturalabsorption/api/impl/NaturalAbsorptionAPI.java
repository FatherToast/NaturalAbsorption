package fathertoast.naturalabsorption.api.impl;

import fathertoast.naturalabsorption.api.IAbsorptionAccessor;
import fathertoast.naturalabsorption.api.IHeartData;
import fathertoast.naturalabsorption.api.INaturalAbsorption;
import fathertoast.naturalabsorption.common.core.hearts.HeartData;
import net.minecraft.world.entity.LivingEntity;

/**
 * This is the API implementation. Woah!
 */
public class NaturalAbsorptionAPI implements INaturalAbsorption {
    
    private static final IAbsorptionAccessor absorptionAccessor = new AbsorptionAccessor();
    
    /**
     * Gets or loads heart data for a player.
     *
     * @param entity The player to retrieve heart data from.
     * @return The given player's heart data.
     * @throws IllegalArgumentException if called on client.
     */
    @Override
    public IHeartData getHeartData( LivingEntity entity ) { return HeartData.get( entity ); }
    
    /**
     * @return The API IAbsorptionAccessor instance.
     */
    @Override
    public IAbsorptionAccessor getAbsorptionAccessor() { return absorptionAccessor; }
}