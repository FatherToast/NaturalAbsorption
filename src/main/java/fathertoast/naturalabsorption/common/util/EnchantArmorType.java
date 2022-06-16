package fathertoast.naturalabsorption.common.util;

import net.minecraft.world.item.enchantment.EnchantmentCategory;

public enum EnchantArmorType {
    
    ALL( EnchantmentCategory.ARMOR ),
    HEAD( EnchantmentCategory.ARMOR_HEAD ),
    CHEST( EnchantmentCategory.ARMOR_CHEST ),
    LEGS( EnchantmentCategory.ARMOR_LEGS ),
    FEET( EnchantmentCategory.ARMOR_FEET );
    
    public final EnchantmentCategory parentValue;
    
    EnchantArmorType( EnchantmentCategory parent ) { parentValue = parent; }
}