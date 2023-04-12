package fathertoast.naturalabsorption.common.item;

import fathertoast.naturalabsorption.common.config.Config;
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
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
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
                .tab( CreativeModeTab.TAB_COMBAT )
                .stacksTo( 1 ) );
    }
    
    @SuppressWarnings( "WeakerAccess" )
    public static int getLevelCost( double capacity ) {
        return Mth.clamp(
                (int) (Config.ABSORPTION.NATURAL.upgradeLevelCostBase.get() + Config.ABSORPTION.NATURAL.upgradeLevelCostPerPoint.get() * capacity),
                0, Config.ABSORPTION.NATURAL.upgradeLevelCostMax.get() );
    }

    @Override
    public InteractionResultHolder<ItemStack> use( Level level, Player player, InteractionHand hand ) {
        // Check if natural absorption can be upgraded
        if( !HeartManager.isAbsorptionEnabled() || Config.ABSORPTION.NATURAL.upgradeGain.get() <= 0.0 ) {
            return super.use( level, player, hand );
        }
        final boolean isCreative = player.isCreative();
        final ItemStack book = player.getItemInHand( hand );
        
        if( !level.isClientSide ) {
            final double naturalAbsorption = AbsorptionHelper.getBaseNaturalAbsorption( player );
            final int levelCost = getLevelCost( naturalAbsorption );
            
            // Give the player feedback on failure
            if( naturalAbsorption >= Config.ABSORPTION.NATURAL.maximumAmount.get() ) {
                player.displayClientMessage( Component.translatable( References.ALREADY_MAX ), true );
                return InteractionResultHolder.fail( book );
            }
            if( !isCreative && player.experienceLevel < levelCost ) {
                player.displayClientMessage( Component.translatable( References.NOT_ENOUGH_LEVELS, levelCost ), true );
                return InteractionResultHolder.fail( book );
            }
            // Consume costs
            if( !isCreative ) {
                book.shrink( 1 );
                player.giveExperienceLevels( -levelCost );
            }
            // Apply upgrade effects
            AbsorptionHelper.addBaseNaturalAbsorption( player, true, Config.ABSORPTION.NATURAL.upgradeGain.get() );
            player.awardStat( Stats.ITEM_USED.get( this ) );
            
            // Play sound to show success
            level.playSound( null, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.75F, 1.0F );
            return InteractionResultHolder.consume( book );
        }
        return InteractionResultHolder.success( book );
    }
    
    @Override
    @OnlyIn( value = Dist.CLIENT )
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag ) {
        final Player player = Minecraft.getInstance().player;
        
        if( player == null )
            return;
        
        final double naturalAbsorption = AbsorptionHelper.getBaseNaturalAbsorption( player );
        
        if( naturalAbsorption >= 0.0F ) {
            
            final double maxNaturalAbsorption = Config.ABSORPTION.NATURAL.maximumAmount.get();
            final double gainOnUse = naturalAbsorption >= maxNaturalAbsorption ? 0.0F :
                    Math.min( Config.ABSORPTION.NATURAL.upgradeGain.get(), maxNaturalAbsorption - naturalAbsorption );
            
            // Extra tooltip info, if enabled
            if( Config.ABSORPTION.NATURAL.upgradeBookExtraTooltipInfo.get() ) {
                tooltip.add( Component.translatable( ChatFormatting.GRAY + References.translate( References.ABSORPTION_BOOK_CURRENT ).getString() ) );
                tooltip.add( Component.translatable( ChatFormatting.YELLOW + " " +
                        References.prettyToString( (float) naturalAbsorption ) + " / " + References.prettyToString( (float) maxNaturalAbsorption ) ) );
            }
            tooltip.add( Component.literal( "" ) );
            
            if( gainOnUse > 0.0F ) {
                // Tell player how much absorption they gain on use
                tooltip.add( Component.translatable( ChatFormatting.GRAY + References.translate( References.BOOK_GAIN ).getString() ) );
                tooltip.add( Component.translatable( ChatFormatting.BLUE + References.translate( References.BOOK_MAX, "+" + References.prettyToString( (float) gainOnUse ) ).getString() ) );
                
                tooltip.add( Component.translatable( "" ) );
                
                // Provide feedback on cost and usability
                final int levelCost = getLevelCost( naturalAbsorption );
                if( levelCost > 0 ) {
                    tooltip.add( Component.translatable( ChatFormatting.GREEN + References.translate( References.ABSORPTION_BOOK_COST, levelCost ).getString() ) );
                }
                if( levelCost <= player.experienceLevel || player.isCreative() ) {
                    tooltip.add( Component.translatable( ChatFormatting.GRAY + References.translate( References.BOOK_CAN_USE ).getString() ) );
                }
                else {
                    tooltip.add( Component.translatable( ChatFormatting.RED + References.translate( References.BOOK_NO_USE ).getString() ) );
                }
            }
            else {
                tooltip.add( Component.translatable( ChatFormatting.RED + References.translate( References.BOOK_NO_USE ).getString() ) );
            }
        }
    }
    
    @Override
    public Rarity getRarity(ItemStack stack ) { return Rarity.RARE; }
}