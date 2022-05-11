package fathertoast.naturalabsorption.datagen;

import com.google.common.collect.ImmutableList;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.core.register.NALootModifiers;
import fathertoast.naturalabsorption.common.loot.AddItemChanceModifier;
import net.minecraft.data.DataGenerator;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class NALootModifierProvider extends GlobalLootModifierProvider {

    public NALootModifierProvider(DataGenerator gen) {
        super(gen, NaturalAbsorption.MOD_ID);
    }

    @Override
    protected void start() {
        this.add("absorption_book", NALootModifiers.ADD_ITEM_CHANCE.get(), new AddItemChanceModifier(ImmutableList.of(new ResourceLocation("chests/simple_dungeon")), NAItems.ABSORPTION_BOOK.get(), 0.06, new ILootCondition[]{}));
    }
}
