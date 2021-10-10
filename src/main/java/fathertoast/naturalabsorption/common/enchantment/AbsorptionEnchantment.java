package fathertoast.naturalabsorption.common.enchantment;

import fathertoast.naturalabsorption.*;
import fathertoast.naturalabsorption.common.config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public
class AbsorptionEnchantment extends Enchantment {

	private static final EquipmentSlotType[] VALID_SLOTS = {
			EquipmentSlotType.FEET, EquipmentSlotType.LEGS, EquipmentSlotType.CHEST, EquipmentSlotType.HEAD
	};
	
	/** @return The absorption capacity granted by enchantments. */
	public static float getBonusCapacity( PlayerEntity player ) {
		// Calculate enchantment level
		int absorbLevel = 0;

		if( Config.get( ).ENCHANTMENT.STACKING ) {
		Iterable< ItemStack > equipment = player.getArmorSlots();

			for( ItemStack itemStack : equipment ) {
				absorbLevel += EnchantmentHelper.getItemEnchantmentLevel( ModObjects.ENCHANTMENT_ABSORPTION, itemStack );
			}
		}
		else {
			absorbLevel = EnchantmentHelper.getEnchantmentLevel( ModObjects.ENCHANTMENT_ABSORPTION, player );
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
	
	public AbsorptionEnchantment( ) {
		super( Config.get( ).ENCHANTMENT.RARITY.parentValue, Config.get( ).ENCHANTMENT.SLOT.parentValue, VALID_SLOTS );
	}
	
	/**
	 * Returns the minimal value of enchantability needed on the enchantment level passed.
	 */
	/*
	@Override
	public int getMinEnchantability( int enchantmentLevel ) {
		return Config.get( ).ENCHANTMENT.ENCHANTIBILITY_BASE + (enchantmentLevel - 1) * Config.get( ).ENCHANTMENT.ENCHANTIBILITY_PER_LEVEL;
	}

	 */
	
	/**
	 * Returns the maximum value of enchantability needed on the enchantment level passed.
	 */
	/*
	@Override
	public int getMaxEnchantability( int enchantmentLevel ) { return getMinEnchantability( enchantmentLevel + 1 ); }

	 */
	
	@Override
	public int getMaxLevel( ) { return Config.get( ).ENCHANTMENT.MAXIMUM_LEVEL; }
	
	@Override
	public
	boolean isAllowedOnBooks( ) { return Config.get( ).ENCHANTMENT.BOOKS; }
	
	@Override
	public boolean isTreasureOnly( ) { return Config.get( ).ENCHANTMENT.TREASURE; }
}
