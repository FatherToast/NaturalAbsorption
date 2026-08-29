package fathertoast.naturalabsorption.client;

import fathertoast.naturalabsorption.common.core.hearts.AbsorptionHelper;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * A modified copy-paste of the vanilla player health renderer that in addition
 * to drawing the player's health also draws empty heart containers before absorption
 * to display the player's absorption capacity.
 */
public class NAHealthOverlay implements IGuiOverlay {
    
    /** A resource location pointing to vanilla's GUI icons texture. */
    private static final ResourceLocation GUI_ICONS_LOCATION = ResourceLocation.withDefaultNamespace( "textures/gui/icons.png" );
    
    /** This GUI overlay's RNG. */
    private final Random random = new Random();
    
    /** Used with to make the heart bar flash. */
    private long healthBlinkTime;
    
    /** The last recorded value of the player's health. */
    private int lastHealth;
    /** The last recorded system time. */
    private long lastHealthTime;
    /** The current health value to be used for display. */
    private int displayHealth;
    
    /**
     * Renders this GUI overlay.
     * Renders empty heart container icons to represent absorption capacity and
     * then draws the vanilla health elements on top of it.
     */
    @Override
    public void render( ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height ) {
        final Player player = getCameraPlayer();
        
        if( player == null || !ClientUtil.OVERLAY_ENABLED || player.isCreative() || player.isSpectator() ) return;
        
        final float absorbMax = (float) AbsorptionHelper.getMaxAbsorption( player );
        
        if( absorbMax <= 0.0F ) return;
        
        final int health = Mth.ceil( player.getHealth() );
        final boolean blink = healthBlinkTime > (long) gui.getGuiTicks() && (healthBlinkTime - (long) gui.getGuiTicks()) / 3L % 2L == 1L;
        final long millis = Util.getMillis();
        
        if( health < lastHealth && player.invulnerableTime > 0 ) {
            lastHealthTime = millis;
            healthBlinkTime = gui.getGuiTicks() + 20;
        }
        else if( health > lastHealth && player.invulnerableTime > 0 ) {
            lastHealthTime = millis;
            healthBlinkTime = gui.getGuiTicks() + 10;
        }
        
        if( millis - lastHealthTime > 1000L ) {
            displayHealth = health;
            lastHealthTime = millis;
        }
        lastHealth = health;
        random.setSeed( gui.getGuiTicks() * 312871L );
        
        final int x = width / 2 - 91;
        final int y = height - 39;
        final float maxHealth = Math.max( (float) player.getAttributeValue( Attributes.MAX_HEALTH ), (float) Math.max( displayHealth, health ) );
        final int absorption = Mth.ceil( player.getAbsorptionAmount() );
        final int maxAbsorption = Mth.ceil( absorbMax );
        final int healthRows = Mth.ceil( (maxHealth + (float) maxAbsorption) / 2.0F / 10.0F );
        final int rowHeight = Math.max( 10 - (healthRows - 2), 3 );
        int jump = -1;
        
        if( player.hasEffect( MobEffects.REGENERATION ) ) {
            jump = gui.getGuiTicks() % Mth.ceil( maxHealth + 5.0F );
        }
        // Assume the vanilla health renderer is inactive while we render,
        // so we must add the offset ourselves.
        gui.leftHeight += (healthRows * rowHeight) + 1;
        
        renderHearts( graphics, player, x, y, rowHeight, jump, maxHealth, health, displayHealth, absorption, maxAbsorption, blink );
    }
    
    /**
     * A modified copy-paste of vanilla's heart renderer method.
     *
     * @see ForgeGui#renderHearts(GuiGraphics, Player, int, int, int, int, float, int, int, int, boolean)
     */
    @SuppressWarnings( "JavadocReference" )
    protected void renderHearts( GuiGraphics graphics, Player player, int x, int y, int rowHeight, int jump, float maxHealth, int health, int displayHealth, int absorption, int maxAbsorption, boolean blink ) {
        final Gui.HeartType heartType = Gui.HeartType.forPlayer( player );
        // noinspection resource
        final int vOffset = 9 * (player.level().getLevelData().isHardcore() ? 5 : 0);
        final int healthHearts = Mth.ceil( (double) maxHealth / 2.0D );
        final int totalHearts = healthHearts + Mth.ceil( (Math.max( absorption, maxAbsorption )) / 2.0D );
        
        for( int i = totalHearts - 1; i >= 0; --i ) {
            int xPos = x + (i % 10) * 8;
            int yPos = y - (i / 10) * rowHeight;
            
            // Make hearts "shiver" when health is low
            if( health + absorption <= 4 ) {
                yPos += random.nextInt( 2 );
            }
            // Do a tiny tick-based jump
            // Usually only happens if the player has the regeneration effect
            if( i < healthHearts && i == jump ) {
                yPos -= 2;
            }
            // Draw a heart container before drawing a heart
            renderHeart( graphics, Gui.HeartType.CONTAINER, xPos, yPos, vOffset, blink, false );
            int halves = i * 2;
            
            // Draw an absorption heart, if absorption is not 0
            if( i >= healthHearts ) {
                int absorptionHalves = halves - (healthHearts * 2);
                
                if( absorptionHalves < absorption ) {
                    renderHeart( graphics, heartType == Gui.HeartType.WITHERED ? heartType : Gui.HeartType.ABSORBING, xPos, yPos, vOffset, false, absorptionHalves + 1 == absorption );
                }
            }
            if( blink && halves < displayHealth ) {
                renderHeart( graphics, heartType, xPos, yPos, vOffset, true, halves + 1 == displayHealth );
            }
            if( halves < health ) {
                renderHeart( graphics, heartType, xPos, yPos, vOffset, false, halves + 1 == health );
            }
        }
    }
    
    /**
     * Renders a heart icon based on the specified heart type.
     *
     * @param graphics  The graphics object to draw with.
     * @param heartType The type of heart to draw.
     * @param x         The X-position to draw the heart icon at.
     * @param y         The Y-position to draw the heart icon at.
     * @param v         The texture V offset.
     * @param blink     True if the heart icon should flash.
     * @param half      True if half a heart should be drawn instead of a full heart.
     */
    private void renderHeart( GuiGraphics graphics, Gui.HeartType heartType, int x, int y, int v, boolean blink, boolean half ) {
        graphics.blit( GUI_ICONS_LOCATION, x, y, heartType.getX( half, blink ), v, 9, 9 );
    }
    
    /** @return The camera entity if it is the player. */
    @Nullable
    private Player getCameraPlayer() {
        return Minecraft.getInstance().getCameraEntity() instanceof Player player ? player : null;
    }
}