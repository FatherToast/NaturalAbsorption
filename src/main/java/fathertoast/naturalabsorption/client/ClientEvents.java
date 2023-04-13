package fathertoast.naturalabsorption.client;

import fathertoast.naturalabsorption.common.hearts.AbsorptionHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEvents {

    @SubscribeEvent
    public void onRenderGuiOverlayPre(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type() && !ClientUtil.RENDER_ARMOR) {
            event.setCanceled(true);
        }
        else if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type()) {
            if (AbsorptionHelper.getSteadyStateMaxAbsorption(Minecraft.getInstance().player) > 0.0D && ClientUtil.OVERLAY_ENABLED)
                event.setCanceled(true);
        }
    }
}
