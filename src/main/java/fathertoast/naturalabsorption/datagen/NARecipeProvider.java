package fathertoast.naturalabsorption.datagen;

import fathertoast.naturalabsorption.common.compat.tc.NAModifiers;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NARecipeProvider extends RecipeProvider {

    public NARecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(NAItems.ABSORPTION_ABSORBING_BOOK.get())
                .requires(Items.BOOK)
                .requires(Items.SPONGE)
                .unlockedBy("has_book", has(Items.BOOK))
                .unlockedBy("has_sponge", has(Items.SPONGE))
                .save(consumer);

        this.handleDependencyRecipes(consumer);
    }

    private void handleDependencyRecipes(Consumer<IFinishedRecipe> consumer) {
        if (ModList.get().isLoaded("tconstruct")) {
            this.tinkersRecipes(consumer);
        }
    }

    private void tinkersRecipes(Consumer<IFinishedRecipe> consumer) {
        final String upgradeFolder = "tools/modifiers/upgrade/";
        final String abilityFolder = "tools/modifiers/ability/";
        final String slotlessFolder = "tools/modifiers/slotless/";
        final String upgradeSalvage = "tools/modifiers/salvage/upgrade/";
        final String abilitySalvage = "tools/modifiers/salvage/ability/";
        final String slotlessSalvage = "tools/modifiers/salvage/slotless/";
        final String defenseFolder = "tools/modifiers/defense/";
        final String defenseSalvage = "tools/modifiers/salvage/defense/";
        final String compatFolder = "tools/modifiers/compat/";
        final String compatSalvage = "tools/modifiers/salvage/compat/";

        ModifierRecipeBuilder.modifier(NAModifiers.ARMOR_ABSORPTION.get())
                .addInput(NAItems.ABSORPTION_BOOK.get())
                .addSalvage(NAItems.ABSORPTION_BOOK.get(), 0.5f)
                .setTools(TinkerTags.Items.ARMOR)
                .setSlots(SlotType.UPGRADE, 1)
                .setMaxLevel(3)
                .build(consumer, prefix("tconstruct", NAModifiers.ARMOR_ABSORPTION, upgradeFolder));
    }


    public ResourceLocation prefix(String modid, Supplier<? extends IForgeRegistryEntry<?>> entry, String prefix) {
        ResourceLocation loc = Objects.requireNonNull(entry.get().getRegistryName());
        return new ResourceLocation(modid, prefix + loc.getPath());
    }
}
