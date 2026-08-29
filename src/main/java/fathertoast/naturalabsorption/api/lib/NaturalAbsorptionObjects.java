package fathertoast.naturalabsorption.api.lib;

import com.mojang.serialization.Codec;
import fathertoast.naturalabsorption.api.INaturalAbsorptionApi;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

/** This helper class contains references/getters for all registry objects provided by Natural Absorption. */
public class NaturalAbsorptionObjects {
    
    
    /** The items provided by Natural Absorption. */
    public interface Items {
        RegistryObject<Item> ABSORPTION_BOOK = item( "absorption_book" );
        RegistryObject<Item> ABSORPTION_ABSORBING_BOOK = item( "absorption_absorbing_book" );
    }
    
    /** The enchantments provided by Natural Absorption. */
    public interface Enchantments {
        RegistryObject<Enchantment> ABSORPTION = enchantment( "absorption" );
    }
    
    /** The attributes provided by Natural Absorption. */
    public interface Attributes {
        RegistryObject<Attribute> NATURAL_ABSORPTION = attribute( "entity.natural_absorption" );
        RegistryObject<Attribute> EQUIPMENT_ABSORPTION = attribute( "entity.equipment_absorption" );
    }
    
    /** The global loot modifier serializers provided by Natural Absorption. */
    public interface GLMSerializers {
        RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_WITH_CHANCE = lootModSerializer( "add_with_chance" );
    }
    
    // ---- Internal Methods ---- //
    
    /** @return An object holder for an item. */
    private static RegistryObject<Item> item( String name ) { return ro( name, ForgeRegistries.ITEMS ); }
    
    /** @return An object holder for an enchantment. */
    @SuppressWarnings( "SameParameterValue" )
    private static RegistryObject<Enchantment> enchantment( String name ) { return ro( name, ForgeRegistries.ENCHANTMENTS ); }
    
    /** @return An object holder for an attribute. */
    private static RegistryObject<Attribute> attribute( String name ) { return ro( name, ForgeRegistries.ATTRIBUTES ); }
    
    /** @return An object holder for an attribute. */
    @SuppressWarnings( "SameParameterValue" )
    private static RegistryObject<Codec<? extends IGlobalLootModifier>> lootModSerializer( String name ) { return ro( name, ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS ); }
    
    /** @return An object holder for a Forge registry object. */
    private static <R, T extends R> RegistryObject<T> ro( String name, IForgeRegistry<R> reg ) {
        return RegistryObject.create( rl( name ), reg );
    }
    
    /** @return An object holder for a vanilla/custom registry object. */
    @SuppressWarnings( "SameParameterValue" )
    private static <T> RegistryObject<T> ro( String name, ResourceKey<? extends Registry<T>> registryKey ) {
        return RegistryObject.createOptional( rl( name ), registryKey, INaturalAbsorptionApi.MOD_ID );
    }
    
    /** @return A resource location of Natural Absorption's namespace and the given path. */
    private static ResourceLocation rl( String path ) {
        return ResourceLocation.fromNamespaceAndPath( INaturalAbsorptionApi.MOD_ID, path );
    }
    
    
    // Utility class
    private NaturalAbsorptionObjects() { }
}
