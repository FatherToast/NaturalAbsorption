package fathertoast.naturalabsorption.client;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = NaturalAbsorption.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ClientRegister {

    public static final IGuiOverlay ABSORPTION_BACKGROUND = new AbsorptionBackgroundOverlay();

    @SubscribeEvent
    public static void onClientSetup( FMLClientSetupEvent event ) {
        // Register above, since the configs will probably need this value before client setup
        //ABSORPTION_BACKGROUND = OverlayRegistry.registerOverlayBelow( ForgeIngameGui.PLAYER_HEALTH_ELEMENT, "NAAbsorptionBackgroundCapacity", new AbsorptionBackgroundOverlay() );
    }

    public static void onGuiOverlayRegister(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("NAAbsorptionBackgroundCapacity", ABSORPTION_BACKGROUND);
    }
}