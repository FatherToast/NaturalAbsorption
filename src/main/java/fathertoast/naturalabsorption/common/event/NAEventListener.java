package fathertoast.naturalabsorption.common.event;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.config.Config;
import fathertoast.naturalabsorption.common.core.hearts.HeartManager;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.recipe.condition.BookRecipeCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class NAEventListener {
    
    private static final ResourceLocation ADV_BOOK_RECIPE = NaturalAbsorption.rl( "recipes/" + Objects.requireNonNull( NAItems.ABSORPTION_BOOK.getId() ).getPath() );
    
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onAdvancement( AdvancementEvent.AdvancementEarnEvent event ) {
        if( HeartManager.isAbsorptionEnabled() && Config.ABSORPTION.NATURAL.upgradeBookRecipe.get() != BookRecipeCondition.Type.NONE ) {
            if( event.getAdvancement().getId().equals( ADV_BOOK_RECIPE ) ) {
                
                // The advancement for unlocking the book of absorption recipe
                ResourceLocation recipe = ResourceLocation.tryParse(
                        NaturalAbsorption.toString( NAItems.ABSORPTION_BOOK.get(), ForgeRegistries.ITEMS ) + "_" +
                                Config.ABSORPTION.NATURAL.upgradeBookRecipe.get().getSerializedName()
                );
                try {
                    event.getEntity().awardRecipesByKey( new ResourceLocation[] { recipe } );
                }
                catch( Exception ex ) {
                    NaturalAbsorption.LOG.warn( "Something went wrong trying to award a player the absorption book recipe! Aw man :(" );
                    // noinspection CallToPrintStackTrace
                    ex.printStackTrace();
                }
            }
        }
    }
}