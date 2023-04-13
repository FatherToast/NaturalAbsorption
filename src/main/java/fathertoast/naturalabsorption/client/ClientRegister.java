package fathertoast.naturalabsorption.client;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = NaturalAbsorption.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ClientRegister {

    public static final IGuiOverlay ABSORPTION_BACKGROUND = new AbsorptionBackgroundOverlay();

    @SubscribeEvent
    public static void onClientSetup( FMLClientSetupEvent event ) {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }

    @SubscribeEvent
    public static void onGuiOverlayRegister(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), "absorption_background_capacity", ABSORPTION_BACKGROUND);
    }
}