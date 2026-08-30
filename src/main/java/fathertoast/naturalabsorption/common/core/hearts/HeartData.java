package fathertoast.naturalabsorption.common.core.hearts;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.naturalabsorption.api.IHeartData;
import fathertoast.naturalabsorption.api.impl.NaturalAbsorptionAPI;
import fathertoast.naturalabsorption.common.core.config.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HeartData implements IHeartData {
    
    private static final Map<UUID, HeartData> ENTITY_CACHE = new HashMap<>();
    
    /**
     * Clears the cache of all stored entity health data.
     * Done periodically just in case anything weird goes on.
     */
    public static void clearCache() { ENTITY_CACHE.clear(); }
    
    /**
     * @param entity Entity to get or load heart data for.
     * @return The entity's heart data.
     * @throws IllegalArgumentException if not called server-side.
     */
    public static HeartData get( LivingEntity entity ) {
        // noinspection resource
        if( entity.level().isClientSide ) {
            throw new IllegalArgumentException( "Heart data is only stored on the server side!" );
        }
        UUID uuid = entity.getUUID();
        HeartData data = ENTITY_CACHE.get( uuid );
        
        if( data == null || entity != data.owner ) {
            data = new HeartData( entity );
            ENTITY_CACHE.put( uuid, data );
        }
        return data;
    }
    
    public final LivingEntity owner;
    private final boolean isPlayer;
    private final CompoundTag saveTag;
    
    private int absorptionRecoveryDelay;
    private int healthRecoveryDelay;
    
    /** For internal use, call {@link #get(LivingEntity)} outside this class. */
    private HeartData( LivingEntity entity ) {
        owner = entity;
        isPlayer = owner instanceof Player;
        saveTag = getModNBTTag( entity );
        
        // First-time initialization
        if( !AbsorptionHelper.isBaseNaturalAbsorptionInitialized( owner ) && Config.ABSORPTION.NATURAL.entities.contains( owner.getType() ) ) {
            if( AbsorptionHelper.hasNaturalAbsorptionAttribute( owner ) ) {
                // noinspection ConstantConditions
                double startingAmount = Config.ABSORPTION.NATURAL.entities.get( owner.getType() );
                AbsorptionHelper.setBaseNaturalAbsorption( owner, true, startingAmount );
            }
        }
        
        // Absorption delay
        if( NBTHelper.containsNumber( saveTag, NaturalAbsorptionAPI.TAG_DELAY_ABSORPTION ) ) {
            absorptionRecoveryDelay = saveTag.getInt( NaturalAbsorptionAPI.TAG_DELAY_ABSORPTION );
        }
        else {
            setAbsorptionDelay( 0 );
        }
        
        // Health delay
        if( NBTHelper.containsNumber( saveTag, NaturalAbsorptionAPI.TAG_DELAY_HEALTH ) ) {
            healthRecoveryDelay = saveTag.getInt( NaturalAbsorptionAPI.TAG_DELAY_HEALTH );
        }
        else {
            setHealthDelay( 0 );
        }
    }
    
    
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
    
    /** Starts the entity's recovery delay timers. */
    @Override
    public void startRecoveryDelay() {
        if( HeartManager.isHealthEnabled() && Config.HEALTH.GENERAL.recoveryDelay.get() > 0 ) {
            setHealthDelay( Config.HEALTH.GENERAL.recoveryDelay.get() );
        }
        if( HeartManager.isAbsorptionEnabled() && Config.ABSORPTION.GENERAL.recoveryDelay.get() > 0 ) {
            setAbsorptionDelay( Config.ABSORPTION.GENERAL.recoveryDelay.get() );
        }
    }
    
    /** Helper method to set the entity's current absorption; clamps the value between 0 and the entity's personal maximum. */
    public void setAbsorption( float value ) {
        owner.setAbsorptionAmount( Mth.clamp( value, 0.0F, (float) AbsorptionHelper.getMaxAbsorption( owner ) ) );
    }
    
    /** Updates the entity's absorption and health values by the number of ticks since this was last updated. */
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
    
    /** Updated absorption data. */
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
        
        // Handle hunger cost restrictions for players
        if( isPlayer && ((Player) owner).getFoodData().getFoodLevel() < Config.ABSORPTION.GENERAL.recoveryHungerRequired.get() )
            return;
        
        // Recover absorption, if needed
        final double maxAbsorption = AbsorptionHelper.getMaxAbsorption( owner );
        final float oldAbsorption = owner.getAbsorptionAmount();
        
        if( recovered > 0.0F && oldAbsorption < maxAbsorption ) {
            // Apply recovery rate increase from armor
            if( HeartManager.isArmorReplacementEnabled() ) {
                if( Config.EQUIPMENT.ARMOR.armorRecovery.get() > 0.0 ) {
                    final double armor = owner.getAttributeValue( Attributes.ARMOR );
                    
                    if( armor > 0.0F ) {
                        recovered *= (float) (1.0 + armor * Config.EQUIPMENT.ARMOR.armorRecovery.get());
                    }
                }
                if( Config.EQUIPMENT.ARMOR.armorToughnessRecovery.get() > 0.0 ) {
                    final double toughness = owner.getAttributeValue( Attributes.ARMOR_TOUGHNESS );
                    
                    if( toughness > 0.0F ) {
                        recovered *= (float) (1.0 + toughness * Config.EQUIPMENT.ARMOR.armorToughnessRecovery.get());
                    }
                }
            }
            
            // Add absorption recovery
            final double newAbsorption = Math.min( maxAbsorption, oldAbsorption + recovered );
            owner.setAbsorptionAmount( (float) newAbsorption );
            
            // Apply hunger cost
            if( isPlayer && newAbsorption - oldAbsorption > 0 && Config.ABSORPTION.GENERAL.recoveryHungerCost.get() > 0.0 ) {
                ((Player) owner).getFoodData().addExhaustion( (float) (newAbsorption - oldAbsorption) *
                        Config.ABSORPTION.GENERAL.recoveryHungerCost.getFloat() );
            }
        }
    }
    
    /** Updates health data. */
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
        if( isPlayer && ((Player) owner).getFoodData().getFoodLevel() < Config.HEALTH.GENERAL.recoveryHungerRequired.get() )
            return;
        
        // Recover health, if needed
        final float maxHealth = Math.min( Config.HEALTH.GENERAL.recoveryMax.getFloat(), owner.getMaxHealth() );
        final float oldHealth = owner.getHealth();
        
        if( recovered > 0.0F && oldHealth < maxHealth ) {
            // Add health recovery
            final float newHealth = Math.min( maxHealth, oldHealth + recovered );
            owner.setHealth( newHealth );
            
            // Apply hunger cost
            if( isPlayer && newHealth - oldHealth > 0 && Config.HEALTH.GENERAL.recoveryHungerCost.get() > 0.0 ) {
                ((Player) owner).getFoodData().addExhaustion( (newHealth - oldHealth) *
                        Config.HEALTH.GENERAL.recoveryHungerCost.getFloat() );
            }
        }
    }
    
    /** @return The compound tag that holds Natural Absorption's data. */
    private static CompoundTag getModNBTTag( LivingEntity entity ) {
        // Start with the base entity Forge data.
        // If the entity is a player, we get the persist-on-death data.
        CompoundTag tag = entity instanceof Player player
                ? NBTHelper.getPlayerData( player )
                : NBTHelper.getForgeData( entity );
        
        return NBTHelper.getOrCreateCompound( tag, NaturalAbsorptionAPI.TAG_BASE );
    }
}