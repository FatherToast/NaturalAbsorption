package toast.naturalAbsorption;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;

public class EnchantmentAbsorption extends Enchantment {
    public EnchantmentAbsorption(int id, int weight) {
        super(id, weight, EnumEnchantmentType.armor);
        this.setName("NaturalAbsorption.absorb");
        Enchantment.addToBookList(this);
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
}