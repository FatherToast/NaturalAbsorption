package fathertoast.naturalabsorption.common.network;

import fathertoast.naturalabsorption.common.network.message.S2CSetNaturalAbsorption;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nonnull;

public class NetworkHelper {

    /** Sends a message to the client about a change in natural absorption capacity. */
    public static void setNaturalAbsorption(@Nonnull ServerPlayerEntity serverPlayer, float absorption) {
        PacketHandler.sendToClient(new S2CSetNaturalAbsorption(absorption), serverPlayer);
    }
}