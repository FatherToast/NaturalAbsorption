package fathertoast.naturalabsorption.common.core;

import fathertoast.naturalabsorption.api.INaturalAbsorption;
import fathertoast.naturalabsorption.api.impl.NaturalAbsorptionAPI;
import fathertoast.naturalabsorption.common.command.CommandRegister;
import fathertoast.naturalabsorption.common.compat.tc.NaturalAbsorptionTC;
import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.register.NAAttributes;
import fathertoast.naturalabsorption.common.core.register.NAEnchantments;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.core.register.NALootModifiers;
import fathertoast.naturalabsorption.common.event.NAEventListener;
import fathertoast.naturalabsorption.common.hearts.HeartManager;
import fathertoast.naturalabsorption.common.network.PacketHandler;
import fathertoast.naturalabsorption.common.recipe.CraftingUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.function.Function;

@Mod( NaturalAbsorption.MOD_ID )
public class NaturalAbsorption {
    
    /* Feature List:
     *  - Absorption
     *      - Natural
     *      - Regeneration to max
     *  - Health
     *      - Disable vanilla regen
     *      - Regeneration to limit
     *      - Eating heals
     *      - Adds food info to tooltip
     *  - Items
     *      - Book of Absorption
     *      - Absorption Absorbing Book
     *  - Enchantments
     *      - Absorption
     *  - Loot
     *      - Book of Absorption added to dungeon loot
     *  - GUI
     *      - Render empty hearts to show max absorption
     *  - Commands
     *      - Add or remove natural absorption from player(s) (OP permission level)
     *  - Secret features
     *      - The Almighty Cactus God
     *  - Compatibility
     *      - Enchantment Descriptions
     *      - Tinkers Construct
     *  - Default-disabled features
     *      - Armor replacement
     */
    
    /** Our mod ID. */
    @SuppressWarnings( "SpellCheckingInspection" )
    public static final String MOD_ID = "naturalabsorption";
    
    /** Logger instance for the mod. */
    public static final Logger LOG = LogManager.getLogger( MOD_ID );
    
    /** Our mod's packet handler; takes care of networking and sending messages. */
    @SuppressWarnings( "FieldCanBeLocal" )
    private final PacketHandler packetHandler = new PacketHandler();
    
    /** Mod API instance **/
    private final INaturalAbsorption modApi = new NaturalAbsorptionAPI();
    
    
    public NaturalAbsorption() {
        Config.initialize();
        
        packetHandler.registerMessages();
        CraftingUtil.registerConditions();
        
        MinecraftForge.EVENT_BUS.register( new NAEventListener() );
        MinecraftForge.EVENT_BUS.register( new HeartManager() );
        MinecraftForge.EVENT_BUS.addListener( CommandRegister::register );
        
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        modBus.addListener( this::onInterModProcess );
        modBus.addListener( HeartManager::onEntityAttributeCreation );
        
        NAItems.ITEMS.register( modBus );
        NAAttributes.ATTRIBUTES.register( modBus );
        NAEnchantments.ENCHANTMENTS.register( modBus );
        NALootModifiers.LOOT_MODIFIER_SERIALIZERS.register( modBus );
        
        if( ModList.get().isLoaded( "tconstruct" ) ) {
            NaturalAbsorptionTC.init( modBus );
        }
    }
    
    /**
     * Hands the mod API to mods that ask for it.
     */
    @SuppressWarnings("unchecked")
    private void onInterModProcess( InterModProcessEvent event ) {
        event.getIMCStream().forEach( ( message ) -> {
            if( message.method().equals( "getNaturalAbsorptionAPI" ) ) {
                Object o = message.messageSupplier().get();

                try {
                    ((Function<INaturalAbsorption, Void>) o).apply( modApi );
                }
                catch (Exception ignored) {
                    LOG.warn("Mod with ID \"{}\" asked for our API instance, but something went wrong!", message.senderModId());
                }
            }
        } );
    }
    
    /** @return A ResourceLocation with the mod's namespace. */
    public static ResourceLocation resourceLoc(String path ) { return new ResourceLocation( MOD_ID, path ); }
    
    /** @return Returns a Forge registry entry as a string, or "null" if it is null. */
    public static String toString( @Nullable ForgeRegistryEntry<?> regEntry ) { return regEntry == null ? "null" : toString( regEntry.getRegistryName() ); }
    
    /** @return Returns the resource location as a string, or "null" if it is null. */
    public static String toString( @Nullable ResourceLocation res ) { return res == null ? "null" : res.toString(); }
}