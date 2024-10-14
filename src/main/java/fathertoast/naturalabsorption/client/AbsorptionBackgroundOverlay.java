package fathertoast.naturalabsorption.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import fathertoast.naturalabsorption.common.hearts.AbsorptionHelper;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.Random;

/**
 * Essentially a copy-paste of the vanilla player health render code,
 * but tweaked to render empty hearts depending on the player's
 * maximum natural/equipment absorption.
 */
public class AbsorptionBackgroundOverlay implements IGuiOverlay {
    
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
     * @see ForgeGui#renderHealth(int, int, PoseStack)
     */
    @Override
    public void render( ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height ) {
        // Moved below; only should be done once we decide to cancel the vanilla render
        //RenderSystem.enableBlend();

        // We still want to do the below even if we don't commit to rendering anything,
        // to smooth out rendering when we do render

        final Player player = getCameraPlayer();

        if ( player == null || !ClientUtil.OVERLAY_ENABLED || player.isCreative() || player.isSpectator() ) return;

        final float absorbMax = (float) AbsorptionHelper.getMaxAbsorption( player );

        if ( absorbMax <= 0.0F ) return;

        int health = Mth.ceil( player.getHealth() );
        boolean blink = healthBlinkTime > (long) gui.getGuiTicks() && (healthBlinkTime - (long) gui.getGuiTicks()) / 3L % 2L == 1L;
        long millis = Util.getMillis();

        if ( health < lastHealth && player.invulnerableTime > 0 ) {
            lastHealthTime = millis;
            healthBlinkTime = gui.getGuiTicks() + 20;
        }
        else if ( health > lastHealth && player.invulnerableTime > 0 ) {
            lastHealthTime = millis;
            healthBlinkTime = gui.getGuiTicks() + 10;
        }

        if ( millis - lastHealthTime > 1000L ) {
            displayHealth = health;
            lastHealthTime = millis;
        }

        lastHealth = health;
        random.setSeed( gui.getGuiTicks() * 312871L );
        int x = width / 2 - 91;
        int y = height - 39;
        float maxHealth = Math.max( (float) player.getAttributeValue( Attributes.MAX_HEALTH), (float) Math.max( displayHealth, health ) );
        int absorption = Mth.ceil( player.getAbsorptionAmount() );
        int maxAbsorption = Mth.ceil( absorbMax );
        int healthRows = Mth.ceil(( maxHealth + (float) maxAbsorption) / 2.0F / 10.0F );
        int rowHeight = Math.max( 10 - ( healthRows - 2 ), 3 );
        int shake = -1;

        if ( player.hasEffect( MobEffects.REGENERATION ) ) {
            shake = gui.getGuiTicks() % Mth.ceil( maxHealth + 5.0F );
        }
        // Assume the vanilla health renderer is inactive while we render,
        // so we must add the offset ourselves.
        gui.leftHeight += ( healthRows * rowHeight ) + 1;

        renderHearts( poseStack, player, x, y, rowHeight, shake, maxHealth, health, displayHealth, absorption, maxAbsorption, blink );
    }

    protected void renderHearts( PoseStack poseStack, Player player, int x, int y, int rowHeight, int shake, float maxHealth, int health, int displayHealth, int absorption, int maxAbsorption, boolean blink ) {
        Gui.HeartType heartType = Gui.HeartType.forPlayer( player );
        int vOffset = 9 * ( player.level.getLevelData().isHardcore() ? 5 : 0 );
        int healthHearts = Mth.ceil( (double) maxHealth / 2.0D );
        int absorptionHearts = Mth.ceil( (double) maxAbsorption / 2.0D );
        int l = healthHearts * 2;

        for( int hearts = healthHearts + absorptionHearts - 1; hearts >= 0; --hearts ) {
            int j1 = hearts / 10;
            int k1 = hearts % 10;
            int xPos = x + k1 * 8;
            int yPos = y - j1 * rowHeight;

            if ( health + absorption <= 4 ) {
                yPos += random.nextInt( 2 );
            }

            if ( hearts < healthHearts && hearts == shake ) {
                yPos -= 2;
            }
            renderHeart( poseStack, Gui.HeartType.CONTAINER, xPos, yPos, vOffset, blink, false );
            int heartHalves = hearts * 2;
            boolean drawAbsorption = hearts >= healthHearts;

            if ( drawAbsorption ) {
                int k2 = heartHalves - l;

                if ( k2 < absorption ) {
                    boolean jump = k2 + 1 == absorption;
                    renderHeart( poseStack, heartType == Gui.HeartType.WITHERED ? heartType : Gui.HeartType.ABSORBING, xPos, yPos, vOffset, false, jump );
                }
            }

            if ( blink && heartHalves < displayHealth ) {
                boolean jump = heartHalves + 1 == displayHealth;
                renderHeart( poseStack, heartType, xPos, yPos, vOffset, true, jump );
            }

            if ( heartHalves < health ) {
                boolean jump = heartHalves + 1 == health;
                renderHeart( poseStack, heartType, xPos, yPos, vOffset, false, jump );
            }
        }

    }

    private void renderHeart( PoseStack poseStack, Gui.HeartType heartType, int x, int y, int v, boolean blink, boolean jump ) {
        blit( poseStack, x, y, heartType.getX( jump, blink ), v, 9, 9 );
    }

    /**
     * @return The camera entity if it is the player.
     */
    private Player getCameraPlayer() {
        return !(Minecraft.getInstance().getCameraEntity() instanceof Player)
                ? null
                : (Player) Minecraft.getInstance().getCameraEntity();
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
        BufferUploader.drawWithShader( bufferbuilder.end() );
    }
}