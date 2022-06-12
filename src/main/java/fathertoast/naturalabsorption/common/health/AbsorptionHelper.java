package fathertoast.naturalabsorption.common.health;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.register.NAAttributes;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.UUID;

public class AbsorptionHelper {

    public static void addNaturalAbsorption(PlayerEntity player, boolean updateActualAbsorption, double value) {
        if(HeartManager.isAbsorptionEnabled()) {
            double current = getNaturalAbsorption(player);
            value = MathHelper.clamp(value, 0.0F, (float) Config.ABSORPTION.NATURAL.maximumAmount.get());

            if(current != value) {
                final double netChange = value - current;
                player.getAttribute(NAAttributes.NATURAL_ABSORPTION.get()).setBaseValue(value);

                if (updateActualAbsorption) {
                    player.setAbsorptionAmount(player.getAbsorptionAmount() + (float) netChange);
                }
            }
        }
    }

    public static double getNaturalAbsorption(PlayerEntity player) {
        return player.getAttributeValue(NAAttributes.NATURAL_ABSORPTION.get());
    }

    public static void addEquipmentAbsorption(PlayerEntity player, boolean updateActualAbsorption, double value) {
        if(HeartManager.isAbsorptionEnabled() && Config.EQUIPMENT.ENCHANTMENT.enabled.get()) {
            double current = getNaturalAbsorption(player);
            value = Math.max(value, 0.0D);

            if(current != value) {
                final double netChange = value - current;
                player.getAttribute(NAAttributes.EQUIPMENT_ABSORPTION.get()).setBaseValue(value);

                if (updateActualAbsorption) {
                    player.setAbsorptionAmount(player.getAbsorptionAmount() + (float) netChange);
                }
            }
        }
    }

    public static double getEquipmentAbsorption(PlayerEntity player) {
        return player.getAttributeValue(NAAttributes.EQUIPMENT_ABSORPTION.get());
    }

    public static double getSteadyStateMaxAbsorption(PlayerEntity player) {
        final double calculatedMax = getNaturalAbsorption(player) + getEquipmentAbsorption(player);
        return Config.ABSORPTION.GENERAL.globalMax.get() < 0.0F ? calculatedMax :
                Math.min(calculatedMax, (float) Config.ABSORPTION.GENERAL.globalMax.get());
    }

    /**
     * Helper method for "changing" absorption attribute modifier values (removing old modifiers and replacing them with new ones).<br>
     * <br>
     *
     * @param player The Player to change a modifier for
     * @param natural Whether it is Natural Absorption or Equipment Absorption that should be changed.
     * @param modifierId The UUID of the modifier to change.
     * @param amount The new value to be added or subtracted from the current modifier's value.
     */
    public static void changeAbsorptionModifier(PlayerEntity player, boolean natural, UUID modifierId, double amount) {
        Attribute attribute = natural ? NAAttributes.NATURAL_ABSORPTION.get() : NAAttributes.EQUIPMENT_ABSORPTION.get();
        ModifiableAttributeInstance attributeInstance = player.getAttribute(attribute);

        if (attributeInstance.getModifier(modifierId) != null) {
            AttributeModifier modifier = attributeInstance.getModifier(modifierId);

            final double currentValue = modifier.getAmount();
            final boolean permanentMod = attributeInstance.permanentModifiers.contains(modifier);
            final String modifierName = modifier.getName();
            final AttributeModifier.Operation operation = modifier.getOperation();

            attributeInstance.removeModifier(modifier);

            AttributeModifier newModifier = new AttributeModifier(modifierId, modifierName, currentValue + amount, operation);

            if (permanentMod) {
                attributeInstance.addPermanentModifier(newModifier);
            }
            else {
                attributeInstance.addTransientModifier(newModifier);
            }


        }
    }
}
