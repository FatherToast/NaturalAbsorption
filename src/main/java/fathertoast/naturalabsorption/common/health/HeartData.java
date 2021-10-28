package fathertoast.naturalabsorption.common.health;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.network.NetworkHelper;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings( "WeakerAccess" )
public class HeartData {
    
    private static final int NBT_TYPE_NUMERICAL = 99;
    
    private static final String TAG_BASE = NaturalAbsorption.MOD_ID;
    private static final String TAG_NATURAL_ABSORPTION = "AbsorbNatural";
    private static final String TAG_DELAY_ABSORPTION = "AbsorbDelay";
    private static final String TAG_DELAY_HEALTH = "HealthDelay";
    
    private static final Map<UUID, HeartData> PLAYER_CACHE = new HashMap<>();
    
    /** Clears the cache of all stored player heath data. Done periodically just in case anything weird goes on. */
    public static void clearCache() { PLAYER_CACHE.clear(); }
    
    /**
     * @param player Player to get or load heart data for.
     * @return The player's heart data.
     */
    public static HeartData get( PlayerEntity player ) {
        if( player.level.isClientSide ) {
            throw new IllegalArgumentException( "Heart data is only stored on the server side!" );
        }
        UUID uuid = player.getUUID();
        HeartData data = PLAYER_CACHE.get( uuid );
        
        if( data == null || player != data.owner ) {
            data = new HeartData( player );
            PLAYER_CACHE.put( uuid, data );
        }
        return data;
    }
    
    public final PlayerEntity owner;
    private final CompoundNBT saveTag;
    
    private float naturalAbsorption = -1.0F;
    private int absorptionRecoveryDelay;
    private int healthRecoveryDelay;
    
    // Absorption health capacity methods
    
    public float getNaturalAbsorption() { return naturalAbsorption; }
    
    public void setNaturalAbsorption( float value ) {
        if( HeartManager.isAbsorptionEnabled() ) {
            value = MathHelper.clamp( value, 0.0F, (float) Config.ABSORPTION.NATURAL.maximumAmount.get() );
            if( naturalAbsorption != value ) {
                saveTag.putFloat( TAG_NATURAL_ABSORPTION, value );
                naturalAbsorption = value;
                
                if( owner instanceof ServerPlayerEntity ) {
                    NetworkHelper.setNaturalAbsorption( (ServerPlayerEntity) owner, value );
                }
            }
        }
    }
    
    // Absorption recovery delay methods
    
    public int getAbsorptionDelay() { return absorptionRecoveryDelay; }
    
    public void setAbsorptionDelay( int value ) {
        saveTag.putFloat( TAG_DELAY_ABSORPTION, value );
        absorptionRecoveryDelay = value;
    }
    
    public void reduceAbsorptionDelay( int value ) { setAbsorptionDelay( absorptionRecoveryDelay - value ); }
    
    // Health recovery delay methods
    
    public int getHealthDelay() { return healthRecoveryDelay; }
    
    public void setHealthDelay( int value ) {
        saveTag.putFloat( TAG_DELAY_HEALTH, value );
        healthRecoveryDelay = value;
    }
    
    public void reduceHealthDelay( int val ) { setHealthDelay( healthRecoveryDelay - val ); }
    
    /** Starts the player's recovery delay timers. */
    public void startRecoveryDelay() {
        if( HeartManager.isHealthEnabled() && Config.HEALTH.GENERAL.recoveryDelay.get() > 0 ) {
            setHealthDelay( Config.HEALTH.GENERAL.recoveryDelay.get() );
        }
        if( HeartManager.isAbsorptionEnabled() && Config.ABSORPTION.GENERAL.recoveryDelay.get() > 0 ) {
            setAbsorptionDelay( Config.ABSORPTION.GENERAL.recoveryDelay.get() );
        }
    }
    
    /** @return The player's max absorption not counting buffs, limited by the global max absorption config. */
    private float getSteadyStateMaxAbsorption() {
        final float calculatedMax = getNaturalAbsorption() + HeartManager.getEquipmentAbsorption( owner );
        return Config.ABSORPTION.GENERAL.globalMax.get() < 0.0F ? calculatedMax :
                Math.min( calculatedMax, (float) Config.ABSORPTION.GENERAL.globalMax.get() );
    }
    
    /** @return The player's max absorption, from all sources combined. */
    public float getMaxAbsorption() { return getSteadyStateMaxAbsorption() + HeartManager.getPotionAbsorption( owner ); }
    
    /** Helper method to set the player's current absorption; clamps the value between 0 and the player's personal maximum. */
    public void setAbsorption( float value ) {
        owner.setAbsorptionAmount( MathHelper.clamp( value, 0.0F, getMaxAbsorption() ) );
    }
    
    /** Updates the player's absorption and health values by the number of ticks since this was last updated. */
    void update() {
        if( HeartManager.isHealthEnabled() && Config.HEALTH.GENERAL.recoveryDelay.get() >= 0 ) {
            updateHealth();
        }
        if( HeartManager.isAbsorptionEnabled() && Config.ABSORPTION.GENERAL.recoveryDelay.get() >= 0 ) {
            updateAbsorption();
        }
    }
    
