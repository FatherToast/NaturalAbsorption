package fathertoast.naturalabsorption.common.core.register;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.loot.AddItemChanceLootModifier;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class NALootModifiers {

    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, NaturalAbsorption.MOD_ID);


    public static final RegistryObject<AddItemChanceLootModifier.Serializer> ADD_ITEM_CHANCE = register("add_with_chance", AddItemChanceLootModifier.Serializer::new);


    private static <T extends GlobalLootModifierSerializer<?>> RegistryObject<T> register(String name, Supplier<T> supplier) {
        return LOOT_MODIFIER_SERIALIZERS.register(name, supplier);
    }
}
