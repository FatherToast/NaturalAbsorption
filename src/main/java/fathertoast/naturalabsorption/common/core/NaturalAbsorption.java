package fathertoast.naturalabsorption.common.core;

import fathertoast.naturalabsorption.common.core.register.NAEnchantments;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.event.NAEventListener;
import fathertoast.naturalabsorption.common.health.HealthManager;
import fathertoast.naturalabsorption.common.network.PacketHandler;
import fathertoast.naturalabsorption.common.recipe.CraftingUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NaturalAbsorption.MOD_ID)
public class NaturalAbsorption {

    /** Our mod ID. */
    public static final String MOD_ID  = "naturalabsorption";

    /** Logger instance for the mod. */
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    /** Our mod's packet handler; takes care of networking and sending messages. */
    private final PacketHandler packetHandler = new PacketHandler();


    public NaturalAbsorption() {
        this.packetHandler.registerMessages();
        CraftingUtil.registerConditions();

        MinecraftForge.EVENT_BUS.register(new NAEventListener());
        MinecraftForge.EVENT_BUS.register(new HealthManager());

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        NAItems.ITEMS.register(eventBus);
        NAEnchantments.ENCHANTMENTS.register(eventBus);
    }
}
