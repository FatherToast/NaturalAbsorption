package fathertoast.naturalabsorption.common.core.register;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.enchantment.AbsorptionEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class NAEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, NaturalAbsorption.MOD_ID);


    public static final RegistryObject<Enchantment> ABSORPTION_ENCHANTMENT = ENCHANTMENTS.register("absorption", AbsorptionEnchantment::new);
}
