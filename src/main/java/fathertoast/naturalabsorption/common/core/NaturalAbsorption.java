package fathertoast.naturalabsorption.common.core;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.register.NAEnchantments;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.event.NAEventListener;
import fathertoast.naturalabsorption.common.health.HeartManager;
import fathertoast.naturalabsorption.common.network.PacketHandler;
import fathertoast.naturalabsorption.common.recipe.CraftingUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod( NaturalAbsorption.MOD_ID )
public class NaturalAbsorption {
    /** Our mod ID. */
    @SuppressWarnings( "SpellCheckingInspection" )
    public static final String MOD_ID = "naturalabsorption";
    
    /** Logger instance for the mod. */
    public static final Logger LOG = LogManager.getLogger( MOD_ID );
    
    /** Our mod's packet handler; takes care of networking and sending messages. */
    @SuppressWarnings( "FieldCanBeLocal" )
    private final PacketHandler packetHandler = new PacketHandler();

    public NaturalAbsorption() {
        Config.initialize();

        packetHandler.registerMessages();
        CraftingUtil.registerConditions();
        
        MinecraftForge.EVENT_BUS.register( new NAEventListener() );
        MinecraftForge.EVENT_BUS.register( new HeartManager() );
        
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        NAItems.ITEMS.register( eventBus );
        NAEnchantments.ENCHANTMENTS.register( eventBus );
    }
    
    /** @return A ResourceLocation with the mod's namespace. */
    public static ResourceLocation resourceLoc( String path ) { return new ResourceLocation( MOD_ID, path ); }
    
    /** @return Returns a Forge registry entry as a string, or "null" if it is null. */
    public static String toString( @Nullable ForgeRegistryEntry<?> regEntry ) { return regEntry == null ? "null" : toString( regEntry.getRegistryName() ); }
    
    /** @return Returns the resource location as a string, or "null" if it is null. */
    public static String toString( @Nullable ResourceLocation res ) { return res == null ? "null" : res.toString(); }
}