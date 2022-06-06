package fathertoast.naturalabsorption.common.health;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.register.NAAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class AbsorptionHelper {

    public static void setNaturalAbsorption(PlayerEntity player, boolean updateActualAbsorption, double value) {
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

    public static void addNaturalAbsorption(PlayerEntity player, boolean updateActualAbsorption, double amount) {
        setNaturalAbsorption(player, updateActualAbsorption,getNaturalAbsorption(player) + amount);
    }

    public static void removeNaturalAbsorption(PlayerEntity player, double amount) {
        setNaturalAbsorption(player, true, getNaturalAbsorption(player) - amount);
        double currentValue = player.getAttributeValue(NAAttributes.NATURAL_ABSORPTION.get());
        player.getAttribute(NAAttributes.NATURAL_ABSORPTION.get()).setBaseValue(Math.max(0.0D, currentValue - amount));
    }

    public static double getNaturalAbsorption(PlayerEntity player) {
        return player.getAttributeValue(NAAttributes.NATURAL_ABSORPTION.get());
    }

    public static void setEquipmentAbsorption(PlayerEntity player, boolean updateActualAbsorption, double value) {
        if(HeartManager.isAbsorptionEnabled()) {
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

    public static void addEquipmentAbsorption(PlayerEntity player, boolean updateActualAbsorption, double amount) {
        setNaturalAbsorption(player, updateActualAbsorption,getNaturalAbsorption(player) + amount);
    }

    public static void removeEquipmentAbsorption(PlayerEntity player, double amount) {
        setNaturalAbsorption(player, true, getNaturalAbsorption(player) - amount);
        double currentValue = player.getAttributeValue(NAAttributes.EQUIPMENT_ABSORPTION.get());
        player.getAttribute(NAAttributes.EQUIPMENT_ABSORPTION.get()).setBaseValue(Math.max(0.0D, currentValue - amount));
    }

    public static double getEquipmentAbsorption(PlayerEntity player) {
        return player.getAttributeValue(NAAttributes.EQUIPMENT_ABSORPTION.get());
    }

    public static double getSteadyStateMaxAbsorption(PlayerEntity player) {
        final double calculatedMax = getNaturalAbsorption(player) + getEquipmentAbsorption(player);
        return Config.ABSORPTION.GENERAL.globalMax.get() < 0.0F ? calculatedMax :
                Math.min(calculatedMax, (float) Config.ABSORPTION.GENERAL.globalMax.get());
    }
}
