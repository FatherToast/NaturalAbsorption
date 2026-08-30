package fathertoast.naturalabsorption.common.event;

import com.mojang.brigadier.CommandDispatcher;
import fathertoast.naturalabsorption.api.lib.NaturalAbsorptionObjects;
import fathertoast.naturalabsorption.common.command.NABaseCommand;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.config.Config;
import fathertoast.naturalabsorption.common.core.hearts.HeartManager;
import fathertoast.naturalabsorption.common.recipe.condition.BookRecipeCondition;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

@Mod.EventBusSubscriber( modid = NaturalAbsorption.MOD_ID )
public final class GameEventListener {
    
    private static final ResourceLocation ADV_BOOK_RECIPE = NaturalAbsorption.rl(
            "recipes/" + Objects.requireNonNull( NaturalAbsorptionObjects.Items.ABSORPTION_BOOK.getId() ).getPath()
    );
    
    
    /** Called when a player earns an advancement, unlocking it. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onAdvancement( AdvancementEvent.AdvancementEarnEvent event ) {
        if( HeartManager.isAbsorptionEnabled() && Config.ABSORPTION.NATURAL.upgradeBookRecipe.get() != BookRecipeCondition.Type.NONE ) {
            if( event.getAdvancement().getId().equals( ADV_BOOK_RECIPE ) ) {
                
                // The advancement for unlocking the book of absorption recipe
                ResourceLocation recipe = ResourceLocation.tryParse(
                        NaturalAbsorption.toString( NaturalAbsorptionObjects.Items.ABSORPTION_BOOK.get(), ForgeRegistries.ITEMS ) + "_" +
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
    
    /** Called each time commands are loaded. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void registerCommands( RegisterCommandsEvent event ) {
        final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        NABaseCommand.register( dispatcher );
    }
}