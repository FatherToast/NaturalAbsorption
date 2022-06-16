package fathertoast.naturalabsorption.client;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.hearts.HeartManager;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = NaturalAbsorption.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ClientRegister {

    public static IIngameOverlay ABSORPTION_BACKGROUND = null;

    @SubscribeEvent
    public static void onClientSetup( FMLClientSetupEvent event ) {
        ABSORPTION_BACKGROUND = OverlayRegistry.registerOverlayBelow( ForgeIngameGui.PLAYER_HEALTH_ELEMENT, "NAAbsorptionBackgroundCapacity", new AbsorptionBackgroundOverlay( ));

        // Toggle
        OverlayRegistry.enableOverlay(ABSORPTION_BACKGROUND, Config.ABSORPTION.GENERAL.renderCapacityBackground.get() );
    }
}