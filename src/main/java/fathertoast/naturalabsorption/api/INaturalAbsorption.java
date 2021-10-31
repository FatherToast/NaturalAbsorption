package fathertoast.naturalabsorption.api;

import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public interface INaturalAbsorption {

    /**
     * Must be called server-side. Throws an
     * IllegalArgumentException if called on client.
     *
     * @param player The player to retrieve heart data from.
     * @return the given player's heart data.
     */
    @Nullable
    IHeartData get(PlayerEntity player);
}
