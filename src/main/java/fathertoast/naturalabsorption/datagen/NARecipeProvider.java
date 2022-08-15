package fathertoast.naturalabsorption.datagen;

import fathertoast.naturalabsorption.common.compat.tc.NAModifiers;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
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
    
    public NARecipeProvider( DataGenerator generator ) {
        super( generator );
    }
    
    @Override
    protected void buildShapelessRecipes( Consumer<IFinishedRecipe> consumer ) {
        ShapelessRecipeBuilder.shapeless( NAItems.ABSORPTION_ABSORBING_BOOK.get() )
                .requires( Items.BOOK )
                .requires( Items.SPONGE )
                .unlockedBy( "has_book", has( Items.BOOK ) )
                .unlockedBy( "has_sponge", has( Items.SPONGE ) )
                .save( consumer );
        
        this.tinkersRecipes( consumer );
    }
    
    private void tinkersRecipes( Consumer<IFinishedRecipe> consumer ) {
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

        modDependentRecipe( recipeConsumer -> {
            ModifierRecipeBuilder.modifier( NAModifiers.ARMOR_ABSORPTION.get() )
                    .addInput( NAItems.ABSORPTION_BOOK.get() )
                    .addSalvage( NAItems.ABSORPTION_BOOK.get(), 0.5f )
                    .setTools( TinkerTags.Items.ARMOR )
                    .setSlots( SlotType.DEFENSE, 1 )
                    .setMaxLevel( 3 )
                    .build( recipeConsumer, prefix( "tconstruct", NAModifiers.ARMOR_ABSORPTION, defenseFolder ) );
        }, consumer, false, "tconstruct");
    }


    public void modDependentRecipe( Consumer<Consumer<IFinishedRecipe>> recipeBuilder, Consumer<IFinishedRecipe> consumer, boolean advancement, String... modIds ) {
        final ConditionalRecipe.Builder builder = ConditionalRecipe.builder( );
        final ConditionalAdvancement.Builder advBuilder = ConditionalAdvancement.builder( );

        for ( String modid : modIds ) {
            ICondition condition = new ModLoadedCondition( modid );
            builder.addCondition( condition );
            advBuilder.addCondition( condition );
        }

        if ( advancement ) {
            recipeBuilder.accept(finishedRecipe -> {
                builder.addRecipe(finishedRecipe)
                        .setAdvancement(advBuilder.addAdvancement(finishedRecipe))
                        .build(consumer, finishedRecipe.getId());
            });
        }
        else {
            recipeBuilder.accept(finishedRecipe -> {
                builder.addRecipe(finishedRecipe)
                        .build(consumer, finishedRecipe.getId());
            });
        }
    }
    
    
    public ResourceLocation prefix( String modid, Supplier<? extends IForgeRegistryEntry<?>> entry, String prefix ) {
        ResourceLocation loc = Objects.requireNonNull( entry.get().getRegistryName() );
        return new ResourceLocation( modid, prefix + loc.getPath() );
    }
}