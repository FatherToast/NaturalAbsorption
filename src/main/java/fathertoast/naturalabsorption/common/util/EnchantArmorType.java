package fathertoast.naturalabsorption.common.util;

import net.minecraft.enchantment.EnchantmentType;

public enum EnchantArmorType {

    ALL( EnchantmentType.ARMOR ),
    HEAD( EnchantmentType.ARMOR_HEAD ),
    CHEST( EnchantmentType.ARMOR_CHEST ),
    LEGS( EnchantmentType.ARMOR_LEGS ),
    FEET( EnchantmentType.ARMOR_FEET );

    public final EnchantmentType parentValue;

    EnchantArmorType( EnchantmentType parent ) { parentValue = parent; }
}
