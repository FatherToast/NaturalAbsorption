package fathertoast.naturalabsorption.common.item;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.hearts.AbsorptionHelper;
import fathertoast.naturalabsorption.common.hearts.HeartManager;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
                .stacksTo( 1 ) );
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use( Level level, Player player, InteractionHand hand ) {
        // Check if this item is enabled in the config
        if( !HeartManager.isAbsorptionEnabled() || !Config.ABSORPTION.NATURAL.spongeBookEnabled.get() ) {
            return super.use( level, player, hand );
        }
        final boolean isCreative = player.isCreative();
        final ItemStack spongeBook = player.getItemInHand( hand );
        
        if( !level.isClientSide ) {
            final double naturalAbsorption = AbsorptionHelper.getBaseNaturalAbsorption( player );
            
            if( naturalAbsorption > 0.0F ) {
                final float gainOnUse = (float) Config.ABSORPTION.NATURAL.upgradeGain.get();
                final double newAbsorption = Math.max( 0.0F, naturalAbsorption - gainOnUse );
                
                // Consume costs
                if( !isCreative ) {
                    spongeBook.shrink( 1 );
                    if( Config.ABSORPTION.NATURAL.spongeBookBookRefund.get() )
                        Block.popResource( level, player.blockPosition(), new ItemStack( NAItems.ABSORPTION_BOOK.get() ) );
                }
                
                // Apply downgrade effects and notify client
                final float levelRefundMulti = (float) Config.ABSORPTION.NATURAL.spongeBookLevelRefundMulti.get();
                if( levelRefundMulti > 0.0F ) {
                    final int levelsReturned = (int) (levelRefundMulti * getLevelCost( newAbsorption ));
                    if( levelsReturned > 0 ) player.giveExperienceLevels( levelsReturned );
                }
                AbsorptionHelper.addBaseNaturalAbsorption( player, true, -Config.ABSORPTION.NATURAL.upgradeGain.get() );
                player.awardStat( Stats.ITEM_USED.get( this ) );
                level.playSound( null, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), SoundEvents.WOOL_PLACE, SoundSource.PLAYERS, 0.9F, 1.0F );
                level.playSound( null, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.75F, 1.0F );
                
                return InteractionResultHolder.consume( spongeBook );
            }
            else {
                // Give the player feedback on failure
                player.displayClientMessage( Component.translatable( References.NOT_ENOUGH_ABSORPTION ), true );
                return InteractionResultHolder.fail( spongeBook );
            }
        }
        return InteractionResultHolder.success( spongeBook );
    }
    
    @Override
    @OnlyIn( value = Dist.CLIENT )
    public void appendHoverText( ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag ) {
        if( !Config.ABSORPTION.NATURAL.spongeBookEnabled.get() )
            return;
        
        final Player player = Minecraft.getInstance().player;
        
        if( player == null )
            return;
        
        final double naturalAbsorption = AbsorptionHelper.getBaseNaturalAbsorption( player );
        final float gainOnUse = (float) Config.ABSORPTION.NATURAL.upgradeGain.get();
        final float levelRefundMulti = (float) Config.ABSORPTION.NATURAL.spongeBookLevelRefundMulti.get();
        final int levelsReturned = (int) (levelRefundMulti * getLevelCost( naturalAbsorption - gainOnUse ));
        
        tooltip.add( Component.literal( "" ) );
        
        // Tell player how much absorption they lose on use
        tooltip.add( Component.translatable( ChatFormatting.GRAY + References.translate( References.BOOK_GAIN ).getString() ) );
        tooltip.add( Component.translatable( ChatFormatting.RED + References.translate( References.BOOK_MAX, "-" + References.prettyToString( gainOnUse ) ).getString() ) );
        
        tooltip.add( Component.literal( "" ) );
        
        // Provide feedback on usability
        if( levelRefundMulti > 0.0F ) {
            tooltip.add( Component.translatable( ChatFormatting.GREEN + References.translate( References.SPONGE_BOOK_REFUND, levelsReturned ).getString() ) );
        }
        if( naturalAbsorption > 0.0F ) {
            tooltip.add( Component.translatable( ChatFormatting.GRAY + References.translate( References.BOOK_CAN_USE ).getString() ) );
        }
        else {
            tooltip.add( Component.translatable( ChatFormatting.RED + References.translate( References.BOOK_NO_USE ).getString() ) );
        }
    }
    
    @Override
    public Rarity getRarity(ItemStack stack ) { return Rarity.RARE; }
}