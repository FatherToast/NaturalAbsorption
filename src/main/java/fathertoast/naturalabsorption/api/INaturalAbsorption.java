package fathertoast.naturalabsorption.api;

import net.minecraft.world.entity.LivingEntity;

@SuppressWarnings( "unused" )
public interface INaturalAbsorption {
    /*
     * These are all the tag keys the mod uses for storing the player's absorption data.
     * This data can easily be read and manipulated through the IHeartData interface.
     *
     * Do not modify this NBT directly if IHeartData is available (the NBT will simply be overwritten).
     */
    /**
     * The name of the base NBT compound for all entity save data used by the Natural Absorption mod.
     * The base NBT compound that is written/read to depends on whether the entity is a player or not.
     * <br><br>
     * For players the base tag is the persist-on-death tag:
     * <p>
     * <code>entity.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getCompound(INaturalAbsorption.TAG_BASE)</code>
     * <p>
     * For other living entities the base tag is the Forge persistant data tag:
     * <code>entity.getPersistentData().getCompound(INaturalAbsorption.TAG_BASE)</code>
     */
    String TAG_BASE = "naturalabsorption";
    /** The name of the NBT integer that represents ticks until absorption regeneration can begin. Located in the base tag. */
    String TAG_DELAY_ABSORPTION = "AbsorbDelay";
    /** The name of the NBT integer that represents ticks until health regeneration can begin. Located in the base tag. */
    String TAG_DELAY_HEALTH = "HealthDelay";
    
    /**
     * Gets or loads heart data for the specified entity.
     *
     * @param entity The entity to retrieve heart data from.
     * @return The given entity's heart data.
     * @throws IllegalArgumentException if called on client.
     */
    IHeartData getHeartData( LivingEntity entity );
    
    /** @return The absorption accessor instance provided by Natural Absorption. */
    IAbsorptionAccessor getAbsorptionAccessor();
}