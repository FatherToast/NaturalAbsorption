package fathertoast.naturalabsorption.common.core.register;

import com.mojang.serialization.Codec;
import fathertoast.naturalabsorption.api.lib.NaturalAbsorptionObjects;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.loot.AddItemChanceLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public final class NALootModifiers {
    
    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTRY = DeferredRegister.create( ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, NaturalAbsorption.MOD_ID );
    
    static {
        register( NaturalAbsorptionObjects.GLMSerializers.ADD_WITH_CHANCE, AddItemChanceLootModifier.CODEC );
    }
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a global loot modifier serializer to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    private static <T extends Codec<? extends IGlobalLootModifier>> void register( RegistryObject<Codec<? extends IGlobalLootModifier>> regObj,
                                                                                   Supplier<T> supplier ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), supplier );
    }
    
    
    // Utility class
    private NALootModifiers() { }
}
