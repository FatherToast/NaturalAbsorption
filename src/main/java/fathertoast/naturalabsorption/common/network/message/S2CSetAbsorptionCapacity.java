package fathertoast.naturalabsorption.common.network.message;

import fathertoast.naturalabsorption.common.network.work.ClientWork;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSetAbsorptionCapacity {

    public final float absorptionCapacity;

    public S2CSetAbsorptionCapacity( final float absorptionCapacity ) {
        this.absorptionCapacity = absorptionCapacity;
    }

    public static void handle( S2CSetAbsorptionCapacity message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get( );

        if ( context.getDirection( ).getReceptionSide( ).isClient( ) ) {
            context.enqueueWork( ( ) -> ClientWork.handleAbsorptionCapacityUpdate( message ) );
        }
        context.setPacketHandled( true );
    }

    public static S2CSetAbsorptionCapacity decode( PacketBuffer buffer ) {
        return new S2CSetAbsorptionCapacity( buffer.readFloat( ) );
    }

    public static void encode( S2CSetAbsorptionCapacity message, PacketBuffer buffer ) {
        buffer.writeFloat( message.absorptionCapacity );
    }
}
