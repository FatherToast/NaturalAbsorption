package fathertoast.naturalabsorption.common.event;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.hearts.HeartManager;
import fathertoast.naturalabsorption.common.recipe.condition.BookRecipeCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class NAEventListener {
    
    private static final ResourceLocation ADV_BOOK_RECIPE = NaturalAbsorption.resourceLoc( "recipes/" + NAItems.ABSORPTION_BOOK.getId().getPath() );
    
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onAdvancement( AdvancementEvent.AdvancementEarnEvent event ) {
        if( HeartManager.isAbsorptionEnabled() && Config.ABSORPTION.NATURAL.upgradeBookRecipe.get() != BookRecipeCondition.Type.NONE ) {
            if( event.getAdvancement().getId().equals( ADV_BOOK_RECIPE ) ) {
                
                // The advancement for unlocking the book of absorption recipe
                ResourceLocation recipe = new ResourceLocation(
                        NaturalAbsorption.toString( NAItems.ABSORPTION_BOOK.get(), ForgeRegistries.ITEMS ) + "_" +
                                Config.ABSORPTION.NATURAL.upgradeBookRecipe.get().name().toLowerCase()
                );
                try {
                    event.getEntity().awardRecipesByKey( new ResourceLocation[] { recipe } );
                }
                catch( Exception ex ) {
                    NaturalAbsorption.LOG.warn( "Something went wrong trying to award a player the absorption book recipe! Aw man :(" );
                    ex.printStackTrace();
                }
            }
        }
    }
}