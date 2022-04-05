package fathertoast.naturalabsorption.common.event;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.health.HeartManager;
import fathertoast.naturalabsorption.common.recipe.condition.BookRecipeCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NAEventListener {

    private static final ResourceLocation ADV_BOOK_RECIPE = NaturalAbsorption.resourceLoc(
            "recipes/" + NAItems.ABSORPTION_BOOK.getId().getPath() );
    
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onAdvancement( AdvancementEvent event ) {
        if( HeartManager.isAbsorptionEnabled() && Config.ABSORPTION.NATURAL.upgradeBookRecipe.get() != BookRecipeCondition.Type.NONE ) {
            if( event.getAdvancement().getId().equals( ADV_BOOK_RECIPE ) ) {
                // The advancement for unlocking the book of absorption recipe
                ResourceLocation recipe = new ResourceLocation(
                        NaturalAbsorption.toString( NAItems.ABSORPTION_BOOK.get().getRegistryName() ) + "_" +
                                Config.ABSORPTION.NATURAL.upgradeBookRecipe.get().name().toLowerCase()
                );
                try {
                    event.getPlayer().awardRecipesByKey( new ResourceLocation[] { recipe } );
                }
                catch( Exception e ) {
                    NaturalAbsorption.LOG.warn( "Something went wrong trying to award a player the absorption book recipe! Aw man :(" );
                    e.printStackTrace();
                }
            }
        }
    }
}