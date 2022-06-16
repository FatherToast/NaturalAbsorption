package fathertoast.naturalabsorption.common.enchantment;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.register.NAEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.DungeonHooks;

public
class AbsorptionEnchantment extends Enchantment {
    
    private static final EquipmentSlot[] VALID_SLOTS = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };
    
    public AbsorptionEnchantment() {
        super( Config.EQUIPMENT.ENCHANTMENT.rarity.get().parentValue, Config.EQUIPMENT.ENCHANTMENT.slot.get().parentValue, VALID_SLOTS );
    }
    
    /** @return The maximum absorption granted by enchantments. */
    public static float getMaxAbsorptionBonus( Player player ) {
        // Calculate enchantment level
        int enchantLevel;
        
        if( Config.EQUIPMENT.ENCHANTMENT.stacking.get() ) {
            Iterable<ItemStack> equipment = player.getArmorSlots();
            enchantLevel = 0;
            
            for( ItemStack itemStack : equipment ) {
                enchantLevel += EnchantmentHelper.getItemEnchantmentLevel( NAEnchantments.ABSORPTION_ENCHANTMENT.get(), itemStack );
            }
        }
        else {
            enchantLevel = EnchantmentHelper.getEnchantmentLevel( NAEnchantments.ABSORPTION_ENCHANTMENT.get(), player );
        }
        
        // Calculate capacity to grant for level
        if( enchantLevel > 0 ) {
            return Math.max( 0.0F, (float) Math.min(
                    Config.EQUIPMENT.ENCHANTMENT.potencyBase.get() + Config.EQUIPMENT.ENCHANTMENT.potencyPerLevel.get() * enchantLevel,
                    Config.EQUIPMENT.ENCHANTMENT.potencyMax.get() ) );
        }
        return 0.0F;
    }
    
    @Override
    public int getMinCost( int level ) {
        return Config.EQUIPMENT.ENCHANTMENT.costBase.get() + (level - 1) * Config.EQUIPMENT.ENCHANTMENT.costPerLevel.get();
    }
    
    @Override
    public int getMaxCost( int level ) { return getMinCost( level + 1 ); }
    
    @Override
    public int getMaxLevel() { return Config.EQUIPMENT.ENCHANTMENT.levelMax.get(); }
    
    @Override
    public boolean isAllowedOnBooks() { return Config.EQUIPMENT.ENCHANTMENT.allowOnBooks.get(); }
    
    @Override
    public boolean isTreasureOnly() { return Config.EQUIPMENT.ENCHANTMENT.treasureOnly.get(); }
    
    @Override
    public boolean isDiscoverable() { return Config.EQUIPMENT.ENCHANTMENT.enabled.get(); }
}