    private void updateAbsorption() {
        // Update delay and determine amount to recover accordingly
        final int updateTime = Config.MAIN.GENERAL.updateTime.get();
        float recovered;
        if( getAbsorptionDelay() > 0 ) {
            if( getAbsorptionDelay() < updateTime ) {
                final int ticksPastZero = updateTime - getAbsorptionDelay();
                recovered = (float) (Config.ABSORPTION.GENERAL.recoveryRate.get() * ticksPastZero);
                setAbsorptionDelay( 0 );
            }
            else {
                reduceAbsorptionDelay( updateTime );
                return;
            }
        }
        else {
            recovered = (float) (Config.ABSORPTION.GENERAL.recoveryRate.get() * updateTime);
        }
        
        // Handle hunger cost restrictions
        if( owner.getFoodData().getFoodLevel() < Config.ABSORPTION.GENERAL.recoveryHungerRequired.get() ) return;
        
        // Recover absorption, if needed
        final float maxAbsorption = getMaxAbsorption();
        final float oldAbsorption = owner.getAbsorptionAmount();
        
        if( recovered > 0.0F && oldAbsorption < maxAbsorption ) {
            // Apply armor toughness to recovery
            if( HeartManager.isArmorReplacementEnabled() && Config.EQUIPMENT.ARMOR.armorToughnessRecovery.get() > 0.0 ) {
                final double toughness = owner.getAttributeValue( Attributes.ARMOR_TOUGHNESS );
                if( toughness > 0.0F ) {
                    recovered *= 1.0 + toughness * Config.EQUIPMENT.ARMOR.armorToughnessRecovery.get();
                }
            }
            
            // Add absorption recovery
            final float newAbsorption = Math.min( maxAbsorption, oldAbsorption + recovered );
            owner.setAbsorptionAmount( newAbsorption );
            
            // Apply hunger cost
            if( newAbsorption - oldAbsorption > 0 && Config.ABSORPTION.GENERAL.recoveryHungerCost.get() > 0.0 ) {
                owner.getFoodData().addExhaustion( (newAbsorption - oldAbsorption) *
                        (float) Config.ABSORPTION.GENERAL.recoveryHungerCost.get() );
            }
        }
    }
    
    private void updateHealth() {
        // Update delay and determine amount to recover accordingly
        final int updateTime = Config.MAIN.GENERAL.updateTime.get();
        final float recovered;
        if( getHealthDelay() > 0 ) {
            if( getHealthDelay() < updateTime ) {
                final int ticksPastZero = updateTime - getHealthDelay();
                recovered = (float) (Config.HEALTH.GENERAL.recoveryRate.get() * ticksPastZero);
                setHealthDelay( 0 );
            }
            else {
                reduceHealthDelay( updateTime );
                return;
            }
        }
        else {
            recovered = (float) (Config.HEALTH.GENERAL.recoveryRate.get() * updateTime);
        }
        
        // Handle hunger cost restrictions
        if( owner.getFoodData().getFoodLevel() < Config.HEALTH.GENERAL.recoveryHungerRequired.get() ) {
            return;
        }
        
        // Recover health, if needed
        final float maxHealth = Math.min( (float) Config.HEALTH.GENERAL.recoveryMax.get(), owner.getMaxHealth() );
        final float oldHealth = owner.getHealth();
        
        if( recovered > 0.0F && oldHealth < maxHealth ) {
            // Add health recovery
            final float newHealth = Math.min( maxHealth, oldHealth + recovered );
            owner.setHealth( newHealth );
            
            // Apply hunger cost
            if( newHealth - oldHealth > 0 && Config.HEALTH.GENERAL.recoveryHungerCost.get() > 0.0 ) {
                owner.getFoodData().addExhaustion( (newHealth - oldHealth) *
                        (float) Config.HEALTH.GENERAL.recoveryHungerCost.get() );
            }
        }
    }
    
    private HeartData( PlayerEntity player ) {
        owner = player;
        saveTag = getModNBTTag();
        
        if( saveTag.contains( TAG_NATURAL_ABSORPTION, NBT_TYPE_NUMERICAL ) ) {
            setNaturalAbsorption( saveTag.getFloat( TAG_NATURAL_ABSORPTION ) );
        }
        else if( HeartManager.isAbsorptionEnabled() ) {
            // New player, give starting absorption
            setNaturalAbsorption( (float) Config.ABSORPTION.NATURAL.startingAmount.get() );
            setAbsorption( getNaturalAbsorption() );
        }
        
        if( saveTag.contains( TAG_DELAY_ABSORPTION, NBT_TYPE_NUMERICAL ) ) {
            absorptionRecoveryDelay = saveTag.getInt( TAG_DELAY_ABSORPTION );
        }
        else {
            setAbsorptionDelay( 0 );
        }
        if( saveTag.contains( TAG_DELAY_HEALTH, NBT_TYPE_NUMERICAL ) ) {
            healthRecoveryDelay = saveTag.getInt( TAG_DELAY_HEALTH );
        }
        else {
            setHealthDelay( 0 );
        }
    }
    
    /** @return The nbt tag compound that holds all of this mod's data. */
    private CompoundNBT getModNBTTag() {
        // Start with the base entity forge data
        CompoundNBT tag = owner.getPersistentData();
        
        // Get/make the persistent nbt tag so we don't lose data on respawn
        if( !tag.contains( PlayerEntity.PERSISTED_NBT_TAG, tag.getId() ) ) {
            tag.put( PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT() );
        }
        tag = tag.getCompound( PlayerEntity.PERSISTED_NBT_TAG );
        
        // Get/make a tag unique to this mod
        if( !tag.contains( TAG_BASE, tag.getId() ) ) {
            tag.put( TAG_BASE, new CompoundNBT() );
        }
        return tag.getCompound( TAG_BASE );
    }
}