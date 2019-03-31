package fathertoast.naturalabsorption.client;

import fathertoast.naturalabsorption.*;
import fathertoast.naturalabsorption.config.*;
import fathertoast.naturalabsorption.health.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public
class RenderEventHandler
{
	private Random rand = new Random( );
	
	/** Used with updateCounter to make the heart bar flash */
	private long healthUpdateCounter;
	
	private int  playerHealth;
	private int  lastPlayerHealth;
	/** The last recorded system time */
	private long lastSystemTime;
	
	/**
	 * Called before rendering each game overlay element.
	 * <p>
	 * Cancels armor bar rendering when appropriate.
	 *
	 * @param event The event data.
	 */
	@SubscribeEvent( priority = EventPriority.NORMAL )
	public
	void beforeRenderGameOverlay( RenderGameOverlayEvent.Pre event )
	{
		if( Config.get( ).ABSORPTION_HEALTH.ENABLED ) {
			if( Config.get( ).ARMOR.REPLACE_ARMOR && Config.get( ).ARMOR.HIDE_ARMOR_BAR &&
			    event.getType( ) == RenderGameOverlayEvent.ElementType.ARMOR ) {
				event.setCanceled( true );
			}
			else if( Config.get( ).ABSORPTION_HEALTH.RENDER_CAPACITY_BACKGROUND &&
			         event.getType( ) == RenderGameOverlayEvent.ElementType.HEALTH ) {
				try {
					renderAbsorptionCapacity( event );
				}
				catch( Exception ex ) {
					NaturalAbsorptionMod.log( ).error( "Encountered exception during render tick", ex );
					event.setCanceled( false ); // In case vanilla event was already canceled
				}
			}
		}
	}
	
