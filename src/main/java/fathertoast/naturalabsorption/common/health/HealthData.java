package fathertoast.naturalabsorption.common.health;

import fathertoast.naturalabsorption.*;
import fathertoast.naturalabsorption.common.config.Config;
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
public class HealthData {

	private static final int NBT_TAG_PRIMITIVE = 99;
	
	private static final String TAG_BASE                = "NaturalAbsorption";
	private static final String TAG_CAPACITY_ABSORPTION = "AbspCapacity";
	private static final String TAG_DELAY_ABSORPTION    = "AbspDelay";
	private static final String TAG_DELAY_NORMAL        = "NormDelay";
	
	private static final Map< UUID, HealthData > PLAYER_CACHE = new HashMap<>( );
	
	/** Clears the cache of all stored player heath data. Done periodically just in case anything weird goes on. */
	public static void clearCache( ) { PLAYER_CACHE.clear( ); }
	
	/**
	 * @param player Player to get or load health data for.
	 *
	 * @return The player's health data.
	 */
	public static
	HealthData get( PlayerEntity player ) {
		if( player.level.isClientSide ) {
			throw new IllegalArgumentException( "Health data is only stored on the server side!" );
		}
		UUID uuid = player.getUUID();
		HealthData data = PLAYER_CACHE.get( uuid );

		if( data == null || player != data.owner ) {
			data = new HealthData( player );
			PLAYER_CACHE.put( uuid, data );
		}
		return data;
	}
	
	public final PlayerEntity owner;
	private final CompoundNBT saveTag;
	
	private float capacityAbsp = -1.0F;
	private int  delayAbsp;
	private int  delayNorm;
	
	// Absorption health capacity methods
	
	public float getAbsorptionCapacity( ) { return capacityAbsp; }
	
	public void setAbsorptionCapacity( float value ) {
		if( Config.get( ).ABSORPTION_HEALTH.ENABLED ) {
			value = MathHelper.clamp( value, 0.0F, Config.get( ).ABSORPTION_UPGRADES.MAXIMUM );
			if( capacityAbsp != value ) {
				saveTag.putFloat(TAG_CAPACITY_ABSORPTION, value);
				capacityAbsp = value;

				if (this.owner instanceof ServerPlayerEntity) {
					NetworkHelper.setAbsorptionCapacity((ServerPlayerEntity) this.owner, value);
				}
			}
		}
	}
	
	// Absorption health recovery delay methods
	
	public int getAbsorptionDelay( ) { return delayAbsp; }
	
	public void setAbsorptionDelay( int value ) {
		saveTag.putFloat( TAG_DELAY_ABSORPTION, value );
		delayAbsp = value;
	}
	
	public void reduceAbsorptionDelay( int value ) { setAbsorptionDelay( delayAbsp - value ); }
	
	// Normal health recovery delay methods
	
	public int getNormalDelay( ) { return delayNorm; }
	
	public void setNormalDelay( int value ) {
		saveTag.putFloat( TAG_DELAY_NORMAL, value );
		delayNorm = value;
	}
	
	public void reduceNormalDelay( int val ) { setNormalDelay( delayNorm - val ); }
	
	/** Starts the player's recovery delay timers. */
	public void startRecoveryDelay( ) {
		if( Config.get( ).NORMAL_HEALTH.ENABLED && Config.get( ).NORMAL_HEALTH.RECOVER_DELAY > 0 ) {
			setNormalDelay( Config.get( ).NORMAL_HEALTH.RECOVER_DELAY );
		}
		if( Config.get( ).ABSORPTION_HEALTH.ENABLED && Config.get( ).ABSORPTION_HEALTH.RECOVER_DELAY > 0 ) {
			setAbsorptionDelay( Config.get( ).ABSORPTION_HEALTH.RECOVER_DELAY );
		}
	}
	
	/** @return The player's "global" absorption capacity, limited by the max global capacity config. */
	public float getGlobalCapacity( ) { return Math.min( getAbsorptionCapacity( ) + HealthManager.getArmorAbsorption( owner ), Config.get( ).ABSORPTION_HEALTH.GLOBAL_MAXIMUM ); }
	
	/** @return The player's effective absorption capacity, from all sources combined. */
	public float getEffectiveCapacity( ) { return getGlobalCapacity( ) + HealthManager.getPotionAbsorption( owner ); }
	
	/**
	 * Helper method to set the player's current absorption health; clamps the value between 0 and the player's personal maximum.
	 */
	public void setAbsorptionHealth( float value ) {
		owner.setAbsorptionAmount( MathHelper.clamp( value, 0.0F, getEffectiveCapacity( ) ) );
	}
	
	/**
	 * Updates the player's absorption and normal health values by the number of ticks since this was last updated.
	 */
	void update( ) {
		if( Config.get( ).NORMAL_HEALTH.ENABLED && Config.get( ).NORMAL_HEALTH.RECOVER_DELAY >= 0 ) {
			updateNormalHealth( );
		}
		if( Config.get( ).ABSORPTION_HEALTH.ENABLED && Config.get( ).ABSORPTION_HEALTH.RECOVER_DELAY >= 0 ) {
			updateAbsorptionHealth( );
		}
	}
	
