package fathertoast.naturalabsorption.server;

import fathertoast.naturalabsorption.*;
import fathertoast.naturalabsorption.health.*;
import net.minecraft.entity.player.EntityPlayer;

@SuppressWarnings( "unused" )
public
class ServerProxy extends SidedModProxy
{
	@Override
	public
	float getAbsorptionCapacity( EntityPlayer player ) { return HealthData.get( player ).getAbsorptionCapacity( ); }
}
