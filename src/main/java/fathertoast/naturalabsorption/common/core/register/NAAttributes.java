package fathertoast.naturalabsorption.common.core.register;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;

public class NAAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, NaturalAbsorption.MOD_ID);


    public static final RegistryObject<Attribute> NATURAL_ABSORPTION = registerRanged("natural_absorption", Type.PLAYER,0.0D, 0.0D, Double.MAX_VALUE, true);
    public static final RegistryObject<Attribute> EQUIPMENT_ABSORPTION = registerRanged("equipment_absorption", Type.PLAYER, 0.0D, 0.0D, Double.MAX_VALUE, true);


    private static RegistryObject<Attribute> registerRanged(String name, Type type, double defaultValue, double min, double max, boolean sync) {
        final String regName = type.name().toLowerCase(Locale.ROOT) + "." + name;
        final String attribName = "attribute.name." + regName;
        return ATTRIBUTES.register(regName, () -> new RangedAttribute(attribName, defaultValue, min, max).setSyncable(sync));
    }

    enum Type {
        PLAYER,
        GENERIC;
    }
}
