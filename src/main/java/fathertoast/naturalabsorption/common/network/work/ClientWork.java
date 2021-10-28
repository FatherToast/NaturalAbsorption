package fathertoast.naturalabsorption.common.network.work;

import fathertoast.naturalabsorption.client.RenderEventHandler;
import fathertoast.naturalabsorption.common.health.HeartData;
import fathertoast.naturalabsorption.common.network.message.S2CSetNaturalAbsorption;
import net.minecraft.client.Minecraft;

public class ClientWork {
    /**
     * Updates the natural absorption value clientside and updates the absorption
     * value used when rendering the absorption hearts. Very epic.
     *
     * @see RenderEventHandler#PLAYER_NATURAL_ABSORPTION
     */
    public static void handleNaturalAbsorptionUpdate( S2CSetNaturalAbsorption message ) {
        RenderEventHandler.PLAYER_NATURAL_ABSORPTION = message.naturalAbsorption;
    }
}