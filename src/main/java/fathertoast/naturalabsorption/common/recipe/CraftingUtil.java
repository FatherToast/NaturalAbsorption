package fathertoast.naturalabsorption.common.recipe;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.recipe.condition.BookRecipeCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

public class CraftingUtil {

    public static void registerConditions( ) {
        for ( BookRecipeCondition.Type type : BookRecipeCondition.Type.values( )) {
            CraftingHelper.register( new BookRecipeCondition.Serializer( new ResourceLocation( NaturalAbsorption.MOD_ID, type.getName() )));
        }
    }
}
