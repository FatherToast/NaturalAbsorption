package fathertoast.naturalabsorption.common.compat.tc;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class NaturalAbsorptionTC {
    
    /** Tinkers Construct mod ID. */
    public static final String MOD_ID = "tconstruct";
    
    
    /**
     * Called from Natural Absorption's {@link NaturalAbsorption#NaturalAbsorption(FMLJavaModLoadingContext) mod class constructor}
     * to initialize Tinkers Construct compat.
     */
    public static void init( IEventBus modBus ) {
        if( ModList.get().isLoaded( MOD_ID ) ) {
            modBus.addListener( NAModifiers::onModifierRegister );
        }
    }
    
    // Non-instantiable
    private NaturalAbsorptionTC() { }
}
