package fathertoast.naturalabsorption.common.network;

import fathertoast.naturalabsorption.common.network.message.S2CSetAbsorptionCapacity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class NetworkHelper {

    /** Sends a message to the client about a change in absorption capacity. */
    public static void setAbsorptionCapacity( ServerPlayerEntity serverPlayer, float absorptionCapacity ) {
        PacketHandler.sendToClient( new S2CSetAbsorptionCapacity(absorptionCapacity), serverPlayer );
    }
}
