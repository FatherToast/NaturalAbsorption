package fathertoast.naturalabsorption.common.core.hearts;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.config.Config;
import fathertoast.naturalabsorption.common.core.register.NAAttributes;
import fathertoast.naturalabsorption.common.enchantment.AbsorptionEnchantment;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class AbsorptionHelper {
    
    /** @return The entity's max absorption, from all sources combined. In other words, the actual limit on absorption recovery. */
    public static double getMaxAbsorption( LivingEntity entity ) {
        return getSteadyStateMaxAbsorption( entity ) + HeartManager.getPotionAbsorption( entity );
    }
    
    /** @return The player's max absorption not counting buffs, limited by the global max absorption config. */
    public static double getSteadyStateMaxAbsorption( LivingEntity entity ) {
        final double naturalAbsorption = entity instanceof Player player ? getNaturalAbsorption( player ) : 0.0;
        final double calculatedMax = naturalAbsorption + getEquipmentAbsorption( entity );
        return Config.ABSORPTION.GENERAL.globalMax.get() < 0.0 ? calculatedMax :
                Math.min( calculatedMax, Config.ABSORPTION.GENERAL.globalMax.get() );
    }
    
    /** @return The entity's max absorption granted by natural absorption. */
    public static double getNaturalAbsorption( LivingEntity entity ) {
        return entity.getAttributeValue( NAAttributes.NATURAL_ABSORPTION.get() );
    }
    
    /** @return True if the entity's base natural absorption has been initialized. */
    public static boolean isBaseNaturalAbsorptionInitialized( LivingEntity entity ) {
        return hasAbsorptionModifier( entity, true, NATURAL_MODIFIER_BASE );
    }
    
    /** @return The entity's natural absorption, ignoring all attribute modifiers. */
    public static double getBaseNaturalAbsorption( LivingEntity entity ) {
        return getAbsorptionModifier( entity, true, NATURAL_MODIFIER_BASE );
    }
    
    /** Sets base natural absorption, clamped in a valid range, optionally reducing actual absorption as needed. */
    public static void setBaseNaturalAbsorption( LivingEntity entity, boolean updateActualAbsorption, double value ) {
        if( HeartManager.isAbsorptionEnabled() ) {
            final double initialValue = updateActualAbsorption ? getNaturalAbsorption( entity ) : 0.0;
            
            setAbsorptionModifier( entity, true, NATURAL_MODIFIER_BASE,
                    Mth.clamp( value, 0.0, Config.ABSORPTION.NATURAL.maximumAmount.get() ) );
            
            if( updateActualAbsorption ) {
                final double finalValue = getNaturalAbsorption( entity );
                if( initialValue > finalValue ) {
                    final double netChange = finalValue - initialValue;
                    entity.setAbsorptionAmount( entity.getAbsorptionAmount() + (float) netChange );
                }
            }
        }
    }
    
    /** Adds (or removes) base natural absorption, clamped in a valid range, optionally reducing actual absorption as needed. */
    public static void addBaseNaturalAbsorption( LivingEntity entity, boolean updateActualAbsorption, double value ) {
        setBaseNaturalAbsorption( entity, updateActualAbsorption, getBaseNaturalAbsorption( entity ) + value );
    }
    
    /** Removes base natural absorption equal to the death penalty, down to a limit, reducing actual absorption to match. */
    public static void applyDeathPenalty( Player player ) {
        if( HeartManager.isAbsorptionEnabled() && Config.ABSORPTION.NATURAL.deathPenalty.get() > 0.0 ) {
            final double initialValue = getBaseNaturalAbsorption( player );
            if( initialValue > Config.ABSORPTION.NATURAL.deathPenaltyLimit.get() ) {
                setBaseNaturalAbsorption( player, true,
                        Math.max( initialValue - Config.ABSORPTION.NATURAL.deathPenalty.get(), Config.ABSORPTION.NATURAL.deathPenaltyLimit.get() ) );
            }
        }
    }
    
    /** @return The entity's max absorption granted by equipment. That is, how much the entity would lose by unequipping everything. */
    public static double getEquipmentAbsorption( LivingEntity entity ) {
        return entity.getAttributeValue( NAAttributes.EQUIPMENT_ABSORPTION.get() );
    }
    
    /** @return The entity's equipment absorption from enchantments, ignoring all attribute modifiers. */
    public static double getEnchantmentAbsorption( LivingEntity entity ) {
        return getAbsorptionModifier( entity, false, EQUIP_MODIFIER_ENCHANT );
    }
    
    /** @return The entity's equipment absorption from armor replacement, ignoring all attribute modifiers. */
    public static double getArmorReplacementAbsorption( LivingEntity entity ) {
        return getAbsorptionModifier( entity, false, EQUIP_MODIFIER_ARMOR_REPLACE );
    }
    
    /** Recalculates and reapplies all equipment absorption modifiers. */
    public static void updateEquipmentAbsorption( LivingEntity entity, double previousMaxAbsorb ) {
        if( HeartManager.isAbsorptionEnabled() ) {
            setAbsorptionModifier( entity, false, EQUIP_MODIFIER_ENCHANT, Config.EQUIPMENT.ENCHANTMENT.enabled.get() ?
                    AbsorptionEnchantment.getMaxAbsorptionBonus( entity ) : 0.0 );
            setAbsorptionModifier( entity, false, EQUIP_MODIFIER_ARMOR_REPLACE, HeartManager.isArmorReplacementEnabled() ?
                    getArmorReplacementBonus( entity ) : 0.0 );
            
            final double finalMaxAbsorb = getMaxAbsorption( entity );
            if( previousMaxAbsorb > finalMaxAbsorb ) {
                final double netChange = finalMaxAbsorb - previousMaxAbsorb;
                entity.setAbsorptionAmount( entity.getAbsorptionAmount() + (float) netChange );
            }
        }
    }
    
    /** @return The maximum absorption granted by armor replacement. */
    private static double getArmorReplacementBonus( LivingEntity entity ) {
        double bonus = 0.0;
        if( Config.EQUIPMENT.ARMOR.armorMultiplier.get() > 0.0 ) {
            final double armor = entity.getAttributeValue( Attributes.ARMOR );
            if( armor > 0.0F ) {
                bonus += Config.EQUIPMENT.ARMOR.armorMultiplier.get() * armor;
            }
        }
        if( Config.EQUIPMENT.ARMOR.armorToughnessMultiplier.get() > 0.0 ) {
            final double toughness = entity.getAttributeValue( Attributes.ARMOR_TOUGHNESS );
            if( toughness > 0.0F ) {
                bonus += Config.EQUIPMENT.ARMOR.armorToughnessMultiplier.get() * toughness;
            }
        }
        return bonus;
    }
    
    private static final AttributeModifier NATURAL_MODIFIER_BASE = new AttributeModifier(
            UUID.fromString( "16c3f14f-e0cb-4360-9fb2-3bf20aaf9dc2" ),
            "Natural absorption base", 0.0, AttributeModifier.Operation.ADDITION );
    
    private static final AttributeModifier EQUIP_MODIFIER_ENCHANT = new AttributeModifier(
            UUID.fromString( "a6a0e621-2ca3-4606-81b3-0cd17308262c" ),
            "Equipment absorption from enchantments", 0.0, AttributeModifier.Operation.ADDITION );
    
    private static final AttributeModifier EQUIP_MODIFIER_ARMOR_REPLACE = new AttributeModifier(
            UUID.fromString( "447dbb9f-2995-45c6-a1be-c65d26328afc" ),
            "Equipment absorption from armor replacement", 0.0, AttributeModifier.Operation.ADDITION );
    
    /** Helper method for checking existence of absorption attribute modifiers. */
    @SuppressWarnings( "SameParameterValue" )
    private static boolean hasAbsorptionModifier( LivingEntity entity, boolean natural, AttributeModifier staticModifier ) {
        final Attribute attribute = natural ? NAAttributes.NATURAL_ABSORPTION.get() : NAAttributes.EQUIPMENT_ABSORPTION.get();
        final AttributeInstance instance = entity.getAttribute( attribute );
        return instance != null && instance.getModifier( staticModifier.getId() ) != null;
    }
    
    /** Helper method for reading absorption attribute modifier values. */
    private static double getAbsorptionModifier( LivingEntity entity, boolean natural, AttributeModifier staticModifier ) {
        final Attribute attribute = natural ? NAAttributes.NATURAL_ABSORPTION.get() : NAAttributes.EQUIPMENT_ABSORPTION.get();
        final AttributeInstance instance = entity.getAttribute( attribute );
        if( instance != null ) {
            final AttributeModifier modifier = instance.getModifier( staticModifier.getId() );
            if( modifier != null ) return modifier.getAmount();
        }
        return 0.0;
    }
    
    /** Helper method for writing absorption attribute modifier values. */
    private static void setAbsorptionModifier( LivingEntity entity, boolean natural, AttributeModifier staticModifier, double value ) {
        final Attribute attribute = natural ? NAAttributes.NATURAL_ABSORPTION.get() : NAAttributes.EQUIPMENT_ABSORPTION.get();
        final AttributeInstance instance = entity.getAttribute( attribute );
        if( instance == null ) {
            NaturalAbsorption.LOG.error( "Entity '{}' does not have '{}' registered!",
                    entity.getScoreboardName(), attribute.getDescriptionId() );
            return;
        }
        
        // If the modifier already exists, remove it
        final AttributeModifier oldModifier = instance.getModifier( staticModifier.getId() );
        if( oldModifier != null ) {
            if( oldModifier.getAmount() == value ) return; // No change
            instance.removeModifier( oldModifier );
        }
        
        // Apply the new value by using the static modifier as a template
        final AttributeModifier newModifier = new AttributeModifier( staticModifier.getId(),
                staticModifier.getName(), value, staticModifier.getOperation() );
        // Natural modifiers are permanent, equipment modifiers are derived from the equipment
        if( natural ) {
            instance.addPermanentModifier( newModifier );
        }
        else if( value != 0.0 ) {
            // Do not re-apply transient modifiers with no value
            instance.addTransientModifier( newModifier );
        }
    }
}