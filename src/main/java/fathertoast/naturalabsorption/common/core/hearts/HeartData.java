package fathertoast.naturalabsorption.common.core.hearts;

import fathertoast.naturalabsorption.api.IHeartData;
import fathertoast.naturalabsorption.api.impl.NaturalAbsorptionAPI;
import fathertoast.naturalabsorption.common.config.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings( "WeakerAccess" )
public class HeartData implements IHeartData {
    
    private static final int NBT_TYPE_NUMERICAL = 99;
    
    private static final Map<UUID, HeartData> PLAYER_CACHE = new HashMap<>();
    
    /**
     * Clears the cache of all stored player health data.
     * Done periodically just in case anything weird goes on.
     */
    public static void clearCache() { PLAYER_CACHE.clear(); }
    
    /**
     * @param player Player to get or load heart data for.
     * @return The player's heart data.
     */
    @Nonnull
    public static HeartData get( @Nonnull Player player ) {
        if( player.level().isClientSide ) {
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
    
    public final Player owner;
    private final CompoundTag saveTag;
    
    private int absorptionRecoveryDelay;
    private int healthRecoveryDelay;
    
    
    // Absorption recovery delay methods
    @Override
    public int getAbsorptionDelay() { return absorptionRecoveryDelay; }
    
    @Override
    public void setAbsorptionDelay( int value ) {
        saveTag.putFloat( NaturalAbsorptionAPI.TAG_DELAY_ABSORPTION, value );
        absorptionRecoveryDelay = value;
    }
    
    public void reduceAbsorptionDelay( int value ) {
        setAbsorptionDelay( absorptionRecoveryDelay - value );
    }
    
    // Health recovery delay methods
    @Override
    public int getHealthDelay() { return healthRecoveryDelay; }
    
    @Override
    public void setHealthDelay( int value ) {
        saveTag.putFloat( NaturalAbsorptionAPI.TAG_DELAY_HEALTH, value );
        healthRecoveryDelay = value;
    }
    
    public void reduceHealthDelay( int value ) {
        setHealthDelay( healthRecoveryDelay - value );
    }
    
    /** Starts the player's recovery delay timers. */
    @Override
    public void startRecoveryDelay() {
        if( HeartManager.isHealthEnabled() && Config.HEALTH.GENERAL.recoveryDelay.get() > 0 ) {
            setHealthDelay( Config.HEALTH.GENERAL.recoveryDelay.get() );
        }
        if( HeartManager.isAbsorptionEnabled() && Config.ABSORPTION.GENERAL.recoveryDelay.get() > 0 ) {
            setAbsorptionDelay( Config.ABSORPTION.GENERAL.recoveryDelay.get() );
        }
    }
    
    /** Helper method to set the player's current absorption; clamps the value between 0 and the player's personal maximum. */
    public void setAbsorption( float value ) {
        owner.setAbsorptionAmount( Mth.clamp( value, 0.0F, (float) AbsorptionHelper.getMaxAbsorption( owner ) ) );
    }
    
    /** Updates the player's absorption and health values by the number of ticks since this was last updated. */
    void update() {
        if( HeartManager.isHealthEnabled() && Config.HEALTH.GENERAL.recoveryDelay.get() >= 0 ) {
            updateHealth();
        }
        if( HeartManager.isAbsorptionEnabled() ) {
            if( Config.ABSORPTION.GENERAL.recoveryDelay.get() >= 0 ) {
                updateAbsorption();
            }
        }
    }
    
    private void updateAbsorption() {
        // TEMP Try with this disabled; allow mods to add raw vanilla absorption if they want past the cap
        //        if( owner.getAbsorptionAmount() > AbsorptionHelper.getMaxAbsorption( owner ) ) {
        //            owner.setAbsorptionAmount( (float) AbsorptionHelper.getMaxAbsorption( owner ) );
        //        }
        
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
        final double maxAbsorption = AbsorptionHelper.getMaxAbsorption( owner );
        final float oldAbsorption = owner.getAbsorptionAmount();
        
        if( recovered > 0.0F && oldAbsorption < maxAbsorption ) {
            // Apply recovery rate increase from armor
            if( HeartManager.isArmorReplacementEnabled() ) {
                if( Config.EQUIPMENT.ARMOR.armorRecovery.get() > 0.0 ) {
                    final double armor = owner.getAttributeValue( Attributes.ARMOR );
                    
                    if( armor > 0.0F ) {
                        recovered *= 1.0 + armor * Config.EQUIPMENT.ARMOR.armorRecovery.get();
                    }
                }
                if( Config.EQUIPMENT.ARMOR.armorToughnessRecovery.get() > 0.0 ) {
                    final double toughness = owner.getAttributeValue( Attributes.ARMOR_TOUGHNESS );
                    
                    if( toughness > 0.0F ) {
                        recovered *= 1.0 + toughness * Config.EQUIPMENT.ARMOR.armorToughnessRecovery.get();
                    }
                }
            }
            
            // Add absorption recovery
            final double newAbsorption = Math.min( maxAbsorption, oldAbsorption + recovered );
            owner.setAbsorptionAmount( (float) newAbsorption );
            
            // Apply hunger cost
            if( newAbsorption - oldAbsorption > 0 && Config.ABSORPTION.GENERAL.recoveryHungerCost.get() > 0.0 ) {
                owner.getFoodData().addExhaustion( (float) (newAbsorption - oldAbsorption) *
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
        if( owner.getFoodData().getFoodLevel() < Config.HEALTH.GENERAL.recoveryHungerRequired.get() ) return;
        
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
    
    private HeartData( Player player ) {
        owner = player;
        saveTag = getModNBTTag( player );
        
        // First-time initialization
        if( !AbsorptionHelper.isBaseNaturalAbsorptionInitialized( owner ) ) {
            AbsorptionHelper.setBaseNaturalAbsorption( owner, true, Config.ABSORPTION.NATURAL.startingAmount.get() );
        }
        
        // Absorption delay
        if( saveTag.contains( NaturalAbsorptionAPI.TAG_DELAY_ABSORPTION, NBT_TYPE_NUMERICAL ) ) {
            absorptionRecoveryDelay = saveTag.getInt( NaturalAbsorptionAPI.TAG_DELAY_ABSORPTION );
        }
        else {
            setAbsorptionDelay( 0 );
        }
        
        // Health delay
        if( saveTag.contains( NaturalAbsorptionAPI.TAG_DELAY_HEALTH, NBT_TYPE_NUMERICAL ) ) {
            healthRecoveryDelay = saveTag.getInt( NaturalAbsorptionAPI.TAG_DELAY_HEALTH );
        }
        else {
            setHealthDelay( 0 );
        }
    }
    
    /** @return The nbt tag compound that holds all of this mod's data. */
    private static CompoundTag getModNBTTag( Player player ) {
        // Start with the base entity forge data
        CompoundTag tag = player.getPersistentData().getCompound( Player.PERSISTED_NBT_TAG );
        
        // Get/make a tag unique to this mod
        if( !tag.contains( NaturalAbsorptionAPI.TAG_BASE, tag.getId() ) ) {
            tag.put( NaturalAbsorptionAPI.TAG_BASE, new CompoundTag() );
        }
        return tag.getCompound( NaturalAbsorptionAPI.TAG_BASE );
    }
}