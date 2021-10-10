package fathertoast.naturalabsorption;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;

public class ModObjects {
	
	@SuppressWarnings( "ConstantConditions" )
	private static final ResourceLocation ADV_BOOK_RECIPE = new ResourceLocation(
		NaturalAbsorptionMod.MOD_ID, "recipes/" + BOOK_ABSORPTION.getRegistryName( ).getResourcePath( )
	);
	
	private static
	< T extends Item > T addInfo( String name, CreativeTabs tab, T item )
	{
		item.setRegistryName( NaturalAbsorptionMod.MOD_ID, name ).setUnlocalizedName( NaturalAbsorptionMod.LANG_KEY + name ).setCreativeTab( tab );
		return item;
	}
	
	private static
	< T extends Enchantment > T addInfo( String name, T enchant )
	{
		enchant.setRegistryName( NaturalAbsorptionMod.MOD_ID, name ).setName( NaturalAbsorptionMod.LANG_KEY + name );
		return enchant;
	}
	
	@SubscribeEvent
	public
	void registerItems( RegistryEvent.Register< Item > event )
	{
		if( Config.get( ).ABSORPTION_HEALTH.ENABLED && Config.get( ).ABSORPTION_UPGRADES.ENABLED ) {
			event.getRegistry( ).register( BOOK_ABSORPTION );
		}
	}
	
	@SubscribeEvent
	public
	void registerEnchantments( RegistryEvent.Register< Enchantment > event )
	{
		if( Config.get( ).ABSORPTION_HEALTH.ENABLED && Config.get( ).ENCHANTMENT.ENABLED ) {
			event.getRegistry( ).register( ENCHANTMENT_ABSORPTION );
		}
	}
	
	@SubscribeEvent
	public
	void registerModels( ModelRegistryEvent event )
	{
		if( Config.get( ).ABSORPTION_HEALTH.ENABLED && Config.get( ).ABSORPTION_UPGRADES.ENABLED ) {
			NaturalAbsorptionMod.sidedProxy.registerRenderers( );
		}
	}
	
	@SubscribeEvent
	public
	void onAdvancement( AdvancementEvent event )
	{
		if( Config.get( ).ABSORPTION_HEALTH.ENABLED && Config.get( ).ABSORPTION_UPGRADES.ENABLED && Config.get( ).ABSORPTION_UPGRADES.RECIPE != RecipeStyle.Type.NONE ) {
			if( event.getAdvancement( ).getId( ).equals( ADV_BOOK_RECIPE ) ) {
				// The advancement for unlocking the book of absorption recipe
				ResourceLocation recipe = new ResourceLocation(
					BOOK_ABSORPTION.getRegistryName( ).toString( ) + "_" +
					Config.get( ).ABSORPTION_UPGRADES.RECIPE.name( ).toLowerCase( )
				);
				if ( recipe != null ) {
					event.getEntityPlayer( ).unlockRecipes( new ResourceLocation[] { recipe } );
				}
			}
		}
	}
	
	@SuppressWarnings( "unused" )
	public
	enum EnchantArmorType
	{
		ALL( EnchantmentType.ARMOR ),
		HEAD( EnchantmentType.ARMOR_HEAD ),
		CHEST( EnchantmentType.ARMOR_CHEST ),
		LEGS( EnchantmentType.ARMOR_LEGS ),
		FEET( EnchantmentType.ARMOR_FEET );
		
		public final EnchantmentType parentValue;
		
		EnchantArmorType( EnchantmentType parent ) { parentValue = parent; }
	}
	
	@SuppressWarnings( "unused" )
	public
	enum EnchantRarity
	{
		COMMON( Enchantment.Rarity.COMMON ),
		UNCOMMON( Enchantment.Rarity.UNCOMMON ),
		RARE( Enchantment.Rarity.RARE ),
		VERY_RARE( Enchantment.Rarity.VERY_RARE );
		
		public final Enchantment.Rarity parentValue;
		
		EnchantRarity( Enchantment.Rarity parent ) { parentValue = parent; }
	}
}
