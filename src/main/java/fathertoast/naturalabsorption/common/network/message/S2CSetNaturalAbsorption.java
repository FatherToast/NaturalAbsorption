package fathertoast.naturalabsorption.common.network.message;

import fathertoast.naturalabsorption.common.network.work.ClientWork;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSetNaturalAbsorption {

    public final float naturalAbsorption;

    public S2CSetNaturalAbsorption(final float absorption) {
        naturalAbsorption = absorption;
    }

    public static void handle(S2CSetNaturalAbsorption message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if(context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleNaturalAbsorptionUpdate(message));
        }
        context.setPacketHandled(true);
    }

    public static S2CSetNaturalAbsorption decode(PacketBuffer buffer) {
        return new S2CSetNaturalAbsorption(buffer.readFloat());
    }

    public static void encode(S2CSetNaturalAbsorption message, PacketBuffer buffer) {
        buffer.writeFloat(message.naturalAbsorption);
    }
}
