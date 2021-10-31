package fathertoast.naturalabsorption.api.impl;

import fathertoast.naturalabsorption.api.IHeartData;
import fathertoast.naturalabsorption.api.INaturalAbsorption;
import fathertoast.naturalabsorption.common.health.HeartData;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the API implementation. Woah!
 */
public class NaturalAbsorptionAPI implements INaturalAbsorption {
    /**
     * Must be called server-side. Throws an IllegalArgumentException if called on client.
     *
     * @param player The player to retrieve heart data from.
     * @return The given player's heart data.
     */
    @Nonnull
    @Override
    public IHeartData get( @Nonnull PlayerEntity player ) {
        return HeartData.get( player );
    }
}