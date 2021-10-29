package fathertoast.naturalabsorption.common.health;

import fathertoast.naturalabsorption.ObfuscationHelper;
import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.enchantment.AbsorptionEnchantment;
import fathertoast.naturalabsorption.common.network.NetworkHelper;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.HashSet;
import java.util.Set;

public class HeartManager {
    /** Set of all currently altered damage sources. */
    private static final Set<DamageSource> MODDED_SOURCES = new HashSet<>();
    
    /** @return True if health features in this mod are enabled. */
    public static boolean isHealthEnabled() { return !Config.MAIN.GENERAL.disableHealthFeatures.get(); }
    
    /** @return True if absorption features in this mod are enabled. */
    public static boolean isAbsorptionEnabled() { return !Config.MAIN.GENERAL.disableAbsorptionFeatures.get(); }
    
    /** @return True if armor replacement features in this mod are enabled. */
    public static boolean isArmorReplacementEnabled() { return Config.EQUIPMENT.ARMOR.enabled.get(); }
    
    public static float getNaturalAbsorption( PlayerEntity player ) {
        return HeartData.get( player ).getNaturalAbsorption();
    }
    
    /** Returns true if the given damage source is modified to ignore armor. */
    private static boolean isSourceModified( DamageSource source ) { return MODDED_SOURCES.contains( source ); }
    
    /** Modifies a source to ignore armor. */
    private static void modifySource( DamageSource source ) {
        if( source.isBypassInvul() ) return;
        ObfuscationHelper.DamageSource_isUnblockable.set( source, true );
    }
    
    /** Undoes any modification done to a damage source. */
    private static void restoreSource( DamageSource source ) {
        ObfuscationHelper.DamageSource_isUnblockable.set( source, false );
        MODDED_SOURCES.remove( source );
    }
    
    /** Undoes all modification done to all damage sources. */
    public static void clearSources() {
        for( DamageSource source : MODDED_SOURCES ) {
            ObfuscationHelper.DamageSource_isUnblockable.set( source, false );
        }
        MODDED_SOURCES.clear();
    }
    
    /** @return The max absorption granted by equipment. */
    public static float getEquipmentAbsorption( PlayerEntity player ) {
        float bonus = 0.0F;
        
        // From armor
        if( isArmorReplacementEnabled() ) {
            if( Config.EQUIPMENT.ARMOR.armorMultiplier.get() > 0.0 ) {
                final double armor = player.getAttributeValue( Attributes.ARMOR_TOUGHNESS );
                if( armor > 0.0F ) {
                    bonus += Config.EQUIPMENT.ARMOR.armorMultiplier.get() * armor;
                }
            }
            if( Config.EQUIPMENT.ARMOR.armorToughnessMultiplier.get() > 0.0 ) {
                final double toughness = player.getAttributeValue( Attributes.ARMOR_TOUGHNESS );
                if( toughness > 0.0F ) {
                    bonus += Config.EQUIPMENT.ARMOR.armorToughnessMultiplier.get() * toughness;
                }
            }
        }
        
        // From enchantments
        if( Config.EQUIPMENT.ENCHANTMENT.enabled.get() ) {
            bonus += AbsorptionEnchantment.getMaxAbsorptionBonus( player );
        }
        
        return bonus;
    }
    
    /** @return The max absorption granted by potion effects. */
    public static float getPotionAbsorption( PlayerEntity player ) {
        if( player.hasEffect( Effects.ABSORPTION ) ) {
            final EffectInstance absorptionPotion = player.getEffect( Effects.ABSORPTION );
            if( absorptionPotion != null )
                return 4.0F * (absorptionPotion.getAmplifier() + 1);
        }
        return 0.0F;
    }
    
