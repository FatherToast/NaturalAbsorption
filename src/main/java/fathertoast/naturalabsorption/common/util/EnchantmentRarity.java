package fathertoast.naturalabsorption.common.util;

import net.minecraft.enchantment.Enchantment;

public enum EnchantmentRarity {

    COMMON( Enchantment.Rarity.COMMON ),
    UNCOMMON( Enchantment.Rarity.UNCOMMON ),
    RARE( Enchantment.Rarity.RARE ),
    VERY_RARE( Enchantment.Rarity.VERY_RARE );

    public final Enchantment.Rarity parentValue;

    EnchantmentRarity( Enchantment.Rarity parent ) { parentValue = parent; }
}
