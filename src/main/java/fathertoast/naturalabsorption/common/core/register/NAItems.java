package fathertoast.naturalabsorption.common.core.register;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.item.AbsorptionAbsorbingBookItem;
import fathertoast.naturalabsorption.common.item.AbsorptionBookItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NAItems {
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NaturalAbsorption.MOD_ID);
    
    public static final RegistryObject<Item> ABSORPTION_BOOK = ITEMS.register("absorption_book", AbsorptionBookItem::new);
    public static final RegistryObject<Item> ABSORPTION_ABSORBING_BOOK = ITEMS.register("absorption_absorbing_book", AbsorptionAbsorbingBookItem::new);
}