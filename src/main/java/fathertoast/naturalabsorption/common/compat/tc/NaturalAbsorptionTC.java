package fathertoast.naturalabsorption.common.compat.tc;

import net.minecraftforge.eventbus.api.IEventBus;

public class NaturalAbsorptionTC {

    public static void init(IEventBus modBus) {
        NAModifiers.MODIFIERS.register(modBus);
    }
}
