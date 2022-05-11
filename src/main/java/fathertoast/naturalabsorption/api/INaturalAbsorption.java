package fathertoast.naturalabsorption.api;

import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;

public interface INaturalAbsorption {

    /**
     * These are all the tag keys the mod uses for
     * storing the player's absorption data.<br>
     * <br>
     *
     * This data can easily be read and
     * manipulated through the IHeartData
     * interface, but can also be tinkered
     * with manually by getting the player's
     * persistent data tag and using these keys.
     */
    String TAG_BASE = "naturalabsorption";
    String TAG_NATURAL_ABSORPTION = "AbsorbNatural";
    String TAG_EQUIPMENT_ABSORPTION = "AbsorbEquip";
    String TAG_DELAY_ABSORPTION = "AbsorbDelay";
    String TAG_DELAY_HEALTH = "HealthDelay";

    /**
     * Must be called server-side. Throws an
     * IllegalArgumentException if called on client.<br>
     * <br>
     *
     * @param player The player to retrieve heart data from.
     * @return the given player's heart data.
     */
    @Nonnull
    IHeartData getHeartData(@Nonnull PlayerEntity player);

    /**
     * @return the player's natural absorption.
     *         Can be called on both server and client.
     */
    float getNaturalAbsorption(@Nonnull PlayerEntity player);
}