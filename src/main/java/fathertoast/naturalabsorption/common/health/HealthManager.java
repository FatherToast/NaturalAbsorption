package fathertoast.naturalabsorption.common.health;

import fathertoast.naturalabsorption.ObfuscationHelper;
import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.enchantment.AbsorptionEnchantment;
import fathertoast.naturalabsorption.common.network.NetworkHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public class HealthManager {

	/** Set of all currently altered damage sources. */
	private static final Set< DamageSource > MODDED_SOURCES = new HashSet<>( );

	public static float getAbsorptionCapacity( PlayerEntity player ) {
		return HealthData.get( player ).getAbsorptionCapacity( );
	}
	
	/** Returns true if the given damage source is modified to ignore armor. */
	private static boolean isSourceModified( DamageSource source ) { return MODDED_SOURCES.contains( source ); }
	
	/** Modifies a source to ignore armor. */
	private static void modifySource( DamageSource source ) {
		if( source.isBypassInvul( ) )
			return;
		ObfuscationHelper.DamageSource_isUnblockable.set( source, true );
	}
	
	/** Undoes any modification done to a damage source. */
	private static void unmodifySource( DamageSource source ) {
		ObfuscationHelper.DamageSource_isUnblockable.set( source, false );
		MODDED_SOURCES.remove( source );
	}
	
	/** Undoes all modification done to all damage sources. */
	@SuppressWarnings( "WeakerAccess" )
	public static void clearSources( ) {
		for( DamageSource source : MODDED_SOURCES ) {
			ObfuscationHelper.DamageSource_isUnblockable.set( source, false );
		}
		MODDED_SOURCES.clear( );
	}
	
	/** @return The absorption capacity granted by equipment. */
	public static float getArmorAbsorption( PlayerEntity player ) {
		float bonus = 0.0F;
		
		// From armor
		if( Config.get( ).ARMOR.REPLACE_ARMOR || Config.get( ).ARMOR.ARMOR_MULT_OVERRIDE ) {
			bonus += Config.get( ).ARMOR.ARMOR_MULT * player.getArmorCoverPercentage( );
		}
		
		// From enchantments
		if( Config.get( ).ENCHANTMENT.ENABLED ) {
			bonus += AbsorptionEnchantment.getBonusCapacity( player );
		}
		
		return bonus;
	}
	
	/** @return The absorption capacity granted by potion effects. */
	public static float getPotionAbsorption( PlayerEntity player ) {
		if ( player.hasEffect( Effects.ABSORPTION ) ) {
			EffectInstance absorptionPotion = player.getEffect( Effects.ABSORPTION );
			return 4 * ( absorptionPotion.getAmplifier( ) + 1 );
		}
		return 0.0F;
	}
	
	// The counter to the next cache clear.
	private int cleanupCounter = 0;
	// The counter to the next update.
	private int updateCounter  = 0;
	
	/**
	 * Called each server tick.
	 * <p>
	 * Updates health recovery.
	 *
	 * @param event The event data.
	 */
	@SubscribeEvent( priority = EventPriority.NORMAL )
	public void onServerTick( TickEvent.ServerTickEvent event ) {
		if( event.phase == TickEvent.Phase.END ) {
			// Counter for cache cleanup.
			if( ++cleanupCounter >= 600 ) {
				cleanupCounter = 0;
				clearSources( );
				HealthData.clearCache( );
			}
			
			// Counter for player shield update.
			if( ++updateCounter >= Config.get( ).GENERAL.UPDATE_TIME ) {
				updateCounter = 0;

				// TODO
				/*
				ServerWorld[] worlds = something.getAllServerWorlds();
				for( ServerWorld world : worlds ) {
					if( world != null ) {
						if( Config.get( ).NORMAL_HEALTH.ENABLED && Config.get( ).NORMAL_HEALTH.DISABLE_GAMERULE_REGEN ) {
							world.getGameRules( ).setOrCreateGameRule( "naturalRegeneration", "false" );
						}
						
						for( PlayerEntity player : new ArrayList<>( world.playerEntities ) ) {
							if( player != null && player.isEntityAlive( ) ) {
								HealthData.get( player ).update( );
							}
						}
					}
				}

				 */
			}
		}
	}
	
	/**
	 * Called when any item use is completed.
	 * <p>
	 * Applies healing from food, if enabled.
	 *
	 * @param event The event data.
	 */
	@SubscribeEvent( priority = EventPriority.NORMAL )
	public void onItemUseFinish( LivingEntityUseItemEvent.Finish event ) {
		if( event.getEntityLiving( ) instanceof PlayerEntity && !event.getEntityLiving( ).level.isClientSide ) {
			if( Config.get( ).NORMAL_HEALTH.ENABLED && Config.get( ).NORMAL_HEALTH.FOOD_HEALING > 0.0F ) {
				// Apply healing from food
				ItemStack stack = event.getItem( );
				if( !stack.isEmpty( ) && stack.getItem( ).getFoodProperties( ) != null ) {
					int hungerRestore = stack.getItem( ).getFoodProperties( ).getNutrition( );

					if( hungerRestore > 0 ) {
						event.getEntityLiving( ).heal( hungerRestore * Config.get( ).NORMAL_HEALTH.FOOD_HEALING );
					}
				}
			}
		}
	}
	
	/**
	 * Called when a player is respawned into the world, either by death or certain dimension transitions.
	 * <p>
	 * Applies death penalty and resets health values if the player was dead.
	 *
	 * @param event The event data.
	 */
	@SubscribeEvent( priority = EventPriority.NORMAL )
	public void onPlayerRespawn( PlayerEvent.PlayerRespawnEvent event ) {
		if( !event.getPlayer().level.isClientSide && !event.isEndConquered( ) ) {
			HealthData data = HealthData.get( event.getPlayer( ) );
			
			// Apply death penalty
			if( Config.get( ).ABSORPTION_HEALTH.ENABLED && Config.get( ).ABSORPTION_HEALTH.DEATH_PENALTY > 0.0F && data.getAbsorptionCapacity( ) > Config.get( ).ABSORPTION_HEALTH.DEATH_PENALTY_LIMIT ) {
				float newCapacity = data.getAbsorptionCapacity( ) - Config.get( ).ABSORPTION_HEALTH.DEATH_PENALTY;
				if( newCapacity < Config.get( ).ABSORPTION_HEALTH.DEATH_PENALTY_LIMIT ) {
					data.setAbsorptionCapacity( Config.get( ).ABSORPTION_HEALTH.DEATH_PENALTY_LIMIT );
				}
				else {
					data.setAbsorptionCapacity( newCapacity );
				}
			}
			
			// Prepare the player for respawn
			data.startRecoveryDelay( );
			if( Config.get( ).NORMAL_HEALTH.ENABLED && Config.get( ).NORMAL_HEALTH.RECOVERY_ON_RESPAWN > 0.0F ) {
				data.owner.setHealth( Config.get( ).NORMAL_HEALTH.RECOVERY_ON_RESPAWN );
			}
			if( Config.get( ).ABSORPTION_HEALTH.ENABLED ) {
				data.setAbsorptionHealth( Config.get( ).ABSORPTION_HEALTH.RECOVERY_ON_RESPAWN );
			}
		}
	}
	
	/**
	 * Called when any entity is spawned in the world, including by chunk loading and dimension transition.
	 * <p>
	 * Initializes important information for client players.
	 *
	 * @param event The event data.
	 */
	@SubscribeEvent( priority = EventPriority.NORMAL )
	public void onJoinWorld( EntityJoinWorldEvent event ) {
		if( !event.getWorld( ).isClientSide && event.getEntity( ) instanceof PlayerEntity ) {
			PlayerEntity player = (PlayerEntity) event.getEntity( );
			
			// An effort to fix the vanilla bug with absorption not updating properly
			float absorptionHealth = player.getAbsorptionAmount( );
			player.setAbsorptionAmount( absorptionHealth + 0.01F );
			player.setAbsorptionAmount( absorptionHealth );
			
			// Initialize the client's absorption capacity
			NetworkHelper.setAbsorptionCapacity( (ServerPlayerEntity) player, absorptionHealth );
		}
	}
	
	/**
	 * Called when a living entity is damaged - before armor, potions, and enchantments reduce damage.
	 * <p>
	 * Disrupts health recovery and prevents armor from reducing damage when armor is replaced.
	 *
	 * @param event The event data.
	 */
	@SubscribeEvent( priority = EventPriority.LOWEST )
	public void onLivingHurt( LivingHurtEvent event ) {
		if( event.getEntityLiving( ) instanceof PlayerEntity && !event.getEntityLiving( ).level.isClientSide ) {
			HealthData data = HealthData.get( (PlayerEntity) event.getEntityLiving( ) );
			
			// Interrupt recovery
			data.startRecoveryDelay( );
			
			// Handle armor replacement, if enabled
			if( Config.get( ).ABSORPTION_HEALTH.ENABLED && Config.get( ).ARMOR.REPLACE_ARMOR ) {
				// Force damage to ignore armor
				if( !event.getSource( ).isBypassArmor( ) ) {
					modifySource( event.getSource( ) );
				}
				// Degrade armor
				if( event.getAmount( ) > Config.get( ).ARMOR.DURABILITY_THRESHOLD &&
				    !event.getSource( ).isBypassInvul( ) && !"thorns".equalsIgnoreCase( event.getSource( ).getMsgId( ) ) ) {
					
					switch( Config.get( ).ARMOR.DURABILITY_TRIGGER ) {
						case NONE:
							break;
						case HITS:
							if( isSourceDamageOverTime( event.getSource( ), event.getAmount( ) ) ) {
								break;
							}
						default:
							damageArmor( event );
					}
				}
			}
		}
		else if( Config.get( ).ARMOR.REPLACE_ARMOR && isSourceModified( event.getSource( ) ) ) {
			// Restore the source's normal settings against non-players
			unmodifySource( event.getSource( ) );
		}
	}
	
	// Determines whether a damage source is damage-over-time.
	private boolean isSourceDamageOverTime( DamageSource source, float amount ) {
		return source == DamageSource.MAGIC && amount <= 1.0F || // Poison damage
		       source == DamageSource.WITHER || source == DamageSource.ON_FIRE ||
		       source == DamageSource.IN_FIRE || source == DamageSource.LAVA ||
		       source == DamageSource.HOT_FLOOR || source == DamageSource.CACTUS ||
		       source == DamageSource.IN_WALL || source == DamageSource.CRAMMING ||
		       source == DamageSource.DROWN || source == DamageSource.STARVE;
	}
	
	// Used to degrade armor durability when armor damage reduction is disabled.
	private void damageArmor( LivingHurtEvent event ) {
		PlayerEntity player = (PlayerEntity) event.getEntityLiving( );
		
		float durabilityDamage = event.getAmount( );
		
		// Only degrade armor based on damage dealt to absorption health
		if( Config.get( ).ARMOR.DURABILITY_FRIENDLY && durabilityDamage > player.getAbsorptionAmount( ) ) {
			durabilityDamage = player.getAbsorptionAmount( );
		}
		// Multiply degradation based on settings
		durabilityDamage *= Config.get( ).ARMOR.DURABILITY_MULT;
		
		// Degrade armor durability
		if( !event.getSource( ).isBypassInvul( ) && durabilityDamage > 0.0F ) {
			player.inventory.hurtArmor( event.getSource(), durabilityDamage );
		}
	}
	
	public
	enum EnumDurabilityTrigger
	{
		ALL, HITS, NONE
	}
}
