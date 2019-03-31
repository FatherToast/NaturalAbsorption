package fathertoast.naturalabsorption.health;

import fathertoast.naturalabsorption.*;
import fathertoast.naturalabsorption.config.*;
import fathertoast.naturalabsorption.item.*;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public
class HealthManager
{
	// Set of all currently altered damage sources.
	private static final Set< DamageSource > MODDED_SOURCES = new HashSet<>( );
	
	// Returns true if the given damage source is modified to ignore armor.
	private static
	boolean isSourceModified( DamageSource source ) { return MODDED_SOURCES.contains( source ); }
	
	// Modifies a source to ignore armor.
	private static
	void modifySource( DamageSource source )
	{
		if( source.isUnblockable( ) )
			return;
		ObfuscationHelper.DamageSource_isUnblockable.set( source, true );
	}
	
	// Undoes any modification done to a damage source.
	private static
	void unmodifySource( DamageSource source )
	{
		ObfuscationHelper.DamageSource_isUnblockable.set( source, false );
		MODDED_SOURCES.remove( source );
	}
	
	// Undoes all modification done to all damage sources.
	@SuppressWarnings( "WeakerAccess" )
	public static
	void clearSources( )
	{
		for( DamageSource source : MODDED_SOURCES ) {
			ObfuscationHelper.DamageSource_isUnblockable.set( source, false );
		}
		MODDED_SOURCES.clear( );
	}
	
	/** @return The absorption capacity granted by equipment. */
	public static
	float getArmorAbsorption( EntityPlayer player )
	{
		float bonus = 0.0F;
		
		// From armor
		if( Config.get( ).ARMOR.REPLACE_ARMOR || Config.get( ).ARMOR.ARMOR_MULT_OVERRIDE ) {
			bonus += Config.get( ).ARMOR.ARMOR_MULT * player.getTotalArmorValue( );
		}
		
		// From enchantments
		if( Config.get( ).ENCHANTMENT.ENABLED ) {
			bonus += EnchantmentAbsorption.getBonusCapacity( player );
		}
		
		return bonus;
	}
	
	/** @return The absorption capacity granted by potion effects. */
	public static
	float getPotionAbsorption( EntityPlayer player )
	{
		PotionEffect absorptionPotion = player.getActivePotionEffect( MobEffects.ABSORPTION );
		if( absorptionPotion != null ) {
			return 4 * (absorptionPotion.getAmplifier( ) + 1);
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
	public
	void onServerTick( TickEvent.ServerTickEvent event )
	{
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
				WorldServer[] worlds = FMLCommonHandler.instance( ).getMinecraftServerInstance( ).worlds;
				for( WorldServer world : worlds ) {
					if( world != null ) {
						if( Config.get( ).NORMAL_HEALTH.ENABLED && Config.get( ).NORMAL_HEALTH.DISABLE_GAMERULE_REGEN ) {
							world.getGameRules( ).setOrCreateGameRule( "naturalRegeneration", "false" );
						}
						
						for( EntityPlayer player : new ArrayList<>( world.playerEntities ) ) {
							if( player != null && player.isEntityAlive( ) ) {
								HealthData.get( player ).update( );
							}
						}
					}
				}
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
	public
	void onItemUseFinish( LivingEntityUseItemEvent.Finish event )
	{
		if( event.getEntityLiving( ) instanceof EntityPlayer && !event.getEntityLiving( ).world.isRemote ) {
			if( Config.get( ).NORMAL_HEALTH.ENABLED && Config.get( ).NORMAL_HEALTH.FOOD_HEALING > 0.0F ) {
				// Apply healing from food
				ItemStack stack = event.getItem( );
				if( !stack.isEmpty( ) && stack.getItem( ) instanceof ItemFood ) {
					int hungerRestore = ((ItemFood) stack.getItem( )).getHealAmount( stack );
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
	public
	void onPlayerRespawn( PlayerEvent.PlayerRespawnEvent event )
	{
		if( !event.player.world.isRemote && !event.isEndConquered( ) ) {
			HealthData data = HealthData.get( event.player );
			
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
	public
	void onJoinWorld( EntityJoinWorldEvent event )
	{
		if( !event.getWorld( ).isRemote && event.getEntity( ) instanceof EntityPlayer ) {
			EntityPlayer player = (EntityPlayer) event.getEntity( );
			
			// An effort to fix the vanilla bug with absorption not updating properly
			float absorptionHealth = player.getAbsorptionAmount( );
			player.setAbsorptionAmount( absorptionHealth + 0.01F );
			player.setAbsorptionAmount( absorptionHealth );
			
			// Initialize the client's absorption capacity
			MessageCapacity.sendFor( player );
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
	public
	void onLivingHurt( LivingHurtEvent event )
	{
		if( event.getEntityLiving( ) instanceof EntityPlayer && !event.getEntityLiving( ).world.isRemote ) {
			HealthData data = HealthData.get( (EntityPlayer) event.getEntityLiving( ) );
			
			// Interrupt recovery
			data.startRecoveryDelay( );
			
			// Handle armor replacement, if enabled
			if( Config.get( ).ABSORPTION_HEALTH.ENABLED && Config.get( ).ARMOR.REPLACE_ARMOR ) {
				// Force damage to ignore armor
				if( !event.getSource( ).isUnblockable( ) ) {
					modifySource( event.getSource( ) );
				}
				// Degrade armor
				if( event.getAmount( ) > Config.get( ).ARMOR.DURABILITY_THRESHOLD &&
				    !event.getSource( ).canHarmInCreative( ) && !"thorns".equalsIgnoreCase( event.getSource( ).damageType ) ) {
					
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
	private
	boolean isSourceDamageOverTime( DamageSource source, float amount )
	{
		return source == DamageSource.MAGIC && amount <= 1.0F || // Poison damage
		       source == DamageSource.WITHER || source == DamageSource.ON_FIRE ||
		       source == DamageSource.IN_FIRE || source == DamageSource.LAVA ||
		       source == DamageSource.HOT_FLOOR || source == DamageSource.CACTUS ||
		       source == DamageSource.IN_WALL || source == DamageSource.CRAMMING ||
		       source == DamageSource.DROWN || source == DamageSource.STARVE;
	}
	
	// Used to degrade armor durability when armor damage reduction is disabled.
	private
	void damageArmor( LivingHurtEvent event )
	{
		EntityPlayer player = (EntityPlayer) event.getEntityLiving( );
		
		float durabilityDamage = event.getAmount( );
		
		// Only degrade armor based on damage dealt to absorption health
		if( Config.get( ).ARMOR.DURABILITY_FRIENDLY && durabilityDamage > player.getAbsorptionAmount( ) ) {
			durabilityDamage = player.getAbsorptionAmount( );
		}
		// Multiply degradation based on settings
		durabilityDamage *= Config.get( ).ARMOR.DURABILITY_MULT;
		
		// Degrade armor durability
		if( !event.getSource( ).canHarmInCreative( ) && durabilityDamage > 0.0F ) {
			player.inventory.damageArmor( durabilityDamage );
		}
	}
	
	public
	enum EnumDurabilityTrigger
	{
		ALL, HITS, NONE
	}
}
