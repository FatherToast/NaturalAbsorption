package fathertoast.naturalabsorption.api;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;

@SuppressWarnings( "unused" )
public interface INaturalAbsorption {
    /*
     * These are all the tag keys the mod uses for storing the player's absorption data.
     * This data can easily be read and manipulated through the IHeartData interface.
     *
     * Do not modify this NBT directly if IHeartData is available (the NBT will simply be overwritten).
     */
    /**
     * The name of the base NBT compound for all player save data used by the Natural Absorption mod.
     * The NBT compound is located in the player's persisted NBT (see example below).
     * <p>
     * player.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG).getCompound(INaturalAbsorption.TAG_BASE)
     */
    String TAG_BASE = "naturalabsorption";
    /**
     * The name of the NBT integer that represents ticks until absorption regeneration can begin. Located in the base tag.
     */
    String TAG_DELAY_ABSORPTION = "AbsorbDelay";
    /**
     * The name of the NBT integer that represents ticks until health regeneration can begin. Located in the base tag.
     */
    String TAG_DELAY_HEALTH = "HealthDelay";
    
    /**
     * Gets or loads heart data for a player.<br>
     * <br>
     *
     * @param player The player to retrieve heart data from.
     * @return The given player's heart data.
     * @throws IllegalArgumentException if called on client.
     */
    @Nonnull
    IHeartData getHeartData( @Nonnull Player player );
    
    /**
     * @return The API IAbsorptionAccessor instance.
     */
    IAbsorptionAccessor getAbsorptionAccessor();
}