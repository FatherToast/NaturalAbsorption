package fathertoast.naturalabsorption.common.network.work;

import fathertoast.naturalabsorption.client.RenderEventHandler;
import fathertoast.naturalabsorption.common.health.HealthData;
import fathertoast.naturalabsorption.common.network.message.S2CSetAbsorptionCapacity;
import net.minecraft.client.Minecraft;

public class ClientWork {

    /**
     * Updates the absorption capacity value
     * clientside and updates the absorption
     * cap value used when rendering the
     * absorption hearts. Very epic.
     *
     * @see RenderEventHandler#ABSORPTION_CAPACITY
     */
    public static void handleAbsorptionCapacityUpdate( S2CSetAbsorptionCapacity message ) {
        RenderEventHandler.ABSORPTION_CAPACITY = message.absorptionCapacity;
        HealthData.get( Minecraft.getInstance( ).player ).setAbsorptionCapacity( message.absorptionCapacity );
    }
}
