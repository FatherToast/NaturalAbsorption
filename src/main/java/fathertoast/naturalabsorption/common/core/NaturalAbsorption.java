package fathertoast.naturalabsorption.common.core;

import fathertoast.naturalabsorption.common.core.register.NAEnchantments;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NaturalAbsorption.MOD_ID)
public class NaturalAbsorption {

    public static final String MOD_ID  = "naturalabsorption";


    public NaturalAbsorption() {

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        NAItems.ITEMS.register(eventBus);
        NAEnchantments.ENCHANTMENTS.register(eventBus);
    }
}
