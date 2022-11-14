package fathertoast.naturalabsorption.common.compat.tc;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

public class NAModifiers {

    public static final ModifierId ARMOR_ABSORPTION_ID = new ModifierId(NaturalAbsorption.resourceLoc("armor_absorption"));
    public static final ArmorAbsorptionModifier ARMOR_ABSORPTION = new ArmorAbsorptionModifier();


    public static void onModifierRegister(ModifierManager.ModifierRegistrationEvent event) {
        event.registerStatic(ARMOR_ABSORPTION_ID, ARMOR_ABSORPTION);
    }
}
