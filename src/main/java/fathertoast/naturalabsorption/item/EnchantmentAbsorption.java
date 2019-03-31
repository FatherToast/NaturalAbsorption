package fathertoast.naturalabsorption.item;

import fathertoast.naturalabsorption.*;
import fathertoast.naturalabsorption.config.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public
class EnchantmentAbsorption extends Enchantment
{
	private static final EntityEquipmentSlot[] VALID_SLOTS = {
		EntityEquipmentSlot.FEET, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.HEAD
	};
	
	/** @return The absorption capacity granted by enchantments. */
	public static
	float getBonusCapacity( EntityPlayer player )
	{
		// Calculate enchantment level
		int absorbLevel = 0;
		if( Config.get( ).ENCHANTMENT.STACKING ) {
			Iterable< ItemStack > equipment = ModObjects.ENCHANTMENT_ABSORPTION.getEntityEquipment( player );
			for( ItemStack itemStack : equipment ) {
				absorbLevel += EnchantmentHelper.getEnchantmentLevel( ModObjects.ENCHANTMENT_ABSORPTION, itemStack );
			}
		}
		else {
			absorbLevel = EnchantmentHelper.getMaxEnchantmentLevel( ModObjects.ENCHANTMENT_ABSORPTION, player );
		}
		
		// Calculate capacity to grant for level
		if( absorbLevel > 0 ) {
			return Math.min(
				Config.get( ).ENCHANTMENT.POTENCY_BASE + Config.get( ).ENCHANTMENT.POTENCY * absorbLevel,
				Config.get( ).ENCHANTMENT.POTENCY_MAX
			);
		}
		return 0.0F;
	}
	
	public
	EnchantmentAbsorption( )
	{
		super( Config.get( ).ENCHANTMENT.RARITY.parentValue, Config.get( ).ENCHANTMENT.SLOT.parentValue, VALID_SLOTS );
	}
	
	/**
	 * Returns the minimal value of enchantability needed on the enchantment level passed.
	 */
	@Override
	public
	int getMinEnchantability( int enchantmentLevel )
	{
		return Config.get( ).ENCHANTMENT.ENCHANTIBILITY_BASE + (enchantmentLevel - 1) * Config.get( ).ENCHANTMENT.ENCHANTIBILITY_PER_LEVEL;
	}
	
	/**
	 * Returns the maximum value of enchantability nedded on the enchantment level passed.
	 */
	@Override
	public
	int getMaxEnchantability( int enchantmentLevel ) { return getMinEnchantability( enchantmentLevel + 1 ); }
	
	@Override
	public
	int getMaxLevel( ) { return Config.get( ).ENCHANTMENT.MAXIMUM_LEVEL; }
	
	@Override
	public
	boolean isAllowedOnBooks( ) { return Config.get( ).ENCHANTMENT.BOOKS; }
	
	@Override
	public
	boolean isTreasureEnchantment( ) { return Config.get( ).ENCHANTMENT.TREASURE; }
}
