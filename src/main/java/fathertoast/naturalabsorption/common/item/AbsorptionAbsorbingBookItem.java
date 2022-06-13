package fathertoast.naturalabsorption.common.item;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.hearts.AbsorptionHelper;
import fathertoast.naturalabsorption.common.hearts.HeartManager;
import fathertoast.naturalabsorption.common.util.References;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
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

import static fathertoast.naturalabsorption.common.item.AbsorptionBookItem.getLevelCost;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AbsorptionAbsorbingBookItem extends Item {
    
    public AbsorptionAbsorbingBookItem() {
        super( new Item.Properties()
                .tab( ItemGroup.TAB_COMBAT )
                .stacksTo( 1 ) );
    }
    
    @Override
    public ActionResult<ItemStack> use( World world, PlayerEntity player, Hand hand ) {
        // Check if this item is enabled in the config
        if( !HeartManager.isAbsorptionEnabled() || !Config.ABSORPTION.NATURAL.spongeBookEnabled.get() ) {
            return super.use( world, player, hand );
        }
        final boolean isCreative = player.isCreative();
        final ItemStack spongeBook = player.getItemInHand( hand );
        
        if( !world.isClientSide ) {
            final double naturalAbsorption = AbsorptionHelper.getBaseNaturalAbsorption( player );
            
            if( naturalAbsorption > 0.0F ) {
                final float gainOnUse = (float) Config.ABSORPTION.NATURAL.upgradeGain.get();
                final double newAbsorption = Math.max( 0.0F, naturalAbsorption - gainOnUse );
                
                // Consume costs
                if( !isCreative ) {
                    spongeBook.shrink( 1 );
                    Block.popResource( world, player.blockPosition(), new ItemStack( NAItems.ABSORPTION_BOOK.get() ) );
                }
                
                // Apply downgrade effects and notify client
                final float levelRefundMulti = (float) Config.ABSORPTION.NATURAL.spongeBookLevelRefundMulti.get();
                if( levelRefundMulti > 0.0F ) {
                    final int levelsReturned = (int) (levelRefundMulti * getLevelCost( newAbsorption ));
                    if( levelsReturned > 0 ) player.giveExperienceLevels( levelsReturned );
                }
                AbsorptionHelper.addBaseNaturalAbsorption( player, true, -Config.ABSORPTION.NATURAL.upgradeGain.get() );
                player.awardStat( Stats.ITEM_USED.get( this ) );
                world.playSound( null, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), SoundEvents.WOOL_PLACE, SoundCategory.PLAYERS, 0.9F, 1.0F );
                world.playSound( null, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.75F, 1.0F );
                
                return ActionResult.consume( spongeBook );
            }
            else {
                // Give the player feedback on failure
                player.displayClientMessage( new TranslationTextComponent( References.NOT_ENOUGH_ABSORPTION ), true );
                return ActionResult.fail( spongeBook );
            }
        }
        return ActionResult.success( spongeBook );
    }
    
    @Override
    @OnlyIn( value = Dist.CLIENT )
    public void appendHoverText( ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag ) {
        if( !Config.ABSORPTION.NATURAL.spongeBookEnabled.get() )
            return;
        
        final PlayerEntity player = Minecraft.getInstance().player;
        
        if( player == null )
            return;
        
        final double naturalAbsorption = AbsorptionHelper.getBaseNaturalAbsorption( player );
        final float gainOnUse = (float) Config.ABSORPTION.NATURAL.upgradeGain.get();
        final float levelRefundMulti = (float) Config.ABSORPTION.NATURAL.spongeBookLevelRefundMulti.get();
        final int levelsReturned = (int) (levelRefundMulti * getLevelCost( naturalAbsorption - gainOnUse ));
        
        tooltip.add( new StringTextComponent( "" ) );
        
        // Tell player how much absorption they lose on use
        tooltip.add( new TranslationTextComponent( TextFormatting.GRAY + References.translate( References.BOOK_GAIN ).getString() ) );
        tooltip.add( new TranslationTextComponent( TextFormatting.RED + References.translate( References.BOOK_MAX, "-" + References.prettyToString( gainOnUse ) ).getString() ) );
        
        tooltip.add( new StringTextComponent( "" ) );
        
        // Provide feedback on usability
        if( levelRefundMulti > 0.0F ) {
            tooltip.add( new TranslationTextComponent( TextFormatting.GREEN + References.translate( References.SPONGE_BOOK_REFUND, levelsReturned ).getString() ) );
        }
        if( naturalAbsorption > 0.0F ) {
            tooltip.add( new TranslationTextComponent( TextFormatting.GRAY + References.translate( References.BOOK_CAN_USE ).getString() ) );
        }
        else {
            tooltip.add( new TranslationTextComponent( TextFormatting.RED + References.translate( References.BOOK_NO_USE ).getString() ) );
        }
    }
    
    @Override
    public Rarity getRarity( ItemStack stack ) { return Rarity.RARE; }
}