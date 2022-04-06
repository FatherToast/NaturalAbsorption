package fathertoast.naturalabsorption.client;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = NaturalAbsorption.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ClientRegister {

    /**
     * Used for the Absorption Book tooltip.
     * Updated via packets when needded.
     */
    public static float NATURAL_ABSORPTION = -1.0F;


    @SubscribeEvent
    public static void onClientSetup( FMLClientSetupEvent event ) {
        //MinecraftForge.EVENT_BUS.register( new RenderEventHandler() );
    }
}