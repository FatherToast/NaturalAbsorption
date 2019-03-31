package fathertoast.naturalabsorption.client;

import fathertoast.naturalabsorption.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;

public
class ClientProxy extends SidedModProxy
{
	public static float clientAbsorptionCapacity = -1.0F;
	
	@Override
	public
	float getAbsorptionCapacity( EntityPlayer player ) { return clientAbsorptionCapacity; }
	
	@Override
	public
	EntityPlayer getPlayer( ) { return Minecraft.getMinecraft( ).player; }
	
	@Override
	public
	void registerRenderers( )
	{
		register( ModObjects.BOOK_ABSORPTION );
	}
	
	private
	void register( Item item )
	{
		register( item, 0 );
	}
	
	private
	void register( Item item, int meta )
	{
		//noinspection ConstantConditions
		ModelLoader.setCustomModelResourceLocation( item, meta, new ModelResourceLocation( item.getRegistryName( ), "normal" ) );
	}
	
	@Override
	public
	void registerEventHandlers( )
	{
		super.registerEventHandlers( );
		MinecraftForge.EVENT_BUS.register( new RenderEventHandler( ) );
	}
}
