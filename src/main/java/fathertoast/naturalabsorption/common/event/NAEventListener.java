package fathertoast.naturalabsorption.common.event;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.health.HeartData;
import fathertoast.naturalabsorption.common.health.HeartManager;
import fathertoast.naturalabsorption.common.network.NetworkHelper;
import fathertoast.naturalabsorption.common.recipe.condition.BookRecipeCondition;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NAEventListener {
    
    private static final ResourceLocation ADV_BOOK_RECIPE = NaturalAbsorption.resourceLoc( "recipes/" + NAItems.ABSORPTION_BOOK.getId().getPath() );
    
    
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
    
    /**
     * Update player absorption client side when
     * the player changes dimension.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onPlayerChangeDimension( PlayerEvent.PlayerChangedDimensionEvent event ) {
        if( !event.getPlayer().level.isClientSide ) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            NetworkHelper.setNaturalAbsorption( player, HeartData.get( player ).getNaturalAbsorption() );
        }
    }
    
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onItemTooltip( ItemTooltipEvent event ) {
        final Food food = event.getItemStack().getItem().getFoodProperties();
        if( food != null ) {
            final int hunger = food.getNutrition();
            final float saturation = HeartManager.calculateSaturation( hunger, food.getSaturationModifier() );
            
            if( Config.MAIN.GENERAL.foodExtraTooltipInfo.get() ) {
                // Food nutrition values could theoretically be zero or negative, make sure we handle that
                if( hunger != 0 ) {
                    event.getToolTip().add( new TranslationTextComponent( (hunger > 0 ? TextFormatting.BLUE : TextFormatting.RED) +
                            References.translate( References.FOOD_HUNGER, String.format( "%+d", -hunger ) ).getString() ) );
                }
                if( saturation != 0.0F ) {
                    event.getToolTip().add( new TranslationTextComponent( (saturation > 0.0F ? TextFormatting.BLUE : TextFormatting.RED) +
                            References.translate( References.FOOD_SATURATION, (saturation > 0.0F ? "+" : "") + References.prettyToString( saturation ) ).getString() ) );
                }
            }
            if( HeartManager.isHealthEnabled() && Config.HEALTH.GENERAL.foodHealingExtraTooltipInfo.get() ) {
                // Calculate as if the food's entire nutritional value is used
                final float maxHealing = Config.HEALTH.GENERAL.foodHealingMax.get() < 0.0 ? Float.POSITIVE_INFINITY :
                        (float) Config.HEALTH.GENERAL.foodHealingMax.get();
                final float healing = Math.min( HeartManager.getFoodHealing( hunger, saturation ), maxHealing );
                if( healing > 0.0F ) {
                    event.getToolTip().add( new TranslationTextComponent( TextFormatting.BLUE + References.translate( References.FOOD_HEALTH, "+" + References.prettyToString( healing ) ).getString() ) );
                }
            }
        }
    }
}