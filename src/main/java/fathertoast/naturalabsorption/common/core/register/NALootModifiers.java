package fathertoast.naturalabsorption.common.core.register;

import com.mojang.serialization.Codec;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.loot.AddItemChanceLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class NALootModifiers {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, NaturalAbsorption.MOD_ID);


    public static final RegistryObject<Codec<AddItemChanceLootModifier>> ADD_ITEM_CHANCE = register("add_with_chance", AddItemChanceLootModifier.CODEC);


    private static <T extends Codec<? extends IGlobalLootModifier>> RegistryObject<T> register(String name, Supplier<T> supplier) {
        return LOOT_MODIFIER_SERIALIZERS.register(name, supplier);
    }
}
