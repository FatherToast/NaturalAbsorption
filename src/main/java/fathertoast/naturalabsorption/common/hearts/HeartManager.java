package fathertoast.naturalabsorption.common.hearts;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.register.NAAttributes;
import fathertoast.naturalabsorption.common.enchantment.AbsorptionEnchantment;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HeartManager {
    /** Set of all currently altered damage sources. */
    private static final Set<DamageSource> MODDED_SOURCES = new HashSet<>();
    
    /** Map of all currently tracked players to their last known hunger state. */
    private static final HashMap<PlayerEntity, HungerState> PLAYER_HUNGER_STATE_TRACKER = new HashMap<>();
    
    /** @return True if health features in this mod are enabled. */
    public static boolean isHealthEnabled() { return !Config.MAIN.GENERAL.disableHealthFeatures.get(); }
    
    /** @return True if absorption features in this mod are enabled. */
    public static boolean isAbsorptionEnabled() { return !Config.MAIN.GENERAL.disableAbsorptionFeatures.get(); }
    
    /** @return True if armor replacement features in this mod are enabled. */
    public static boolean isArmorReplacementEnabled() { return Config.EQUIPMENT.ARMOR.enabled.get(); }
    
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
    
    private static void trackPlayerHungerState( PlayerEntity player ) {
        final HungerState hungerState = PLAYER_HUNGER_STATE_TRACKER.get( player );
        if( hungerState == null ) {
            PLAYER_HUNGER_STATE_TRACKER.put( player, new HungerState( player ) );
        }
        else {
            hungerState.update( player );
        }
    }
    
    private static void clearPlayerHungerState( PlayerEntity player ) { PLAYER_HUNGER_STATE_TRACKER.remove( player ); }
    
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
            final MinecraftServer server = LogicalSidedProvider.INSTANCE.get( LogicalSide.SERVER );
            
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
                
                for( ServerPlayerEntity player : server.getPlayerList().getPlayers() ) {
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
        if( event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().level.isClientSide ) {
            // Start watching hunger
            trackPlayerHungerState( (PlayerEntity) event.getEntityLiving() );
        }
    }
    
    /**
     * Called each tick while an item is in use.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onItemUseTick( LivingEntityUseItemEvent.Tick event ) {
        if( event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().level.isClientSide ) {
            // Update watched hunger, just in case anything changes mid-use
            trackPlayerHungerState( (PlayerEntity) event.getEntityLiving() );
        }
    }
    
    /**
     * Called when any item use is canceled (i.e., before reaching max use duration).
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onItemUseStop( LivingEntityUseItemEvent.Stop event ) {
        if( event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().level.isClientSide ) {
            // Stop watching hunger; item was not food or eating was canceled
            clearPlayerHungerState( (PlayerEntity) event.getEntityLiving() );
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
            final PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            if( isHealthEnabled() && Config.HEALTH.GENERAL.foodHealingMax.get() != 0.0 ) {
                // Apply healing from food
                final ItemStack stack = event.getItem();
                if( !stack.isEmpty() && stack.getItem().getFoodProperties() != null ) {
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
                        
                        final Food food = stack.getItem().getFoodProperties();
                        hunger = food.getNutrition();
                        saturation = calculateSaturation( hunger, food.getSaturationModifier() );
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
    
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onItemTooltip( ItemTooltipEvent event ) {
        final Food food = event.getItemStack().getItem().getFoodProperties();
        if( food != null ) {
            final int hunger = food.getNutrition();
            final float saturation = calculateSaturation( hunger, food.getSaturationModifier() );
            
            if( Config.MAIN.GENERAL.foodExtraTooltipInfo.get() ) {
                // Food nutrition values could theoretically be zero or negative, make sure we handle that
                if( hunger != 0 ) {
                    event.getToolTip().add( new TranslationTextComponent( (hunger > 0 ? TextFormatting.BLUE : TextFormatting.RED) +
                            References.translate( References.FOOD_HUNGER, String.format( "%+d", -hunger ) ).getString() ) );
                }
                if( saturation != 0.0F ) {
                    event.getToolTip().add( new TranslationTextComponent( (saturation > 0.0F ? TextFormatting.BLUE : TextFormatting.RED) +
                            References.translate( References.FOOD_SATURATION, (saturation > 0.0F ? "+" : "") + References.prettyToString( saturation ) ).getString() ) );
                }
            }
            if( isHealthEnabled() && Config.HEALTH.GENERAL.foodHealingExtraTooltipInfo.get() ) {
                // Calculate as if the food's entire nutritional value is used
                final float maxHealing = Config.HEALTH.GENERAL.foodHealingMax.get() < 0.0 ? Float.POSITIVE_INFINITY :
                        (float) Config.HEALTH.GENERAL.foodHealingMax.get();
                final float healing = Math.min( getFoodHealing( hunger, saturation ), maxHealing );
                if( healing > 0.0F ) {
                    event.getToolTip().add( new TranslationTextComponent( TextFormatting.BLUE + References.translate( References.FOOD_HEALTH, "+" + References.prettyToString( healing ) ).getString() ) );
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
        final PlayerEntity player = event.getPlayer();
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
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onPlayerClone( PlayerEvent.Clone event ) {
        final double originalAbsorption = AbsorptionHelper.getBaseNaturalAbsorption( event.getOriginal() );
        AbsorptionHelper.setBaseNaturalAbsorption( event.getPlayer(), false, originalAbsorption );
    }
    
    /**
     * Update equipment absorption when the player changes armor items.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onPlayerEquipmentChange( LivingEquipmentChangeEvent event ) {
        if( event.getEntityLiving() instanceof PlayerEntity ) {
            // TODO figure out why armor replacement doesn't update correctly
            AbsorptionHelper.updateEquipmentAbsorption( (PlayerEntity) event.getEntityLiving() );
        }
    }
    
    /**
     * Adds our absorption attributes to the player entity type.
     */
    public static void onEntityAttributeCreation( EntityAttributeCreationEvent event ) {
        AttributeModifierMap modifierMap = GlobalEntityTypeAttributes.getSupplier( EntityType.PLAYER );
        AttributeModifierMap.MutableAttribute builder = AttributeModifierMap.builder();
        
        builder.add( NAAttributes.NATURAL_ABSORPTION.get(), 0.0 );
        builder.add( NAAttributes.EQUIPMENT_ABSORPTION.get(), 0.0 );
        AttributeModifierMap newModifierMap = builder.build();
        Map<Attribute, ModifiableAttributeInstance> map = new HashMap<>();
        
        map.putAll( modifierMap.instances );
        map.putAll( newModifierMap.instances );
        modifierMap.instances = map;
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
    
    /** Used to track the current hunger and saturation level for a player of interest. */
    private static class HungerState {
        int food;
        float saturation;
        
        HungerState( PlayerEntity player ) { update( player ); }
        
        void update( PlayerEntity player ) {
            food = player.getFoodData().getFoodLevel();
            saturation = player.getFoodData().getSaturationLevel();
        }
    }
}