	private void updateAbsorptionHealth( ) {
		// Update delay and determine amount to recover accordingly
		float recovered;
		if( getAbsorptionDelay( ) > 0 ) {
			if( getAbsorptionDelay( ) < Config.get( ).GENERAL.UPDATE_TIME ) {
				int ticksPastZero = Config.get( ).GENERAL.UPDATE_TIME - getAbsorptionDelay( );
				recovered = Config.get( ).ABSORPTION_HEALTH.RECOVER_RATE * ticksPastZero;
				setAbsorptionDelay( 0 );
			}
			else {
				reduceAbsorptionDelay( Config.get( ).GENERAL.UPDATE_TIME );
				return;
			}
		}
		else {
			recovered = Config.get( ).ABSORPTION_HEALTH.RECOVER_RATE * Config.get( ).GENERAL.UPDATE_TIME;
		}
		
		// Handle hunger cost restrictions
		if( owner.getFoodData( ).getFoodLevel( ) < Config.get( ).ABSORPTION_HEALTH.HUNGER_REQUIRED ) {
			return;
		}
		
		// Recover absorption health, if needed
		float effectiveCapacity = getEffectiveCapacity( );
		float currentAbsorption = owner.getAbsorptionAmount( );

		if( recovered > 0.0F && currentAbsorption < effectiveCapacity ) {
			// Apply armor toughness to recovery
			if( Config.get( ).ARMOR.REPLACE_ARMOR && Config.get( ).ARMOR.ARMOR_TOUGHNESS_RECOVERY > 0.0F ) {
				float toughness = Math.min( 20.0F, (float) owner.getAttributeValue( Attributes.ARMOR_TOUGHNESS ) );

				if( toughness > 0.0F ) {
					recovered *= 1.0F + toughness * Config.get( ).ARMOR.ARMOR_TOUGHNESS_RECOVERY;
				}
			}
			
			// Add absorption recovery
			
			float newAbsorption = Math.min( effectiveCapacity, currentAbsorption + recovered );
			owner.setAbsorptionAmount( newAbsorption );
			
			// consume hunger if needed
			if ( newAbsorption - currentAbsorption > 0 && Config.get( ).ABSORPTION_HEALTH.HUNGER_COST > 0 ) {
				owner.getFoodData( ).addExhaustion( ( newAbsorption - currentAbsorption ) * Config.get( ).ABSORPTION_HEALTH.HUNGER_COST );
			}
		}
	}
	
	private
	void updateNormalHealth( )
	{
		// Update delay and determine amount to recover accordingly
		float recovered;
		if( getNormalDelay( ) > 0 ) {
			if( getNormalDelay( ) < Config.get( ).GENERAL.UPDATE_TIME ) {
				int ticksPastZero = Config.get( ).GENERAL.UPDATE_TIME - getNormalDelay( );
				recovered = Config.get( ).NORMAL_HEALTH.RECOVER_RATE * ticksPastZero;
				setNormalDelay( 0 );
			}
			else {
				reduceNormalDelay( Config.get( ).GENERAL.UPDATE_TIME );
				return;
			}
		}
		else {
			recovered = Config.get( ).NORMAL_HEALTH.RECOVER_RATE * Config.get( ).GENERAL.UPDATE_TIME;
		}
		
		// Handle hunger cost restrictions
		if( owner.getFoodData( ).getFoodLevel( ) < Config.get( ).NORMAL_HEALTH.HUNGER_REQUIRED ) {
			return;
		}
		
		// Recover normal health, if needed
		float effectiveCapacity = Math.min( Config.get( ).NORMAL_HEALTH.MAXIMUM, owner.getMaxHealth( ) );
		float oldHealthAmount   = owner.getHealth( );
		if( recovered > 0.0F && oldHealthAmount < effectiveCapacity ) {
			float newHealthAmount = oldHealthAmount + recovered;
			if( newHealthAmount > effectiveCapacity ) {
				owner.setHealth( effectiveCapacity );
				owner.getFoodData( ).addExhaustion( (effectiveCapacity - oldHealthAmount) * Config.get( ).NORMAL_HEALTH.HUNGER_COST );
			}
			else {
				owner.setHealth( newHealthAmount );
				owner.getFoodData( ).addExhaustion( recovered * Config.get( ).NORMAL_HEALTH.HUNGER_COST );
			}
		}
	}
	
	private HealthData( PlayerEntity player ) {
		owner = player;
		saveTag = getHealthNBTTag( );
		
		if( saveTag.contains( TAG_CAPACITY_ABSORPTION, NBT_TAG_PRIMITIVE ) ) {
			setAbsorptionCapacity( saveTag.getFloat( TAG_CAPACITY_ABSORPTION ) );
		}
		else if( Config.get( ).ABSORPTION_HEALTH.ENABLED ) {
			// New player, give starting absorption
			setAbsorptionCapacity( Config.get( ).ABSORPTION_HEALTH.STARTING_AMOUNT );
			setAbsorptionHealth( getAbsorptionCapacity( ) );
		}
		
		if( saveTag.contains( TAG_DELAY_ABSORPTION, NBT_TAG_PRIMITIVE ) ) {
			delayAbsp = saveTag.getInt( TAG_DELAY_ABSORPTION );
		}
		else {
			setAbsorptionDelay( 0 );
		}
		if( saveTag.contains( TAG_DELAY_NORMAL, NBT_TAG_PRIMITIVE ) ) {
			delayNorm = saveTag.getInt( TAG_DELAY_NORMAL );
		}
		else {
			setNormalDelay( 0 );
		}
	}
	
	/** @return The nbt tag compound that holds all of this mod's data. */
	private CompoundNBT getHealthNBTTag( ) {
		// Get/make the persistent nbt tag (so we don't lose data on respawn)
		CompoundNBT tag = owner.getPersistentData( );

		if( !tag.contains( PlayerEntity.PERSISTED_NBT_TAG, tag.getId( ) ) ) {
			tag.put( PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT( ) );
		}
		tag = tag.getCompound( PlayerEntity.PERSISTED_NBT_TAG );
		
		// Get/make a tag unique to this mod
		if( !tag.contains( TAG_BASE, tag.getId( ) ) ) {
			tag.put( TAG_BASE, new CompoundNBT( ) );
		}
		return tag.getCompound( TAG_BASE );
	}
}
