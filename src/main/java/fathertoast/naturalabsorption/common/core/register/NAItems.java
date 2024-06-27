package fathertoast.naturalabsorption.common.core.register;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.item.AbsorptionAbsorbingBookItem;
import fathertoast.naturalabsorption.common.item.AbsorptionBookItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class NAItems {
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NaturalAbsorption.MOD_ID);
    public static final Map<ResourceKey<CreativeModeTab>, List<RegistryObject<? extends Item>>> TAB_ITEMS = new HashMap<>();

    
    public static final RegistryObject<Item> ABSORPTION_BOOK = register("absorption_book", AbsorptionBookItem::new, CreativeModeTabs.COMBAT );
    public static final RegistryObject<Item> ABSORPTION_ABSORBING_BOOK = register("absorption_absorbing_book", AbsorptionAbsorbingBookItem::new, CreativeModeTabs.COMBAT );


    @SafeVarargs
    protected static <T extends Item> RegistryObject<T> register(String name, Supplier<T> itemSupplier, ResourceKey<CreativeModeTab>... creativeTabs) {
        RegistryObject<T> regObj = ITEMS.register(name, itemSupplier);
        queueForCreativeTabs(regObj, creativeTabs);
        return regObj;
    }

    @SafeVarargs
    protected static void queueForCreativeTabs(RegistryObject<? extends Item> item, ResourceKey<CreativeModeTab>... creativeTabs) {
        for (ResourceKey<CreativeModeTab> tab : creativeTabs) {
            if (!TAB_ITEMS.containsKey(tab)) {
                List<RegistryObject<? extends Item>> list = new ArrayList<>();
                list.add(item);
                TAB_ITEMS.put(tab, list);
            } else {
                TAB_ITEMS.get(tab).add(item);
            }
        }
    }

    /**
     * Called when creative tabs gets populated with items.
     */
    public static void onCreativeTabPopulate(BuildCreativeModeTabContentsEvent event) {
        if (TAB_ITEMS.containsKey(event.getTabKey())) {
            List<RegistryObject<? extends Item>> items = TAB_ITEMS.get(event.getTabKey());
            items.forEach((regObj) -> event.accept(regObj.get()));
        }
    }
}