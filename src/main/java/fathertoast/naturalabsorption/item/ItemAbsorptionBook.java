package fathertoast.naturalabsorption.item;

import fathertoast.naturalabsorption.*;
import fathertoast.naturalabsorption.client.*;
import fathertoast.naturalabsorption.config.*;
import fathertoast.naturalabsorption.health.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public
class ItemAbsorptionBook extends Item
{
	public
	ItemAbsorptionBook( )
	{
		setMaxStackSize( 1 );
	}
	
	@SuppressWarnings( "WeakerAccess" )
	public static
	int getLevelCost( float capacity )
	{
		return MathHelper.clamp(
			(int) (Config.get( ).ABSORPTION_UPGRADES.LEVEL_COST + Config.get( ).ABSORPTION_UPGRADES.LEVEL_COST_PER_POINT * capacity),
			0, Config.get( ).ABSORPTION_UPGRADES.LEVEL_COST_MAX
		);
	}
	
	@Override
	public
	ActionResult< ItemStack > onItemRightClick( World world, EntityPlayer player, EnumHand hand )
	{
		if( !Config.get( ).ABSORPTION_HEALTH.ENABLED || !Config.get( ).ABSORPTION_UPGRADES.ENABLED ) {
			return super.onItemRightClick( world, player, hand );
		}
		
		ItemStack book = player.getHeldItem( hand );
		
		float currentCap = NaturalAbsorptionMod.sidedProxy.getAbsorptionCapacity( player );
		int   levelCost  = getLevelCost( currentCap );
		
		boolean isCreative = player.capabilities.isCreativeMode;
		if( currentCap < Config.get( ).ABSORPTION_UPGRADES.MAXIMUM && (isCreative || player.experienceLevel >= levelCost) ) {
			if( !isCreative ) {
				player.setHeldItem( hand, ItemStack.EMPTY );
				player.addExperienceLevel( -levelCost );
			}
			if( !world.isRemote ) {
				HealthData data = HealthData.get( player );
				data.setAbsorptionCapacity( currentCap + Config.get( ).ABSORPTION_UPGRADES.CAPACITY_GAIN );
				
				//noinspection ConstantConditions
				player.addStat( StatList.getObjectUseStats( this ) );
			}
			
			// play sound to show success
	        player.playSound( SoundEvents.ENTITY_PLAYER_LEVELUP, 1f, 1f );
	        
			return ActionResult.newResult( EnumActionResult.SUCCESS, book );
		}
		
		// tell the player why right click failed
    	player.sendStatusMessage( new TextComponentTranslation( "item.naturalabsorption.book_absorption.not_enough_levels", levelCost ), true );
    	
		return ActionResult.newResult( EnumActionResult.FAIL, book );
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public
	void addInformation( ItemStack stack, World world, List< String > tooltip, ITooltipFlag flag )
	{
		float capacity = ClientProxy.clientAbsorptionCapacity;
		if( capacity >= 0.0F ) {
			EntityPlayer player = NaturalAbsorptionMod.sidedProxy.getPlayer( );
			
			final float maxCapacity = Config.get( ).ABSORPTION_UPGRADES.MAXIMUM;
			float       gainOnUse   = Config.get( ).ABSORPTION_UPGRADES.CAPACITY_GAIN;
			if( gainOnUse > maxCapacity - capacity ) {
				gainOnUse = maxCapacity - capacity;
			}
			
			if( Config.get( ).ABSORPTION_UPGRADES.SHOW_INFO_IN_TOOLTIP ) {
				tooltip.add( translate( "current" ) );
				tooltip.add( TextFormatting.YELLOW.toString( ) + " " + prettyToString( capacity ) + " / " + prettyToString( maxCapacity ) );
			}
			
			if( gainOnUse > 0.0F ) {
				tooltip.add( "" );
				tooltip.add( translate( "gain" ) );
				tooltip.add( TextFormatting.BLUE.toString( ) + " +" + prettyToString( gainOnUse ) + " " + translate( "max" ) );
				
				if( player != null ) {
					final int levelCost = getLevelCost( capacity );
					boolean   hasLevels = levelCost <= player.experienceLevel;
					tooltip.add( "" );
					if( levelCost > 0 ) {
						tooltip.add( translate( "cost", levelCost ) );
					}
					if( hasLevels || player.capabilities.isCreativeMode ) {
						tooltip.add( translate( "canuse" ) );
					}
					else {
						tooltip.add( translate( "nouse" ) );
					}
				}
			}
			else {
				tooltip.add( "" );
				tooltip.add( translate( "nouse" ) );
			}
		}
	}
	
	@SideOnly( Side.CLIENT )
	private
	String translate( String key, Object... args )
	{
		return new TextComponentTranslation( getUnlocalizedName( ) + ".tooltip." + key, args ).getUnformattedText( );
	}
	
	private
	String prettyToString( float value )
	{
		return Math.round( value ) == value ? Integer.toString( Math.round( value ) ) : Float.toString( value );
	}
	
	@Override
	public
	EnumRarity getRarity( ItemStack stack ) { return EnumRarity.UNCOMMON; }
}