    // The counter to the next cache clear.
    private int cleanupCounter = 0;
    // The counter to the next update.
    private int updateCounter = 0;
    
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
                clearSources();
                HeartData.clearCache();
            }
            
            // Counter for player shield update.
            if( ++updateCounter >= Config.MAIN.GENERAL.updateTime.get() ) {
                updateCounter = 0;
                
                // Maybe this is the right way to get the server instance
                final MinecraftServer server = LogicalSidedProvider.INSTANCE.get( LogicalSide.SERVER );
                for( ServerPlayerEntity player : server.getPlayerList().getPlayers() ) {
                    // Update each player's data
                    if( player != null && player.isAlive() ) HeartData.get( player ).update();
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
    public void onItemUseFinish( LivingEntityUseItemEvent.Finish event ) {
        if( event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().level.isClientSide ) {
            if( isHealthEnabled() && Config.HEALTH.GENERAL.foodHealingMax.get() != 0.0 ) {
                // Apply healing from food
                final ItemStack stack = event.getItem();
                if( !stack.isEmpty() && stack.getItem().getFoodProperties() != null ) {
                    // Ignore the max if setting is negative
                    final float maxHealing = Config.HEALTH.GENERAL.foodHealingMax.get() < 0.0 ? Float.POSITIVE_INFINITY :
                            (float) Config.HEALTH.GENERAL.foodHealingMax.get();
                    
                    // Calculate the food's hunger and saturation
                    //TODO Calculate actual hunger/saturation changes, try tracking players that are using items
                    final Food food = stack.getItem().getFoodProperties();
                    final int hunger = food.getNutrition();
                    final float saturation = hunger * food.getSaturationModifier() * 2.0F;
                    
                    // Apply any healing
                    float healing = 0.0F;
                    if( hunger > 0 ) {
                        healing += hunger * (float) Config.HEALTH.GENERAL.foodHealingPerHunger.get();
                    }
                    if( saturation > 0.0F ) {
                        healing += saturation * (float) Config.HEALTH.GENERAL.foodHealingPerSaturation.get();
                    }
                    if( healing > 0.0F ) {
                        event.getEntityLiving().heal( Math.min( healing, maxHealing ) );
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
        if( !event.getPlayer().level.isClientSide && !event.isEndConquered() ) {
            final HeartData data = HeartData.get( event.getPlayer() );
            
            // Apply death penalty
            if( isAbsorptionEnabled() && Config.ABSORPTION.NATURAL.deathPenalty.get() > 0.0 ) {
                final float naturalAbsorption = data.getNaturalAbsorption();
                if( naturalAbsorption > Config.ABSORPTION.NATURAL.deathPenaltyLimit.get() ) {
                    data.setNaturalAbsorption( (float) Math.max( Config.ABSORPTION.NATURAL.deathPenaltyLimit.get(),
                            naturalAbsorption - Config.ABSORPTION.NATURAL.deathPenalty.get() ) );
                }
            }
            
            // Prepare the player for respawn
            data.startRecoveryDelay();
            if( isHealthEnabled() && Config.HEALTH.GENERAL.respawnAmount.get() > 0.0F ) {
                data.owner.setHealth( (float) Config.HEALTH.GENERAL.respawnAmount.get() );
            }
            if( isAbsorptionEnabled() ) {
                data.setAbsorption( (float) Config.ABSORPTION.GENERAL.respawnAmount.get() );
            }
        }
    }
    
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onServerStarting( FMLServerStartingEvent event ) {
        // Disable the health regen game rule if needed
        if( Config.MAIN.GENERAL.disableRegenGameRule.get() ) {
            // Pretty sure the game rules are only stored in the overworld instance, but do this just in case for now
            for( ServerWorld world : event.getServer().getAllLevels() ) {
                final GameRules.BooleanValue naturalRegenRule = world.getGameRules().getRule( GameRules.RULE_NATURAL_REGENERATION );
                if( naturalRegenRule.get() ) naturalRegenRule.set( false, event.getServer() );
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
        if( !event.getWorld().isClientSide && event.getEntity() instanceof PlayerEntity ) {
            final PlayerEntity player = (PlayerEntity) event.getEntity();
            final float absorptionHealth = player.getAbsorptionAmount();
            
            // An effort to fix the vanilla bug with absorption not updating properly
            /* This bug appears to have been fixed; remove this for now.
            player.setAbsorptionAmount( absorptionHealth + 0.01F );
            player.setAbsorptionAmount( absorptionHealth );
            */
            
            // Initialize the client's absorption capacity
            NetworkHelper.setNaturalAbsorption( (ServerPlayerEntity) player, absorptionHealth );
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
        if( event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().level.isClientSide ) {
            HeartData data = HeartData.get( (PlayerEntity) event.getEntityLiving() );
            
            // Interrupt recovery
            data.startRecoveryDelay();
            
            // Handle armor replacement, if enabled
            if( isArmorReplacementEnabled() ) {
                // Force damage to ignore armor
                if( Config.EQUIPMENT.ARMOR.disableArmor.get() && !event.getSource().isBypassArmor() ) {
                    modifySource( event.getSource() );
                }
                // Degrade armor manually (armor ignoring damage otherwise won't)
                if( event.getAmount() > Config.EQUIPMENT.ARMOR.durabilityThreshold.get() &&
                        !event.getSource().isBypassInvul() && !"thorns".equalsIgnoreCase( event.getSource().getMsgId() ) ) {
                    
                    switch( Config.EQUIPMENT.ARMOR.durabilityTrigger.get() ) {
                        case NONE:
                            break;
                        case HITS:
                            if( isSourceDamageOverTime( event.getSource(), event.getAmount() ) ) break;
                        default:
                            damageArmor( event );
                    }
                }
            }
        }
        else if( isArmorReplacementEnabled() && isSourceModified( event.getSource() ) ) {
            // Restore the source's normal settings against non-players
            restoreSource( event.getSource() );
        }
    }
    
    // Determines whether a damage source is damage-over-time.
    private boolean isSourceDamageOverTime( DamageSource source, float amount ) {
        // Potion degeneration
        return source == DamageSource.MAGIC && amount <= 1.0F /* Hopefully poison damage */ || source == DamageSource.WITHER ||
                // Burning damage
                source == DamageSource.ON_FIRE || source == DamageSource.IN_FIRE ||
                source == DamageSource.LAVA || source == DamageSource.HOT_FLOOR ||
                // Damaging plants
                source == DamageSource.CACTUS || source == DamageSource.SWEET_BERRY_BUSH ||
                // Unfortunate states
                source == DamageSource.IN_WALL || source == DamageSource.CRAMMING ||
                source == DamageSource.DROWN || source == DamageSource.STARVE || source == DamageSource.DRY_OUT;
    }
    
    // Used to degrade armor durability when armor damage reduction is disabled.
    private void damageArmor( LivingHurtEvent event ) {
        final PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        
        float durabilityDamage = event.getAmount();
        
        // Only degrade armor based on damage dealt to absorption health
        if( Config.EQUIPMENT.ARMOR.durabilityFriendly.get() && durabilityDamage > player.getAbsorptionAmount() ) {
            durabilityDamage = player.getAbsorptionAmount();
        }
        // Multiply degradation based on settings
        durabilityDamage *= Config.EQUIPMENT.ARMOR.durabilityMultiplier.get();
        
        // Degrade armor durability
        if( !event.getSource().isBypassInvul() && durabilityDamage > 0.0F ) {
            player.inventory.hurtArmor( event.getSource(), durabilityDamage );
        }
    }
    
    public enum EnumDurabilityTrigger { ALL, HITS, NONE }
}