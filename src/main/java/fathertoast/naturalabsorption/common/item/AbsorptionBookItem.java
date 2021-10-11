package fathertoast.naturalabsorption.common.item;

import fathertoast.naturalabsorption.*;
import fathertoast.naturalabsorption.client.*;
import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.health.HealthData;
import fathertoast.naturalabsorption.common.health.HealthManager;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public
class AbsorptionBookItem extends Item {

	public AbsorptionBookItem( ) {
		super(new Item.Properties().stacksTo(1));
	}
	
	@SuppressWarnings( "WeakerAccess" )
	public static int getLevelCost( float capacity ) {
		return MathHelper.clamp(
			(int) (Config.get( ).ABSORPTION_UPGRADES.LEVEL_COST + Config.get( ).ABSORPTION_UPGRADES.LEVEL_COST_PER_POINT * capacity),
			0, Config.get( ).ABSORPTION_UPGRADES.LEVEL_COST_MAX
		);
	}
	
	@Override
	public ActionResult< ItemStack > use( World world, PlayerEntity player, Hand hand ) {
		if( !Config.get( ).ABSORPTION_HEALTH.ENABLED || !Config.get( ).ABSORPTION_UPGRADES.ENABLED ) {
			return super.use( world, player, hand );
		}
		
		ItemStack book = player.getItemInHand( hand );
		
		float currentCap = HealthManager.getAbsorptionCapacity( player );
		int   levelCost  = getLevelCost( currentCap );
		
		boolean isCreative = player.isCreative();
		if( currentCap < Config.get( ).ABSORPTION_UPGRADES.MAXIMUM && (isCreative || player.experienceLevel >= levelCost) ) {
			if( !isCreative ) {
				player.setItemInHand( hand, ItemStack.EMPTY );
				player.giveExperienceLevels( -levelCost );
			}
			if( !world.isClientSide ) {
				HealthData data = HealthData.get( player );
				data.setAbsorptionCapacity( currentCap + Config.get( ).ABSORPTION_UPGRADES.CAPACITY_GAIN );

				player.awardStat( Stats.ITEM_USED.get( this ) );
			}
			
			// play sound to show success
	        player.playSound( SoundEvents.PLAYER_LEVELUP, 1f, 1f );
	        
			return ActionResult.success( book );
		}
		
		// tell the player why right click failed
    	player.displayClientMessage( new TranslationTextComponent( References.NOT_ENOUGH_LEVELS, levelCost ), true );

		return ActionResult.fail( book );
	}
	
	@Override
	@OnlyIn( value = Dist.CLIENT)
	public void appendHoverText( ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag ) {
		float capacity = RenderEventHandler.ABSORPTION_CAPACITY;

		if( capacity >= 0.0F ) {
			PlayerEntity player = Minecraft.getInstance().player;
			
			final float maxCapacity = Config.get( ).ABSORPTION_UPGRADES.MAXIMUM;
			float gainOnUse   = Config.get( ).ABSORPTION_UPGRADES.CAPACITY_GAIN;
			if( gainOnUse > maxCapacity - capacity ) {
				gainOnUse = maxCapacity - capacity;
			}
			
			if( Config.get( ).ABSORPTION_UPGRADES.SHOW_INFO_IN_TOOLTIP ) {
				tooltip.add( translate( References.BOOK_CURRENT ) );
				tooltip.add( new TranslationTextComponent( TextFormatting.YELLOW + " " + prettyToString( capacity ) + " / " + prettyToString( maxCapacity ) ) );
			}

			if( gainOnUse > 0.0F ) {
				tooltip.add( new StringTextComponent( "" ) );
				tooltip.add( translate( References.BOOK_GAIN ) );
				tooltip.add( new TranslationTextComponent( TextFormatting.BLUE + " +" + prettyToString( gainOnUse ) + " " + translate( "max" ) ) );
				
				if( player != null ) {
					final int levelCost = getLevelCost( capacity );
					boolean   hasLevels = levelCost <= player.experienceLevel;
					tooltip.add( new StringTextComponent( "" ) );

					if( levelCost > 0 ) {
						tooltip.add( translate( References.BOOK_COST, levelCost ) );
					}
					if( hasLevels || player.isCreative() ) {
						tooltip.add( translate( References.BOOK_CAN_USE ) );
					}
					else {
						tooltip.add( translate( References.BOOK_NO_USE ) );
					}
				}
			}
			else {
				tooltip.add( new StringTextComponent("") );
				tooltip.add( translate( References.BOOK_NO_USE ) );
			}
		}
	}

	private ITextComponent translate( String key, Object... args ) {
		return new TranslationTextComponent( key, args );
	}
	
	private String prettyToString( float value ) {
		return Math.round( value ) == value ? Integer.toString( Math.round( value ) ) : Float.toString( value );
	}
	
	@Override
	public Rarity getRarity(ItemStack stack ) { return Rarity.UNCOMMON; }
}
