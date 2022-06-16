package fathertoast.naturalabsorption.common.compat.tc;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

public class NAModifiers {

    public static final ArmorAbsorptionModifier ARMOR_ABSORPTION = new ArmorAbsorptionModifier();


    public static void onModifierRegister(ModifierManager.ModifierRegistrationEvent event) {
        event.registerStatic(new ModifierId(NaturalAbsorption.MOD_ID, "armor_absorption"), ARMOR_ABSORPTION);
    }
}
