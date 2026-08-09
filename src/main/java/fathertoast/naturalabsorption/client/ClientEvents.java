package fathertoast.naturalabsorption.client;

import fathertoast.naturalabsorption.common.core.hearts.AbsorptionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEvents {
    
    /** Called right before a GUI overlay is being rendered. */
    @SubscribeEvent
    public void onRenderGuiOverlayPre( RenderGuiOverlayEvent.Pre event ) {
        Player player = Minecraft.getInstance().player;
        if( player == null ) return;
        
        if( event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type() && !ClientUtil.RENDER_ARMOR ) {
            event.setCanceled( true );
        }
        else if( event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type() ) {
            if( AbsorptionHelper.getSteadyStateMaxAbsorption( player ) > 0.0D && ClientUtil.OVERLAY_ENABLED )
                event.setCanceled( true );
        }
    }
}
