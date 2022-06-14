package fathertoast.naturalabsorption.common.hearts;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.register.NAAttributes;
import fathertoast.naturalabsorption.common.enchantment.AbsorptionEnchantment;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.UUID;

public class AbsorptionHelper {
    
    /** @return The player's max absorption, from all sources combined. In other words, the actual limit on absorption recovery. */
    public static double getMaxAbsorption( PlayerEntity player ) {
        return getSteadyStateMaxAbsorption( player ) + HeartManager.getPotionAbsorption( player );
    }
    
    /** @return The player's max absorption not counting buffs, limited by the global max absorption config. */
    public static double getSteadyStateMaxAbsorption( PlayerEntity player ) {
        final double calculatedMax = getNaturalAbsorption( player ) + getEquipmentAbsorption( player );
        return Config.ABSORPTION.GENERAL.globalMax.get() < 0.0 ? calculatedMax :
                Math.min( calculatedMax, Config.ABSORPTION.GENERAL.globalMax.get() );
    }
    
    /** @return The player's max absorption granted by natural absorption. */
    public static double getNaturalAbsorption( PlayerEntity player ) {
        return player.getAttributeValue( NAAttributes.NATURAL_ABSORPTION.get() );
    }
    
    /** @return True if the player's base natural absorption has been initialized. */
    public static boolean isBaseNaturalAbsorptionInitialized( PlayerEntity player ) {
        return hasAbsorptionModifier( player, true, NATURAL_MODIFIER_BASE );
    }
    
    /** @return The player's natural absorption, ignoring all attribute modifiers. */
    public static double getBaseNaturalAbsorption( PlayerEntity player ) {
        return getAbsorptionModifier( player, true, NATURAL_MODIFIER_BASE );
    }
    
    /** Sets base natural absorption, clamped in a valid range, optionally reducing actual absorption as needed. */
    public static void setBaseNaturalAbsorption( PlayerEntity player, boolean updateActualAbsorption, double value ) {
        if( HeartManager.isAbsorptionEnabled() ) {
            final double initialValue = updateActualAbsorption ? getNaturalAbsorption( player ) : 0.0;
            
            setAbsorptionModifier( player, true, NATURAL_MODIFIER_BASE,
                    MathHelper.clamp( value, 0.0, Config.ABSORPTION.NATURAL.maximumAmount.get() ) );
            
            if( updateActualAbsorption ) {
                final double finalValue = getNaturalAbsorption( player );
                if( initialValue > finalValue ) {
                    final double netChange = finalValue - initialValue;
                    player.setAbsorptionAmount( player.getAbsorptionAmount() + (float) netChange );
                }
            }
        }
    }
    
    /** Adds (or removes) base natural absorption, clamped in a valid range, optionally reducing actual absorption as needed. */
    public static void addBaseNaturalAbsorption( PlayerEntity player, boolean updateActualAbsorption, double value ) {
        setBaseNaturalAbsorption( player, updateActualAbsorption, getBaseNaturalAbsorption( player ) + value );
    }
    
    /** Removes base natural absorption equal to the death penalty, down to a limit, reducing actual absorption to match. */
    public static void applyDeathPenalty( PlayerEntity player ) {
        if( HeartManager.isAbsorptionEnabled() && Config.ABSORPTION.NATURAL.deathPenalty.get() > 0.0 ) {
            final double initialValue = getBaseNaturalAbsorption( player );
            if( initialValue > Config.ABSORPTION.NATURAL.deathPenaltyLimit.get() ) {
                setBaseNaturalAbsorption( player, true,
                        Math.max( initialValue - Config.ABSORPTION.NATURAL.deathPenalty.get(), Config.ABSORPTION.NATURAL.deathPenaltyLimit.get() ) );
            }
        }
    }
    
    /** @return The player's max absorption granted by equipment. That is, how much they would lose by unequipping everything. */
    public static double getEquipmentAbsorption( PlayerEntity player ) {
        return player.getAttributeValue( NAAttributes.EQUIPMENT_ABSORPTION.get() );
    }
    
    /** @return The player's equipment absorption from enchantments, ignoring all attribute modifiers. */
    public static double getEnchantmentAbsorption( PlayerEntity player ) {
        return getAbsorptionModifier( player, false, EQUIP_MODIFIER_ENCHANT );
    }
    
    /** @return The player's equipment absorption from armor replacement, ignoring all attribute modifiers. */
    public static double getArmorReplacementAbsorption( PlayerEntity player ) {
        return getAbsorptionModifier( player, false, EQUIP_MODIFIER_ARMOR_REPLACE );
    }
    
    /** Recalculates and reapplies all equipment absorption modifiers. */
    public static void updateEquipmentAbsorption( PlayerEntity player, double previousMaxAbsorb ) {
        if( HeartManager.isAbsorptionEnabled() ) {
            if( Config.EQUIPMENT.ENCHANTMENT.enabled.get() )
                setAbsorptionModifier( player, false, EQUIP_MODIFIER_ENCHANT, AbsorptionEnchantment.getMaxAbsorptionBonus( player ) );
            if( HeartManager.isArmorReplacementEnabled() )
                setAbsorptionModifier( player, false, EQUIP_MODIFIER_ARMOR_REPLACE, getArmorReplacementBonus( player ) );
            
            final double finalMaxAbsorb = getMaxAbsorption( player );
            if( previousMaxAbsorb > finalMaxAbsorb ) {
                final double netChange = finalMaxAbsorb - previousMaxAbsorb;
                player.setAbsorptionAmount( player.getAbsorptionAmount() + (float) netChange );
            }
        }
    }
    
    /** @return The maximum absorption granted by armor replacement. */
    private static double getArmorReplacementBonus( PlayerEntity player ) {
        double bonus = 0.0;
        if( Config.EQUIPMENT.ARMOR.armorMultiplier.get() > 0.0 ) {
            final double armor = player.getAttributeValue( Attributes.ARMOR );
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
    private static boolean hasAbsorptionModifier( PlayerEntity player, boolean natural, AttributeModifier staticModifier ) {
        final Attribute attribute = natural ? NAAttributes.NATURAL_ABSORPTION.get() : NAAttributes.EQUIPMENT_ABSORPTION.get();
        final ModifiableAttributeInstance instance = player.getAttribute( attribute );
        return instance != null && instance.getModifier( staticModifier.getId() ) != null;
    }
    
    /** Helper method for reading absorption attribute modifier values. */
    private static double getAbsorptionModifier( PlayerEntity player, boolean natural, AttributeModifier staticModifier ) {
        final Attribute attribute = natural ? NAAttributes.NATURAL_ABSORPTION.get() : NAAttributes.EQUIPMENT_ABSORPTION.get();
        final ModifiableAttributeInstance instance = player.getAttribute( attribute );
        if( instance != null ) {
            final AttributeModifier modifier = instance.getModifier( staticModifier.getId() );
            if( modifier != null ) return modifier.getAmount();
        }
        return 0.0;
    }
    
    /** Helper method for writing absorption attribute modifier values. */
    private static void setAbsorptionModifier( PlayerEntity player, boolean natural, AttributeModifier staticModifier, double value ) {
        final Attribute attribute = natural ? NAAttributes.NATURAL_ABSORPTION.get() : NAAttributes.EQUIPMENT_ABSORPTION.get();
        final ModifiableAttributeInstance instance = player.getAttribute( attribute );
        if( instance == null ) {
            NaturalAbsorption.LOG.error( "Player '{}' does not have '{}' registered!",
                    player.getScoreboardName(), attribute.getDescriptionId() );
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
        else {
            instance.addTransientModifier( newModifier );
        }
    }
}