	private
	void renderAbsorptionCapacity( RenderGameOverlayEvent.Pre event )
	{
		ScaledResolution resolution = event.getResolution( );
		
		int width  = resolution.getScaledWidth( );
		int height = resolution.getScaledHeight( );
		
		GlStateManager.enableBlend( );
		
		EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft( ).getRenderViewEntity( );
		if( player == null || ClientProxy.clientAbsorptionCapacity <= 0.0F ) {
			return;
		}
		int updateCounter = Minecraft.getMinecraft( ).ingameGUI.getUpdateCounter( );
		
		int     health    = MathHelper.ceil( player.getHealth( ) );
		boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;
		
		if( health < playerHealth && player.hurtResistantTime > 0 ) {
			lastSystemTime = Minecraft.getSystemTime( );
			healthUpdateCounter = (long) (updateCounter + 20);
		}
		else if( health > playerHealth && player.hurtResistantTime > 0 ) {
			lastSystemTime = Minecraft.getSystemTime( );
			healthUpdateCounter = (long) (updateCounter + 10);
		}
		
		if( Minecraft.getSystemTime( ) - lastSystemTime > 1000L ) {
			playerHealth = health;
			lastPlayerHealth = health;
			lastSystemTime = Minecraft.getSystemTime( );
		}
		
		playerHealth = health;
		int healthLast = lastPlayerHealth;
		
		IAttributeInstance attrMaxHealth = player.getEntityAttribute( SharedMonsterAttributes.MAX_HEALTH );
		float              healthMax     = (float) attrMaxHealth.getAttributeValue( );
		float              absorb        = MathHelper.ceil( player.getAbsorptionAmount( ) );
		
		// Calculate the effective absorption capacity
		float absorbMax = Math.min(
			ClientProxy.clientAbsorptionCapacity + HealthManager.getArmorAbsorption( player ),
			Config.get( ).ABSORPTION_HEALTH.GLOBAL_MAXIMUM
		) + HealthManager.getPotionAbsorption( player );
		
		// Calculate number of hearts we want vs. number that vanilla would render
		int extraHearts = MathHelper.ceil( (healthMax + absorbMax) / 2.0F ) - MathHelper.ceil( (healthMax + absorb) / 2.0F );
		if( extraHearts <= 0 ) {
			// All backgrounds will be handled normally, no need for render override
			return;
		}
		
		// At this point, fully commit to overriding vanilla health bar render
		// This allows us to not have to worry about adding more rows and such
		event.setCanceled( true );
		
		int healthRows = MathHelper.ceil( (healthMax + absorbMax) / 2.0F / 10.0F );
		int rowHeight  = Math.max( 10 - (healthRows - 2), 3 );
		
		rand.setSeed( (long) (updateCounter * 312871) );
		
		int left = width / 2 - 91;
		int top  = height - GuiIngameForge.left_height;
		GuiIngameForge.left_height += (healthRows * rowHeight);
		if( rowHeight != 10 ) {
			GuiIngameForge.left_height += 10 - rowHeight;
		}
		
		int regen = -1;
		if( player.isPotionActive( MobEffects.REGENERATION ) ) {
			regen = updateCounter % 25;
		}
		
		final int TOP        = 9 * (Minecraft.getMinecraft( ).world.getWorldInfo( ).isHardcoreModeEnabled( ) ? 5 : 0);
		final int BACKGROUND = (highlight ? 25 : 16);
		int       MARGIN     = 16;
		if( player.isPotionActive( MobEffects.POISON ) )
			MARGIN += 36;
		else if( player.isPotionActive( MobEffects.WITHER ) )
			MARGIN += 72;
		float absorbRemaining = absorb;
		
		for( int i = MathHelper.ceil( (healthMax + absorbMax) / 2.0F ) - 1; i >= 0; --i ) {
			int row = MathHelper.ceil( (float) (i + 1) / 10.0F ) - 1;
			int x   = left + i % 10 * 8;
			int y   = top - row * rowHeight;
			
			// Health for half-heart on current heart
			int halfHeartHealth = i * 2 + 1;
			
			if( health <= 4 )
				y += rand.nextInt( 2 );
			if( i == regen )
				y -= 2;
			
			drawTexturedModalRect( x, y, BACKGROUND, TOP, 9, 9 );
			
			if( highlight ) {
				if( halfHeartHealth < healthLast )
					drawTexturedModalRect( x, y, MARGIN + 54, TOP, 9, 9 ); //6
				else if( halfHeartHealth == healthLast )
					drawTexturedModalRect( x, y, MARGIN + 63, TOP, 9, 9 ); //7
			}
			
			// Simply skip the fill logic until we're back to the vanilla heart count
			if( extraHearts > 0 ) {
				extraHearts--;
			}
			else if( absorbRemaining > 0.0F ) {
				if( absorbRemaining == absorb && absorb % 2.0F == 1.0F ) {
					drawTexturedModalRect( x, y, MARGIN + 153, TOP, 9, 9 ); //17
					absorbRemaining -= 1.0F;
				}
				else {
					drawTexturedModalRect( x, y, MARGIN + 144, TOP, 9, 9 ); //16
					absorbRemaining -= 2.0F;
				}
			}
			else {
				if( halfHeartHealth < health )
					drawTexturedModalRect( x, y, MARGIN + 36, TOP, 9, 9 ); //4
				else if( halfHeartHealth == health )
					drawTexturedModalRect( x, y, MARGIN + 45, TOP, 9, 9 ); //5
			}
		}
		
		GlStateManager.disableBlend( );
	}
	
	@SuppressWarnings( "SameParameterValue" )
	private
	void drawTexturedModalRect( int x, int y, int textureX, int textureY, int width, int height )
	{
		// Standard gui z-level
		final float zLevel = -90.0F;
		
		// We assume a square texture, so don't need separate u and v
		final float res = 1.0F / 256.0F;
		
		// Essentially copy/pasted from Gui.class
		Tessellator   tessellator   = Tessellator.getInstance( );
		BufferBuilder bufferbuilder = tessellator.getBuffer( );
		bufferbuilder.begin( 7, DefaultVertexFormats.POSITION_TEX );
		bufferbuilder.pos( (double) (x), (double) (y + height), (double) zLevel ).tex( (double) ((float) (textureX) * res), (double) ((float) (textureY + height) * res) ).endVertex( );
		bufferbuilder.pos( (double) (x + width), (double) (y + height), (double) zLevel ).tex( (double) ((float) (textureX + width) * res), (double) ((float) (textureY + height) * res) ).endVertex( );
		bufferbuilder.pos( (double) (x + width), (double) (y), (double) zLevel ).tex( (double) ((float) (textureX + width) * res), (double) ((float) (textureY) * res) ).endVertex( );
		bufferbuilder.pos( (double) (x), (double) (y), (double) zLevel ).tex( (double) ((float) (textureX) * res), (double) ((float) (textureY) * res) ).endVertex( );
		tessellator.draw( );
	}
}
