package fathertoast.naturalabsorption;

import fathertoast.naturalabsorption.common.health.HealthManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;

public abstract
class SidedModProxy
{
	public abstract
	float getAbsorptionCapacity( PlayerEntity player );
	
	public PlayerEntity getPlayer( ) { return null; }
	
	public
	void registerRenderers( ) { }
	
	public
	void registerEventHandlers( )
	{
		MinecraftForge.EVENT_BUS.register( new HealthManager( ) );
	}
}
