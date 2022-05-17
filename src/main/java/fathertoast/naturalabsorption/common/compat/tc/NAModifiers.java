package fathertoast.naturalabsorption.common.compat.tc;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import slimeknights.tconstruct.library.modifiers.Modifier;

import java.util.function.Supplier;

public class NAModifiers {

    public static final DeferredRegister<Modifier> MODIFIERS = DeferredRegister.create(Modifier.class, NaturalAbsorption.MOD_ID);

    public static final RegistryObject<ArmorAbsorptionModifier> ARMOR_ABSORPTION = register("armor_absorption", ArmorAbsorptionModifier::new);


    private static <T extends Modifier> RegistryObject<T> register(String name, Supplier<T> supplier) {
        return MODIFIERS.register(name, supplier);
    }
}
