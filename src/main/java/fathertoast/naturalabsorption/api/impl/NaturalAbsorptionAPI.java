package fathertoast.naturalabsorption.api.impl;

import fathertoast.naturalabsorption.api.IHeartData;
import fathertoast.naturalabsorption.api.INaturalAbsorption;
import fathertoast.naturalabsorption.client.RenderEventHandler;
import fathertoast.naturalabsorption.common.health.HeartData;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the API implementation. Woah!
 */
public class NaturalAbsorptionAPI implements INaturalAbsorption {

    /**
     * Must be called server-side. Throws an
     * IllegalArgumentException if called on client.<br>
     * <br>
     *
     * @param player The player to retrieve heart data from.
     * @return the given player's heart data.
     */
    @Nonnull
    @Override
    public IHeartData getHeartData(@Nonnull PlayerEntity player) {
        return HeartData.get(player);
    }

    /**
     * @return the player's natural absorption.
     *         Can be called on both server and client.
     */
    @Override
    public float getNaturalAbsorption(@Nonnull PlayerEntity player) {
        if (player.level.isClientSide) {
            return RenderEventHandler.PLAYER_NATURAL_ABSORPTION;
        }
        else {
            return this.getHeartData(player).getNaturalAbsorption();
        }
    }
}