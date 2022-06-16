package fathertoast.naturalabsorption.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import fathertoast.naturalabsorption.common.hearts.AbsorptionHelper;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

import java.util.Random;

public class AbsorptionBackgroundOverlay implements IIngameOverlay {
    
    private final Random random = new Random();
    
    /** Used with to make the heart bar flash. */
    private long healthBlinkTime;
    
    private int lastHealth;
    private int displayHealth;
    /** The last recorded system time */
    private long lastHealthTime;
    
    /**
     * Renders empty heart backgrounds to display 'missing' absorption hearts.
     * <p>
     * Based strongly on the vanilla render method, with variables made final where possible.
     *
     * @see ForgeIngameGui#renderHealth(int, int, PoseStack)
     */
    @Override
    public void render( ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height ) {
        // Moved below; only should be done once we decide to cancel the vanilla render
        //RenderSystem.enableBlend();
        
        // We still want to do the below even if we don't commit to rendering anything,
        // to smooth out rendering when we do render
        
        final Player player = Minecraft.getInstance().player;
        
        if( player == null ) return;
        
        final float absorbMax = (float) AbsorptionHelper.getMaxAbsorption( player );
        
        if( absorbMax <= 0.0F ) return;
        
        final int tickCount = Minecraft.getInstance().gui.getGuiTicks();
        
        final int health = Mth.ceil( player.getHealth() );
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
        
        // Don't render hearts for creative mode players
        if( player.isCreative() ) return;
        
        final float healthMax = (float) player.getAttributeValue( Attributes.MAX_HEALTH );
        final float absorb = Mth.ceil( player.getAbsorptionAmount() );
        
        // Calculate number of hearts we want vs. number that vanilla would render
        int extraHearts = Mth.ceil( (healthMax + absorbMax) / 2.0F ) -
                Mth.ceil( (healthMax + absorb) / 2.0F );
        //noinspection StatementWithEmptyBody
        if( player.isCreative() || extraHearts <= 0 ) {
            // All backgrounds will be handled normally, no need for render override
            //return; NOTE: We are currently always overriding
        }
        RenderSystem.enableBlend();
        
        final int healthRows = Mth.ceil( (healthMax + absorbMax) / 2.0F / 10.0F );
        final int rowHeight = Math.max( 10 - (healthRows - 2), 3 );
        
        random.setSeed( tickCount * 312871L );
        
        final int left = width / 2 - 91;
        final int top = height - gui.left_height;
        gui.left_height += (healthRows * rowHeight);
        if( rowHeight != 10 ) gui.left_height += 10 - rowHeight;
        
        final int regen = player.hasEffect( MobEffects.REGENERATION ) ? tickCount % 25 : -1;
        
        final int TOP = 9 * (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getLevelData().isHardcore() ? 5 : 0);
        final int BACKGROUND = (highlight ? 25 : 16);
        
        int MARGIN = 16;
        if( player.hasEffect( MobEffects.POISON ) ) MARGIN += 36;
        else if( player.hasEffect( MobEffects.WITHER ) ) MARGIN += 72;
        
        float absorbRemaining = absorb;
        
        for( int i = Mth.ceil( (healthMax + absorbMax) / 2.0F ) - 1; i >= 0; --i ) {
            
            final int row = Mth.ceil( (float) (i + 1) / 10.0F ) - 1;
            final int x = left + i % 10 * 8;
            int y = top - row * rowHeight;
            
            if( health <= 4 ) y += random.nextInt( 2 );
            if( i == regen ) y -= 2;
            
            // Health for half-heart on current heart
            final int halfHeartHealth = i * 2 + 1;
            
            blit( poseStack, x, y, BACKGROUND, TOP, 9, 9 );
            
            if( highlight ) {
                if( halfHeartHealth < healthLast )
                    blit( poseStack, x, y, MARGIN + 54, TOP, 9, 9 ); //6
                else if( halfHeartHealth == healthLast )
                    blit( poseStack, x, y, MARGIN + 63, TOP, 9, 9 ); //7
            }
            
            // Simply skip the fill logic until we're back to the vanilla heart count
            if( extraHearts > 0 ) {
                extraHearts--;
            }
            else if( absorbRemaining > 0.0F ) {
                if( absorbRemaining == absorb && absorb % 2.0F == 1.0F ) {
                    blit( poseStack, x, y, MARGIN + 153, TOP, 9, 9 ); //17
                    absorbRemaining -= 1.0F;
                }
                else {
                    blit( poseStack, x, y, MARGIN + 144, TOP, 9, 9 ); //16
                    absorbRemaining -= 2.0F;
                }
            }
            else {
                if( halfHeartHealth < health )
                    blit( poseStack, x, y, MARGIN + 36, TOP, 9, 9 ); //4
                else if( halfHeartHealth == health )
                    blit( poseStack, x, y, MARGIN + 45, TOP, 9, 9 ); //5
            }
        }
        RenderSystem.disableBlend();
    }
    
    /**
     * Renders a 2D texture in the GUI using the default depth (aka blitOffset) and texture resolution.
     *
     * @see net.minecraft.client.gui.GuiComponent#blit(PoseStack, int, int, int, int, int, int)
     */
    @SuppressWarnings( "SameParameterValue" )
    private static void blit( PoseStack poseStack, int x, int y, int u, int v, int width, int height ) {
        final float resolution = 256.0F;
        
        innerBlit( poseStack.last().pose(), x, x + width, y, y + height,
                (float) u / resolution, (float) (u + width) / resolution,
                (float) v / resolution, (float) (v + height) / resolution );
    }
    
    /**
     * Renders a 2D texture in the GUI using the default depth (aka blitOffset) and texture resolution.
     */
    private static void innerBlit( Matrix4f matrix4f, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1 ) {
        final float z = -90;
        
        RenderSystem.setShader( GameRenderer::getPositionTexShader );
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin( VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX );
        bufferbuilder.vertex( matrix4f, (float) x0, (float) y1, z ).uv( u0, v1 ).endVertex();
        bufferbuilder.vertex( matrix4f, (float) x1, (float) y1, z ).uv( u1, v1 ).endVertex();
        bufferbuilder.vertex( matrix4f, (float) x1, (float) y0, z ).uv( u1, v0 ).endVertex();
        bufferbuilder.vertex( matrix4f, (float) x0, (float) y0, z ).uv( u0, v0 ).endVertex();
        bufferbuilder.end();
        BufferUploader.end( bufferbuilder );
    }
}