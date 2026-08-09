package fathertoast.naturalabsorption.common.core.register;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.enchantment.AbsorptionEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NAEnchantments {
    
    public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create( ForgeRegistries.ENCHANTMENTS, NaturalAbsorption.MOD_ID );
    
    
    public static final RegistryObject<AbsorptionEnchantment> ABSORPTION_ENCHANTMENT = REGISTRY.register( "absorption", AbsorptionEnchantment::new );
}
