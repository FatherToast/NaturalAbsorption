package fathertoast.naturalabsorption.common.core.register;

import fathertoast.naturalabsorption.api.lib.NaturalAbsorptionObjects;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.enchantment.AbsorptionEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public final class NAEnchantments {
    
    private static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create( ForgeRegistries.ENCHANTMENTS, NaturalAbsorption.MOD_ID );
    
    static {
        register( NaturalAbsorptionObjects.Enchantments.ABSORPTION, AbsorptionEnchantment::new );
    }
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers an enchantment to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    private static void register( RegistryObject<Enchantment> regObj, Supplier<Enchantment> supplier ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), supplier );
    }
    
    
    // Utility class
    private NAEnchantments() { }
}
