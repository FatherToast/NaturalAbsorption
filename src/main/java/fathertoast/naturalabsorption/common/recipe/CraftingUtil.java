package fathertoast.naturalabsorption.common.recipe;

import fathertoast.naturalabsorption.common.recipe.condition.BookRecipeCondition;
import fathertoast.naturalabsorption.common.recipe.condition.ConfigOptionCondition;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CraftingUtil {
    
    /**
     * Registers Natural Absorption's custom crafting conditions.
     * <br><br>
     * Called from {@link fathertoast.naturalabsorption.common.core.NaturalAbsorption#onCommonSetup(FMLCommonSetupEvent)}
     */
    @SuppressWarnings( "JavadocReference" )
    public static void registerConditions() {
        CraftingHelper.register( new BookRecipeCondition.Serializer() );
        CraftingHelper.register( new ConfigOptionCondition.Serializer() );
    }
}