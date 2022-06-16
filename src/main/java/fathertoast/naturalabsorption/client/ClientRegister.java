package fathertoast.naturalabsorption.client;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = NaturalAbsorption.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ClientRegister {
    
    public static IIngameOverlay ABSORPTION_BACKGROUND = OverlayRegistry.registerOverlayBelow(
            ForgeIngameGui.PLAYER_HEALTH_ELEMENT, "NAAbsorptionBackgroundCapacity", new AbsorptionBackgroundOverlay() );
    
    @SubscribeEvent
    public static void onClientSetup( FMLClientSetupEvent event ) {
        // Register above, since the configs will probably need this value before client setup
        //ABSORPTION_BACKGROUND = OverlayRegistry.registerOverlayBelow( ForgeIngameGui.PLAYER_HEALTH_ELEMENT, "NAAbsorptionBackgroundCapacity", new AbsorptionBackgroundOverlay() );
    }
}