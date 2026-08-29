package fathertoast.naturalabsorption.common.core.register;

import fathertoast.naturalabsorption.api.lib.NaturalAbsorptionObjects;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.item.AbsorptionAbsorbingBookItem;
import fathertoast.naturalabsorption.common.item.AbsorptionBookItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

public final class NAItems {
    
    private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create( ForgeRegistries.ITEMS, NaturalAbsorption.MOD_ID );
    
    public static final Map<ResourceKey<CreativeModeTab>, List<RegistryObject<? extends Item>>> TAB_ITEMS = new HashMap<>();
    
    static {
        register( NaturalAbsorptionObjects.Items.ABSORPTION_BOOK, AbsorptionBookItem::new, CreativeModeTabs.COMBAT );
        register( NaturalAbsorptionObjects.Items.ABSORPTION_ABSORBING_BOOK, AbsorptionAbsorbingBookItem::new, CreativeModeTabs.COMBAT );
    }
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers an item to the deferred register, and adds it to the specified creative mode tabs later. */
    @SafeVarargs
    private static <T extends Item> void register( RegistryObject<Item> regObj, Supplier<T> itemSupplier, ResourceKey<CreativeModeTab>... creativeTabs ) {
        regObj = REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), itemSupplier );
        queueForCreativeTabs( regObj, creativeTabs );
    }
    
    /** Enqueues the given item to be added to the specified creative mode tabs. */
    @SafeVarargs
    private static void queueForCreativeTabs( RegistryObject<? extends Item> item, ResourceKey<CreativeModeTab>... creativeTabs ) {
        for( ResourceKey<CreativeModeTab> tab : creativeTabs ) {
            if( !TAB_ITEMS.containsKey( tab ) ) {
                List<RegistryObject<? extends Item>> list = new ArrayList<>();
                list.add( item );
                TAB_ITEMS.put( tab, list );
            }
            else {
                TAB_ITEMS.get( tab ).add( item );
            }
        }
    }
    
    /** Called when creative tabs gets populated with items. */
    public static void onCreativeTabPopulate( BuildCreativeModeTabContentsEvent event ) {
        if( TAB_ITEMS.containsKey( event.getTabKey() ) ) {
            List<RegistryObject<? extends Item>> items = TAB_ITEMS.get( event.getTabKey() );
            items.forEach( ( regObj ) -> event.accept( regObj.get() ) );
        }
    }
    
    
    // Utility class
    private NAItems() { }
}