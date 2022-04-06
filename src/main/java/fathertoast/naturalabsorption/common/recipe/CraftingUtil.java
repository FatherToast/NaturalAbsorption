package fathertoast.naturalabsorption.common.recipe;

import fathertoast.naturalabsorption.common.recipe.condition.BookRecipeCondition;
import fathertoast.naturalabsorption.common.recipe.condition.ConfigOptionCondition;
import net.minecraftforge.common.crafting.CraftingHelper;

public class CraftingUtil {

    public static void registerConditions( ) {
        CraftingHelper.register(new BookRecipeCondition.Serializer());
        CraftingHelper.register(new ConfigOptionCondition.Serializer());
    }
}