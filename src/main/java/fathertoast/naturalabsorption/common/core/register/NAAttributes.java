package fathertoast.naturalabsorption.common.core.register;

import fathertoast.naturalabsorption.api.lib.NaturalAbsorptionObjects;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public final class NAAttributes {
    
    private static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create( ForgeRegistries.ATTRIBUTES, NaturalAbsorption.MOD_ID );
    
    static {
        registerRanged( NaturalAbsorptionObjects.Attributes.NATURAL_ABSORPTION, 0.0D, 0.0D, Double.MAX_VALUE, true );
        registerRanged( NaturalAbsorptionObjects.Attributes.EQUIPMENT_ABSORPTION, 0.0D, 0.0D, Double.MAX_VALUE, true );
    }
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a ranged attribute to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    private static void registerRanged( RegistryObject<Attribute> regObj, double defaultValue, double min, double max, boolean sync ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();
        final String attribName = "attribute.name." + name;
        REGISTRY.register( name, () -> new RangedAttribute( attribName, defaultValue, min, max ).setSyncable( sync ) );
    }
    
    
    // Utility class
    private NAAttributes() { }
}
