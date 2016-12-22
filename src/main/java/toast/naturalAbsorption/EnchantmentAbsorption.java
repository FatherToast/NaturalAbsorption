package toast.naturalAbsorption;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentAbsorption extends Enchantment {

	public EnchantmentAbsorption(Enchantment.Rarity rarity, EnumEnchantmentType type, EntityEquipmentSlot[] slots) {
		super(rarity, type, slots);
		this.setName("natural_absorption.absorb");
	}

	// Returns the minimal value of enchantability needed on the enchantment level passed.
	@Override
	public int getMinEnchantability(int level) {
		return 1 + 10 * (level - 1);
	}

	// Returns the maximum value of enchantability needed on the enchantment level passed.
	@Override
	public int getMaxEnchantability(int level) {
		return super.getMinEnchantability(level) + 50;
	}

	// Returns the maximum level that the enchantment can have.
	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public boolean isAllowedOnBooks() {
		return Properties.get().ENCHANT.BOOKS;
	}

	@Override
	public boolean isTreasureEnchantment() {
		return Properties.get().ENCHANT.TREASURE;
	}
}
