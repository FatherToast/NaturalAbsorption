package fathertoast.naturalabsorption.common.item;

import fathertoast.naturalabsorption.client.RenderEventHandler;
import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.health.HeartData;
import fathertoast.naturalabsorption.common.health.HeartManager;
import fathertoast.naturalabsorption.common.util.References;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AbsorptionBookItem extends Item {
    
    public AbsorptionBookItem() {
        super( new Item.Properties()
                .tab( ItemGroup.TAB_COMBAT )
                .stacksTo( 1 ) );
    }
    
    @SuppressWarnings( "WeakerAccess" )
    public static int getLevelCost( float capacity ) {
        return MathHelper.clamp(
                (int) (Config.ABSORPTION.NATURAL.upgradeLevelCostBase.get() + Config.ABSORPTION.NATURAL.upgradeLevelCostPerPoint.get() * capacity),
                0, Config.ABSORPTION.NATURAL.upgradeLevelCostMax.get() );
    }
    
    @Override
    public ActionResult<ItemStack> use( World world, PlayerEntity player, Hand hand ) {
        // Check if natural absorption can be upgraded
        if( !HeartManager.isAbsorptionEnabled() || Config.ABSORPTION.NATURAL.upgradeGain.get() <= 0.0 ) {
            return super.use( world, player, hand );
        }
        final boolean isCreative = player.isCreative();
        final ItemStack book = player.getItemInHand( hand );
        
        if( !world.isClientSide ) {
            final HeartData data = HeartData.get( player );
            
            final float naturalAbsorption = data.getNaturalAbsorption();
            final int levelCost = getLevelCost( naturalAbsorption );
            
            // Give the player feedback on failure
            if( naturalAbsorption >= Config.ABSORPTION.NATURAL.maximumAmount.get() ) {
                player.displayClientMessage( new TranslationTextComponent( References.ALREADY_MAX ), true );
                return ActionResult.fail( book );
            }
            if( !isCreative && player.experienceLevel < levelCost ) {
                player.displayClientMessage( new TranslationTextComponent( References.NOT_ENOUGH_LEVELS, levelCost ), true );
                return ActionResult.fail( book );
            }
            // Consume costs
            if( !isCreative ) {
                book.shrink( 1 );
                player.giveExperienceLevels( -levelCost );
            }
            
            // Apply upgrade effects and notify client
            data.setNaturalAbsorption( naturalAbsorption + (float) Config.ABSORPTION.NATURAL.upgradeGain.get(), true );
            player.awardStat( Stats.ITEM_USED.get( this ) );
            world.playSound( null, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.75F, 1.0F );
            
            return ActionResult.consume( book );
        }
        return ActionResult.success( book );
    }
    
    @Override
    @OnlyIn( value = Dist.CLIENT )
    public void appendHoverText( ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag ) {
        final PlayerEntity player = Minecraft.getInstance().player;
        
        if( player == null )
            return;
        
        final float naturalAbsorption = RenderEventHandler.PLAYER_NATURAL_ABSORPTION;
        
        if( naturalAbsorption >= 0.0F ) {
            
            final float maxNaturalAbsorption = (float) Config.ABSORPTION.NATURAL.maximumAmount.get();
            final float gainOnUse = naturalAbsorption >= maxNaturalAbsorption ? 0.0F :
                    Math.min( (float) Config.ABSORPTION.NATURAL.upgradeGain.get(), maxNaturalAbsorption - naturalAbsorption );
            
            // Extra tooltip info, if enabled
            if( Config.ABSORPTION.NATURAL.upgradeBookExtraTooltipInfo.get() ) {
                tooltip.add( new TranslationTextComponent( TextFormatting.GRAY + References.translate( References.ABSORPTION_BOOK_CURRENT ).getString() ) );
                tooltip.add( new TranslationTextComponent( TextFormatting.YELLOW + " " +
                        References.prettyToString( naturalAbsorption ) + " / " + References.prettyToString( maxNaturalAbsorption ) ) );
            }
            tooltip.add( new StringTextComponent( "" ) );
            
            if( gainOnUse > 0.0F ) {
                // Tell player how much absorption they gain on use
                tooltip.add( new TranslationTextComponent( TextFormatting.GRAY + References.translate( References.BOOK_GAIN ).getString() ) );
                tooltip.add( new TranslationTextComponent( TextFormatting.BLUE + References.translate( References.BOOK_MAX, "+" + References.prettyToString( gainOnUse ) ).getString() ) );
                
                tooltip.add( new StringTextComponent( "" ) );
                
                // Provide feedback on cost and usability
                final int levelCost = getLevelCost( naturalAbsorption );
                if( levelCost > 0 ) {
                    tooltip.add( new TranslationTextComponent( TextFormatting.GREEN + References.translate( References.ABSORPTION_BOOK_COST, levelCost ).getString() ) );
                }
                if( levelCost <= player.experienceLevel || player.isCreative() ) {
                    tooltip.add( new TranslationTextComponent( TextFormatting.GRAY + References.translate( References.BOOK_CAN_USE ).getString() ) );
                }
                else {
                    tooltip.add( new TranslationTextComponent( TextFormatting.RED + References.translate( References.BOOK_NO_USE ).getString() ) );
                }
            }
            else {
                tooltip.add( new TranslationTextComponent( TextFormatting.RED + References.translate( References.BOOK_NO_USE ).getString() ) );
            }
        }
    }
    
    @Override
    public Rarity getRarity( ItemStack stack ) { return Rarity.RARE; }
}