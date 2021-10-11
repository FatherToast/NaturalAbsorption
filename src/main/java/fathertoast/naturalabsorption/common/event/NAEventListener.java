package fathertoast.naturalabsorption.common.event;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.recipe.condition.BookRecipeCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NAEventListener {

    private static final ResourceLocation ADV_BOOK_RECIPE = new ResourceLocation(
            NaturalAbsorption.MOD_ID, "recipes/" + NAItems.ABSORPTION_BOOK.get( ).getRegistryName( ).getPath( )
    );

    @SubscribeEvent
    public void onAdvancement( AdvancementEvent event ) {
        if( Config.get( ).ABSORPTION_HEALTH.ENABLED && Config.get( ).ABSORPTION_UPGRADES.ENABLED && Config.get( ).ABSORPTION_UPGRADES.RECIPE != BookRecipeCondition.Type.NONE ) {
            if( event.getAdvancement( ).getId( ).equals( ADV_BOOK_RECIPE ) ) {
                // The advancement for unlocking the book of absorption recipe
                ResourceLocation recipe = new ResourceLocation(
                        NAItems.ABSORPTION_BOOK.get().getRegistryName( ).toString( ) + "_" +
                                Config.get( ).ABSORPTION_UPGRADES.RECIPE.name( ).toLowerCase( )
                );
                try {
                    event.getPlayer( ).awardRecipesByKey( new ResourceLocation[] { recipe } );
                }
                catch (Exception e) {
                    NaturalAbsorption.LOGGER.warn("Something went wrong trying to award a player the absorption book recipe! Aw man :(");
                    e.printStackTrace();
                }
            }
        }
    }
}
