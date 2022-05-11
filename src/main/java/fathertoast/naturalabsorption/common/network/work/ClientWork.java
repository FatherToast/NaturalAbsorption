package fathertoast.naturalabsorption.common.network.work;

import fathertoast.naturalabsorption.client.RenderEventHandler;
import fathertoast.naturalabsorption.common.network.message.S2CSetNaturalAbsorption;

public class ClientWork {

    public static void handleNaturalAbsorptionUpdate(S2CSetNaturalAbsorption message) {
        RenderEventHandler.PLAYER_NATURAL_ABSORPTION = message.naturalAbsorption;
    }
}