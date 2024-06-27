package fathertoast.naturalabsorption.datagen;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class NALootModifierProvider extends GlobalLootModifierProvider {

    public NALootModifierProvider(DataGenerator gen) {
        super(gen.getPackOutput(), NaturalAbsorption.MOD_ID);
    }

    @Override
    protected void start() {
    }
}
