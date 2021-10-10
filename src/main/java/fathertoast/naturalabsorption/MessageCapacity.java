package fathertoast.naturalabsorption;

import fathertoast.naturalabsorption.client.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public
class MessageCapacity implements IMessage
{
	public static
	void sendFor( PlayerEntity player )
	{
		if( player instanceof ServerPlayerEntity ) {
			NaturalAbsorptionMod.network( ).sendTo( new MessageCapacity( player ), (ServerPlayerEntity) player );
		}
	}
	
	private float absorptionCapacity;
	
	// Used by the client reciever
	@SuppressWarnings( "unused" )
	public
	MessageCapacity( )
	{
		absorptionCapacity = -1.0F;
	}
	
	private
	MessageCapacity( EntityPlayer player )
	{
		absorptionCapacity = HealthData.get( player ).getAbsorptionCapacity( );
	}
	
	@Override
	public
	void fromBytes( ByteBuf buf )
	{
		absorptionCapacity = buf.readFloat( );
	}
	
	@Override
	public
	void toBytes( ByteBuf buf )
	{
		buf.writeFloat( absorptionCapacity );
	}
	
	public static
	class Handler implements IMessageHandler< MessageCapacity, IMessage >
	{
		@Override
		public
		IMessage onMessage( MessageCapacity message, MessageContext ctx )
		{
			ClientProxy.clientAbsorptionCapacity = message.absorptionCapacity;
			return null;
		}
	}
}
