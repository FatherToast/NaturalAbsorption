package fathertoast.naturalabsorption.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.health.HeartManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class RenderEventHandler {
    
    public static float PLAYER_NATURAL_ABSORPTION = -1.0F;
    
    private final Random random = new Random();
    
    /** Used with to make the heart bar flash. */
    private long healthBlinkTime;
    
    private int lastHealth;
    private int displayHealth;
    /** The last recorded system time */
    private long lastHealthTime;
    
    /**
     * Called before rendering each game overlay element.
     * <p>
     * Cancels armor bar rendering when appropriate.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void beforeRenderGameOverlay( RenderGameOverlayEvent.Pre event ) {
        if( HeartManager.isArmorReplacementEnabled() && Config.EQUIPMENT.ARMOR.hideArmorBar.get() &&
                RenderGameOverlayEvent.ElementType.ARMOR.equals( event.getType() ) ) {
            event.setCanceled( true );
        }
        else if( Config.ABSORPTION.GENERAL.renderCapacityBackground.get() &&
                RenderGameOverlayEvent.ElementType.HEALTH.equals( event.getType() ) ) {
            try {
                renderAbsorptionCapacity( event );
            }
            catch( Exception ex ) {
                NaturalAbsorption.LOG.error( "Encountered exception during heart render tick", ex );
                event.setCanceled( false ); // In case we already canceled the vanilla event
            }
        }
    }
    
    /**
     * Renders empty heart backgrounds to display 'missing' absorption hearts.
     * <p>
     * Based strongly on the vanilla render method, with variables made final where possible.
     *
     * @see ForgeIngameGui#renderHealth(int, int, MatrixStack)
     */
    private void renderAbsorptionCapacity( RenderGameOverlayEvent.Pre event ) {
        final int width = event.getWindow().getGuiScaledWidth();
        final int height = event.getWindow().getGuiScaledHeight();
        final MatrixStack mStack = event.getMatrixStack();
        
        // Moved below; only should be done once we decide to cancel the vanilla render
        //RenderSystem.enableBlend();
        
        // We still want to do the below even if we don't commit to rendering anything,
        // to smooth out rendering when we do render
        
        final PlayerEntity player = Minecraft.getInstance().player;
        
        if( player == null || PLAYER_NATURAL_ABSORPTION <= 0.0F ) return;
        final int tickCount = Minecraft.getInstance().gui.getGuiTicks();
        
        final int health = MathHelper.ceil( player.getHealth() );
        final boolean highlight = healthBlinkTime > (long) tickCount && (healthBlinkTime - (long) tickCount) / 3L % 2L == 1L;
        
        if( health < lastHealth && player.invulnerableTime > 0 ) {
            lastHealthTime = Util.getMillis();
            healthBlinkTime = tickCount + 20;
        }
        else if( health > lastHealth && player.invulnerableTime > 0 ) {
            lastHealthTime = Util.getMillis();
            healthBlinkTime = tickCount + 10;
        }
        
        if( Util.getMillis() - lastHealthTime > 1000L ) {
            lastHealth = health;
            displayHealth = health;
            lastHealthTime = Util.getMillis();
        }
        
        lastHealth = health;
        final int healthLast = displayHealth;
        
        final float healthMax = (float) player.getAttributeValue( Attributes.MAX_HEALTH );
        final float absorb = MathHelper.ceil( player.getAbsorptionAmount() );
        
        // Calculate the effective absorption capacity
        final float absorbMax = Math.min(
                PLAYER_NATURAL_ABSORPTION + HeartManager.getEquipmentAbsorption( player ),
                (float) Config.ABSORPTION.GENERAL.globalMax.get()
        ) + HeartManager.getPotionAbsorption( player );
        
        // Calculate number of hearts we want vs. number that vanilla would render
        int extraHearts = MathHelper.ceil( (healthMax + absorbMax) / 2.0F ) -
                MathHelper.ceil( (healthMax + absorb) / 2.0F );
        if( extraHearts <= 0 ) {
            // All backgrounds will be handled normally, no need for render override
            return;
        }
        
        // At this point, fully commit to overriding vanilla health bar render
        // This allows us to not have to worry about adding more rows and such
        event.setCanceled( true );
        RenderSystem.enableBlend();
        
        final int healthRows = MathHelper.ceil( (healthMax + absorbMax) / 2.0F / 10.0F );
        final int rowHeight = Math.max( 10 - (healthRows - 2), 3 );
        
        random.setSeed( tickCount * 312871L );
        
        final int left = width / 2 - 91;
        final int top = height - ForgeIngameGui.left_height;
        ForgeIngameGui.left_height += (healthRows * rowHeight);
        if( rowHeight != 10 ) ForgeIngameGui.left_height += 10 - rowHeight;
        
        final int regen = player.hasEffect( Effects.REGENERATION ) ? tickCount % 25 : -1;
        
        final int TOP = 9 * (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getLevelData().isHardcore() ? 5 : 0);
        final int BACKGROUND = (highlight ? 25 : 16);
        
        int MARGIN = 16;
        if( player.hasEffect( Effects.POISON ) ) MARGIN += 36;
        else if( player.hasEffect( Effects.WITHER ) ) MARGIN += 72;
        
        float absorbRemaining = absorb;
        
        for( int i = MathHelper.ceil( (healthMax + absorbMax) / 2.0F ) - 1; i >= 0; --i ) {
            
            final int row = MathHelper.ceil( (float) (i + 1) / 10.0F ) - 1;
            final int x = left + i % 10 * 8;
            int y = top - row * rowHeight;
            
            if( health <= 4 ) y += random.nextInt( 2 );
            if( i == regen ) y -= 2;
            
            // Health for half-heart on current heart
            final int halfHeartHealth = i * 2 + 1;
            
            blit( mStack, x, y, BACKGROUND, TOP, 9, 9 );
            
            if( highlight ) {
                if( halfHeartHealth < healthLast )
                    blit( mStack, x, y, MARGIN + 54, TOP, 9, 9 ); //6
                else if( halfHeartHealth == healthLast )
                    blit( mStack, x, y, MARGIN + 63, TOP, 9, 9 ); //7
            }
            
            // Simply skip the fill logic until we're back to the vanilla heart count
            if( extraHearts > 0 ) {
                extraHearts--;
            }
            else if( absorbRemaining > 0.0F ) {
                if( absorbRemaining == absorb && absorb % 2.0F == 1.0F ) {
                    blit( mStack, x, y, MARGIN + 153, TOP, 9, 9 ); //17
                    absorbRemaining -= 1.0F;
                }
                else {
                    blit( mStack, x, y, MARGIN + 144, TOP, 9, 9 ); //16
                    absorbRemaining -= 2.0F;
                }
            }
            else {
                if( halfHeartHealth < health )
                    blit( mStack, x, y, MARGIN + 36, TOP, 9, 9 ); //4
                else if( halfHeartHealth == health )
                    blit( mStack, x, y, MARGIN + 45, TOP, 9, 9 ); //5
            }
        }
        
        RenderSystem.disableBlend();
    }
    
    @SuppressWarnings( "SameParameterValue" )
    private void blit( MatrixStack mStack, int x, int y, int textureX, int textureY, int width, int height ) {
        final Matrix4f matrix = mStack.last().pose();
        
        // Standard gui z-level
        final float zLevel = -90.0F;
        // We assume a square texture, so don't need separate u and v
        final float res = 1.0F / 256.0F;
        
        // Essentially copy/pasted from Gui.class
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin( 7, DefaultVertexFormats.POSITION_TEX );
        bufferBuilder.vertex( x, y + height, zLevel ).uv( ((float) (textureX) * res), ((float) (textureY + height) * res) ).endVertex();
        bufferBuilder.vertex( x + width, y + height, zLevel ).uv( ((float) (textureX + width) * res), ((float) (textureY + height) * res) ).endVertex();
        bufferBuilder.vertex( x + width, y, zLevel ).uv( ((float) (textureX + width) * res), ((float) (textureY) * res) ).endVertex();
        bufferBuilder.vertex( x, y, zLevel ).uv( ((float) (textureX) * res), ((float) (textureY) * res) ).endVertex();
        tessellator.end();
    }
}