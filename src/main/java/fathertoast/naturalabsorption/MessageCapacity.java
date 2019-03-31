package fathertoast.naturalabsorption;

import fathertoast.naturalabsorption.client.*;
import fathertoast.naturalabsorption.health.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public
class MessageCapacity implements IMessage
{
	public static
	void sendFor( EntityPlayer player )
	{
		if( player instanceof EntityPlayerMP ) {
			NaturalAbsorptionMod.network( ).sendTo( new MessageCapacity( player ), (EntityPlayerMP) player );
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
