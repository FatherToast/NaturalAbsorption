package fathertoast.naturalabsorption.datagen;

import fathertoast.naturalabsorption.common.compat.tc.NAModifiers;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NARecipeProvider extends RecipeProvider {
    
    public NARecipeProvider( DataGenerator generator ) {
        super( generator );
    }
    
    @Override
    protected void buildCraftingRecipes( Consumer<FinishedRecipe> consumer ) {
        ShapelessRecipeBuilder.shapeless( NAItems.ABSORPTION_ABSORBING_BOOK.get() )
                .requires( Items.BOOK )
                .requires( Items.SPONGE )
                .unlockedBy( "has_book", has( Items.BOOK ) )
                .unlockedBy( "has_sponge", has( Items.SPONGE ) )
                .save( consumer );
        
        this.handleDependencyRecipes( consumer );
    }
    
    private void handleDependencyRecipes( Consumer<FinishedRecipe> consumer ) {
        if( ModList.get().isLoaded( "tconstruct" ) ) {
            //this.tinkersRecipes( consumer );
        }
    }

    // TODO - Wait for TC to update
    /*
    private void tinkersRecipes( Consumer<FinishedRecipe> consumer ) {
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
        
        ModifierRecipeBuilder.modifier( NAModifiers.ARMOR_ABSORPTION_ID )
                .addInput( NAItems.ABSORPTION_BOOK.get() )
                .setTools( TinkerTags.Items.ARMOR )
                .setSlots( SlotType.DEFENSE, 1 )
                .setMaxLevel( 3 )
                .save( consumer, prefix( "tconstruct", NAModifiers.ARMOR_ABSORPTION_ID, upgradeFolder ) );
    }
    
    
    public ResourceLocation prefix(String modid, ModifierId modifierId, String prefix ) {
        ResourceLocation loc = Objects.requireNonNull( modifierId );
        return new ResourceLocation( modid, prefix + loc.getPath() );
    }

     */
}