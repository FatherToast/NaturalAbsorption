package fathertoast.naturalabsorption;

import fathertoast.naturalabsorption.health.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public abstract
class SidedModProxy
{
	public abstract
	float getAbsorptionCapacity( EntityPlayer player );
	
	public
	EntityPlayer getPlayer( ) { return null; }
	
	public
	void registerRenderers( ) { }
	
	public
	void registerEventHandlers( )
	{
		MinecraftForge.EVENT_BUS.register( new HealthManager( ) );
	}
}
