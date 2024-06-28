package fathertoast.naturalabsorption.api.impl;

import fathertoast.naturalabsorption.api.IAbsorptionAccessor;
import fathertoast.naturalabsorption.api.IHeartData;
import fathertoast.naturalabsorption.api.INaturalAbsorption;
import fathertoast.naturalabsorption.common.core.hearts.HeartData;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;

/**
 * This is the API implementation. Woah!
 */
public class NaturalAbsorptionAPI implements INaturalAbsorption {
    
    private static final IAbsorptionAccessor absorptionAccessor = new AbsorptionAccessor();
    
    /**
     * Gets or loads heart data for a player.
     *
     * @param player The player to retrieve heart data from.
     * @return The given player's heart data.
     * @throws IllegalArgumentException if called on client.
     */
    @Nonnull
    @Override
    public IHeartData getHeartData( @Nonnull Player player ) { return HeartData.get( player ); }
    
    /**
     * @return The API IAbsorptionAccessor instance.
     */
    @Override
    public IAbsorptionAccessor getAbsorptionAccessor() { return absorptionAccessor; }
}