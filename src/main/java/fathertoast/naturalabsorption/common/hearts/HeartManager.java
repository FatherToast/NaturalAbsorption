package fathertoast.naturalabsorption.common.hearts;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.register.NAAttributes;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HeartManager {
    /** Map of all players that have had equipment changes since the last tick, linked to their previous max absorption. */
    private static final HashMap<Player, Double> PLAYER_EQUIPMENT_TRACKER = new HashMap<>();
    
    /** Set of all currently altered damage sources. */
    private static final Set<DamageSource> MODDED_SOURCES = new HashSet<>();
    
    /** Map of all currently tracked players to their last known hunger state. */
    private static final HashMap<Player, HungerState> PLAYER_HUNGER_STATE_TRACKER = new HashMap<>();
    
    /** @return True if health features in this mod are enabled. */
    public static boolean isHealthEnabled() { return !Config.MAIN.GENERAL.disableHealthFeatures.get(); }
    
    /** @return True if absorption features in this mod are enabled. */
    public static boolean isAbsorptionEnabled() { return !Config.MAIN.GENERAL.disableAbsorptionFeatures.get(); }
    
    /** @return True if armor replacement features in this mod are enabled. */
    public static boolean isArmorReplacementEnabled() { return Config.EQUIPMENT.ARMOR.enabled.get(); }
    
    /** Marks a player as having equipment changes and records their current max absorption. */
    private static void trackPlayerEquipmentChange( Player player ) {
        // This will not be called multiple times per tick for the same player under normal circumstances; however, in
        // case it does happen, we can safely ignore repeat calls since we only care about the initial & final states
        if( !PLAYER_EQUIPMENT_TRACKER.containsKey( player ) ) {
            PLAYER_EQUIPMENT_TRACKER.put( player, AbsorptionHelper.getMaxAbsorption( player ) );
        }
    }
    
    /** Updates all players' pending equipment absorption changes and updates their actual absorption as needed. */
    private static void applyPlayerEquipmentChanges() {
        if( PLAYER_EQUIPMENT_TRACKER.isEmpty() ) return;
        
        for( Map.Entry<Player, Double> entry : PLAYER_EQUIPMENT_TRACKER.entrySet() ) {
            AbsorptionHelper.updateEquipmentAbsorption( entry.getKey(), entry.getValue() );
        }
        PLAYER_EQUIPMENT_TRACKER.clear();
    }
    
    /** Returns true if the given damage source is modified to ignore armor. */
    private static boolean isSourceModified( DamageSource source ) { return MODDED_SOURCES.contains( source ); }
    
    /** Modifies a source to ignore armor. */
    private static void modifySource( DamageSource source ) {
        if( source.isBypassInvul() ) return;
        source.bypassArmor = true;
    }
    
    /** Undoes any modification done to a damage source. */
    private static void restoreSource( DamageSource source ) {
        source.bypassArmor = false;
        MODDED_SOURCES.remove( source );
    }
    
    /** Undoes all modification done to all damage sources. */
    public static void clearSources() {
        for( DamageSource source : MODDED_SOURCES ) {
            source.bypassArmor = false;
        }
        MODDED_SOURCES.clear();
    }
    
    /** Creates or updates a player's tracked hunger state. */
    private static void trackPlayerHungerState( Player player ) {
        final HungerState hungerState = PLAYER_HUNGER_STATE_TRACKER.get( player );
        if( hungerState == null ) {
            PLAYER_HUNGER_STATE_TRACKER.put( player, new HungerState( player ) );
        }
        else {
            hungerState.update( player );
        }
    }
    
    /** Deletes a player's tracked hunger state. */
    private static void clearPlayerHungerState( Player player ) { PLAYER_HUNGER_STATE_TRACKER.remove( player ); }
    
    /** @return The max absorption granted by potion effects. */
    public static float getPotionAbsorption( Player player ) {
        if( player.hasEffect( MobEffects.ABSORPTION ) ) {
            final MobEffectInstance absorptionPotion = player.getEffect( MobEffects.ABSORPTION );
            if( absorptionPotion != null )
                return 4.0F * (absorptionPotion.getAmplifier() + 1);
        }
        return 0.0F;
    }
    
    // The counter to the next cache clear.
    private int cleanupCounter = 0;
    // The counter to the next update.
    private int updateCounter = 0;
    
    private MinecraftServer server = null;
    
    /**
     * Called when the minecraft server is starting.
     * Here we retrieve the server instance for later use.
     */
    @SubscribeEvent
    public void onServerStart( ServerStartingEvent event ) {
        this.server = event.getServer();
    }
    
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
            
            // Apply queued events
            applyPlayerEquipmentChanges();
            
            // Counter for cache cleanup; very lazily do this every ~5.5 minutes
            if( ++cleanupCounter >= 6666 ) {
                cleanupCounter = 0;
                PLAYER_HUNGER_STATE_TRACKER.clear();
                clearSources();
                HeartData.clearCache();
            }
            
            // Counter for player heart update
            if( ++updateCounter >= Config.MAIN.GENERAL.updateTime.get() ) {
                updateCounter = 0;
                
                for( ServerPlayer player : server.getPlayerList().getPlayers() ) {
                    // Update each player's heart data
                    if( player != null && player.isAlive() ) HeartData.get( player ).update();
                }
            }
        }
    }
    
    /**
     * Called when any item use is started.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onItemUseStart( LivingEntityUseItemEvent.Start event ) {
        if( event.getEntity() instanceof Player player && !event.getEntity().level.isClientSide ) {
            // Start watching hunger
            trackPlayerHungerState( player );
        }
    }
    
    /**
     * Called each tick while an item is in use.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onItemUseTick( LivingEntityUseItemEvent.Tick event ) {
        if( event.getEntity() instanceof Player player && !event.getEntity().level.isClientSide ) {
            // Update watched hunger, just in case anything changes mid-use
            trackPlayerHungerState( player );
        }
    }
    
    /**
     * Called when any item use is canceled (i.e., before reaching max use duration).
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onItemUseStop( LivingEntityUseItemEvent.Stop event ) {
        if( event.getEntity() instanceof Player player && !event.getEntity().level.isClientSide ) {
            // Stop watching hunger; item was not food or eating was canceled
            clearPlayerHungerState( player );
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
        if( event.getEntity() instanceof Player player && !event.getEntity().level.isClientSide ) {
            if( isHealthEnabled() && Config.HEALTH.GENERAL.foodHealingMax.get() != 0.0 ) {
                // Apply healing from food
                final ItemStack stack = event.getItem();
                if( !stack.isEmpty() && stack.getItem().getFoodProperties( stack, player ) != null ) {
                    // Ignore the max if setting is negative
                    final float maxHealing = Config.HEALTH.GENERAL.foodHealingMax.get() < 0.0 ? Float.POSITIVE_INFINITY :
                            (float) Config.HEALTH.GENERAL.foodHealingMax.get();
                    
                    // Calculate the food's hunger and saturation
                    final int hunger;
                    final float saturation;
                    final HungerState hungerState = PLAYER_HUNGER_STATE_TRACKER.get( player );
                    if( hungerState == null ) {
                        // Fall back to the old method of direct food potential :(
                        NaturalAbsorption.LOG.warn( "Failed to calculate actual hunger/saturation gained from eating! Item:[{}]",
                                stack.toString() );
                        
                        final FoodProperties food = stack.getItem().getFoodProperties( stack, player );
                        if( food == null ) {
                            hunger = 0;
                            saturation = 0.0F;
                        }
                        else {
                            hunger = food.getNutrition();
                            saturation = calculateSaturation( hunger, food.getSaturationModifier() );
                        }
                    }
                    else {
                        // Calculate actual hunger and saturation gained
                        hunger = player.getFoodData().getFoodLevel() - hungerState.food;
                        saturation = player.getFoodData().getSaturationLevel() - hungerState.saturation;
                        
                        // Stop watching hunger
                        clearPlayerHungerState( player );
                    }
                    
                    // Apply any healing
                    final float healing = getFoodHealing( hunger, saturation );
                    if( healing > 0.0F ) {
                        player.heal( Math.min( healing, maxHealing ) );
                    }
                }
            }
        }
    }
    
    /**
     * Called when populating tooltip contents. Also called on client start with a null player for search indexing.
     * <p>
     * Adds extra tooltip info to food items, if enabled.
     *
     * @param event The event data.
     */
    @OnlyIn( Dist.CLIENT )
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onItemTooltip( ItemTooltipEvent event ) {
        final FoodProperties food = event.getItemStack().getItem().getFoodProperties( event.getItemStack(), event.getEntity() );
        
        if( food != null ) {
            final int hunger = food.getNutrition();
            final float saturation = calculateSaturation( hunger, food.getSaturationModifier() );
            
            if( Config.MAIN.GENERAL.foodExtraTooltipInfo.get() ) {
                // Food nutrition values could theoretically be zero or negative, make sure we handle that
                if( hunger != 0 ) {
                    event.getToolTip().add( Component.translatable( (hunger > 0 ? ChatFormatting.BLUE : ChatFormatting.RED) +
                            References.translate( References.FOOD_HUNGER, String.format( "%+d", -hunger ) ).getString() ) );
                }
                if( saturation != 0.0F ) {
                    event.getToolTip().add( Component.translatable( (saturation > 0.0F ? ChatFormatting.BLUE : ChatFormatting.RED) +
                            References.translate( References.FOOD_SATURATION, (saturation > 0.0F ? "+" : "") + References.prettyToString( saturation ) ).getString() ) );
                }
            }
            if( isHealthEnabled() && Config.HEALTH.GENERAL.foodHealingExtraTooltipInfo.get() ) {
                // Calculate as if the food's entire nutritional value is used
                final float maxHealing = Config.HEALTH.GENERAL.foodHealingMax.get() < 0.0 ? Float.POSITIVE_INFINITY :
                        (float) Config.HEALTH.GENERAL.foodHealingMax.get();
                final float healing = Math.min( getFoodHealing( hunger, saturation ), maxHealing );
                if( healing > 0.0F ) {
                    event.getToolTip().add( Component.translatable( ChatFormatting.BLUE + References.translate( References.FOOD_HEALTH, "+" + References.prettyToString( healing ) ).getString() ) );
                }
            }
        }
    }
    
    /** Calculates saturation value based on hunger and saturation modifier. */
    private static float calculateSaturation( int hunger, float saturationModifier ) { return hunger * saturationModifier * 2.0F; }
    
    /** Calculates amount of healing to provide based on hunger and saturation granted. */
    private static float getFoodHealing( int hunger, float saturation ) {
        float healing = 0.0F;
        if( hunger > 0 ) {
            healing += hunger * (float) Config.HEALTH.GENERAL.foodHealingPerHunger.get();
        }
        if( saturation > 0.0F ) {
            healing += saturation * (float) Config.HEALTH.GENERAL.foodHealingPerSaturation.get();
        }
        return healing;
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
        final Player player = event.getEntity();
        if( !player.level.isClientSide && !event.isEndConquered() ) {
            // Apply death penalty
            AbsorptionHelper.applyDeathPenalty( player );
            
            // Prepare the player for respawn
            final HeartData data = HeartData.get( player );
            data.startRecoveryDelay();
            if( isHealthEnabled() && Config.HEALTH.GENERAL.respawnAmount.get() > 0.0F ) {
                data.owner.setHealth( (float) Config.HEALTH.GENERAL.respawnAmount.get() );
            }
            if( isAbsorptionEnabled() ) {
                data.setAbsorption( (float) Config.ABSORPTION.GENERAL.respawnAmount.get() );
            }
        }
    }
    
    /**
     * Ensure the base natural absorption modifier value is copied over to the new player entity.
     */
    @SubscribeEvent( priority = EventPriority.HIGHEST )
    public void onPlayerClone( PlayerEvent.Clone event ) {
        final double originalAbsorption = AbsorptionHelper.getBaseNaturalAbsorption( event.getOriginal() );
        AbsorptionHelper.setBaseNaturalAbsorption( event.getEntity(), false, originalAbsorption );
    }
    
    /**
     * Update equipment absorption when the player changes armor items.
     */
    @SubscribeEvent( priority = EventPriority.HIGHEST )
    public void onPlayerEquipmentChange( LivingEquipmentChangeEvent event ) {
        if( event.getEntity() instanceof Player player ) {
            trackPlayerEquipmentChange( player );
        }
    }
    
    /**
     * Adds our absorption attributes to the player entity type. Individually registered to the mod event bus.
     */
    public static void onEntityAttributeCreation( @SuppressWarnings( "unused" ) EntityAttributeCreationEvent event ) {
        AttributeSupplier attributeSupplier = DefaultAttributes.getSupplier( EntityType.PLAYER );
        AttributeSupplier.Builder builder = AttributeSupplier.builder();
        
        builder.add( NAAttributes.NATURAL_ABSORPTION.get(), 0.0 );
        builder.add( NAAttributes.EQUIPMENT_ABSORPTION.get(), 0.0 );
        AttributeSupplier newAttributeSupplier = builder.build();
        Map<Attribute, AttributeInstance> map = new HashMap<>();
        
        map.putAll( attributeSupplier.instances );
        map.putAll( newAttributeSupplier.instances );
        attributeSupplier.instances = map;
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
        if( event.getEntity() instanceof Player player && !event.getEntity().level.isClientSide ) {
            HeartData data = HeartData.get( player );
            
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
        final Player player = (Player) event.getEntity();
        
        float durabilityDamage = event.getAmount();
        
        // Only degrade armor based on damage dealt to absorption health
        if( Config.EQUIPMENT.ARMOR.durabilityFriendly.get() && durabilityDamage > player.getAbsorptionAmount() ) {
            durabilityDamage = player.getAbsorptionAmount();
        }
        // Multiply degradation based on settings
        durabilityDamage *= Config.EQUIPMENT.ARMOR.durabilityMultiplier.get();
        
        // Degrade armor durability
        if( !event.getSource().isBypassInvul() && durabilityDamage > 0.0F ) {
            player.getInventory().hurtArmor( event.getSource(), durabilityDamage, Inventory.ALL_ARMOR_SLOTS );
        }
    }
    
    public enum EnumDurabilityTrigger { ALL, HITS, NONE }
    
    /** Used to track the current hunger and saturation level for a player of interest. */
    private static class HungerState {
        int food;
        float saturation;
        
        HungerState( Player player ) { update( player ); }
        
        void update( Player player ) {
            food = player.getFoodData().getFoodLevel();
            saturation = player.getFoodData().getSaturationLevel();
        }
    }
}