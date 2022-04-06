package fathertoast.naturalabsorption.common.network.work;

import fathertoast.naturalabsorption.client.ClientRegister;
import fathertoast.naturalabsorption.common.network.message.S2CSetNaturalAbsorption;

public class ClientWork {

    public static void handleNaturalAbsorptionUpdate(S2CSetNaturalAbsorption message) {
        ClientRegister.NATURAL_ABSORPTION = message.naturalAbsorption;
    }
}