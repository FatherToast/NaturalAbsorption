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

import java.util.Random;

/**
 * Essentially a copy-paste of the vanilla player health render code,
 * but tweaked to render empty hearts depending on the player's
 * maximum natural/equipment absorption.
 */
public class AbsorptionBackgroundOverlay implements IGuiOverlay {

    private static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");

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
     * @see ForgeGui#renderHealth(int, int, GuiGraphics)
     */
    @Override
    public void render( ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height ) {
        // Moved below; only should be done once we decide to cancel the vanilla render
        //RenderSystem.enableBlend();

        // We still want to do the below even if we don't commit to rendering anything,
        // to smooth out rendering when we do render

        final Player player = getCameraPlayer();

        if (player == null || !ClientUtil.OVERLAY_ENABLED || player.isCreative()) return;

        final float absorbMax = (float) AbsorptionHelper.getSteadyStateMaxAbsorption(player);

        if (absorbMax <= 0.0F) return;

        int health = Mth.ceil(player.getHealth());
        boolean blink = healthBlinkTime > (long) gui.getGuiTicks() && (healthBlinkTime - (long) gui.getGuiTicks()) / 3L % 2L == 1L;
        long millis = Util.getMillis();

        if (health < lastHealth && player.invulnerableTime > 0) {
            lastHealthTime = millis;
            healthBlinkTime = gui.getGuiTicks() + 20;
        }
        else if (health > lastHealth && player.invulnerableTime > 0) {
            lastHealthTime = millis;
            healthBlinkTime = gui.getGuiTicks() + 10;
        }

        if (millis - lastHealthTime > 1000L) {
            displayHealth = health;
            lastHealthTime = millis;
        }

        lastHealth = health;
        random.setSeed(gui.getGuiTicks() * 312871L);
        int x = width / 2 - 91;
        int y = height - 39;
        float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), (float) Math.max(displayHealth, health));
        int absorption = Mth.ceil(player.getAbsorptionAmount());
        int maxAbsorption = Mth.ceil(absorbMax);
        int healthRows = Mth.ceil((maxHealth + (float) maxAbsorption) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        int shake = -1;

        if (player.hasEffect(MobEffects.REGENERATION)) {
            shake = gui.getGuiTicks() % Mth.ceil(maxHealth + 5.0F);
        }
        // Assume the vanilla health renderer is inactive while we render,
        // so we must add the offset ourselves.
        gui.leftHeight += (healthRows * rowHeight) + 1;

        renderHearts(graphics, player, x, y, rowHeight, shake, maxHealth, health, displayHealth, absorption, maxAbsorption, blink);
    }

    protected void renderHearts(GuiGraphics graphics, Player player, int x, int y, int rowHeight, int shake, float maxHealth, int health, int displayHealth, int absorption, int maxAbsorption, boolean blink) {
        Gui.HeartType heartType = Gui.HeartType.forPlayer(player);
        int vOffset = 9 * (player.level().getLevelData().isHardcore() ? 5 : 0);
        int healthHearts = Mth.ceil((double)maxHealth / 2.0D);
        int absorptionHearts = Mth.ceil((double)maxAbsorption / 2.0D);
        int l = healthHearts * 2;

        for(int hearts = healthHearts + absorptionHearts - 1; hearts >= 0; --hearts) {
            int j1 = hearts / 10;
            int k1 = hearts % 10;
            int xPos = x + k1 * 8;
            int yPos = y - j1 * rowHeight;

            if (health + absorption <= 4) {
                yPos += random.nextInt(2);
            }

            if (hearts < healthHearts && hearts == shake) {
                yPos -= 2;
            }
            renderHeart(graphics, Gui.HeartType.CONTAINER, xPos, yPos, vOffset, blink, false);
            int j2 = hearts * 2;
            boolean flag = hearts >= healthHearts;

            if (flag) {
                int k2 = j2 - l;

                if (k2 < absorption) {
                    boolean flag1 = k2 + 1 == absorption;
                    renderHeart(graphics, heartType == Gui.HeartType.WITHERED ? heartType : Gui.HeartType.ABSORBING, xPos, yPos, vOffset, false, flag1);
                }
            }

            if (blink && j2 < displayHealth) {
                boolean jump = j2 + 1 == displayHealth;
                renderHeart(graphics, heartType, xPos, yPos, vOffset, true, jump);
            }

            if (j2 < health) {
                boolean jump = j2 + 1 == health;
                renderHeart(graphics, heartType, xPos, yPos, vOffset, false, jump);
            }
        }

    }

    private void renderHeart( GuiGraphics graphics, Gui.HeartType heartType, int x, int y, int v, boolean blink, boolean jump ) {
        graphics.blit( GUI_ICONS_LOCATION, x, y, heartType.getX(jump, blink), v, 9, 9 );
    }

    /**
     * @return The camera entity if it is the player.
     */
    private Player getCameraPlayer() {
        return !(Minecraft.getInstance().getCameraEntity() instanceof Player)
                ? null
                : (Player) Minecraft.getInstance().getCameraEntity();
    }
}