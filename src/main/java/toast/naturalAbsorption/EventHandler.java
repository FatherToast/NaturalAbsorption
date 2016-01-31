package toast.naturalAbsorption;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
    // Useful properties for this class.
    public static final float DEATH_PENALTY = (float) Properties.getDouble(Properties.GENERAL, "death_penalty");
    public static final float MIN_SHIELD = (float) Properties.getDouble(Properties.GENERAL, "min_absorption");
    public static final boolean RECOVER_ON_SPAWN = Properties.getBoolean(Properties.GENERAL, "recover_on_spawn");
    public static final float SHIELD_INCREASE = (float) Properties.getDouble(Properties.UPGRADES, "absorption_gain");
    public static final int LEVEL_COST = Properties.getInt(Properties.UPGRADES, "level_cost");
    public static final boolean HIDE_ARMOR_BAR = Properties.getBoolean(Properties.ARMOR, "hide_armor_bar");

    // The NBT tags used to store all of this mod's NBT info.
    public static final String ITEM_TAG = "NAS|ItemTag";

    // Sets the item as this mod's shield upgrade item.
    public static void setShieldItem(ItemStack itemStack) {
        itemStack.setStackDisplayName("\u00a7eBook of Absorption");
        if (itemStack.stackTagCompound == null) {
            itemStack.stackTagCompound = new NBTTagCompound();
        }
        itemStack.stackTagCompound.setByte(EventHandler.ITEM_TAG, (byte) 1);
    }

    // Returns true if the item is this mod's shield upgrade item.
    public static boolean isShieldItem(ItemStack itemStack) {
        return itemStack.stackTagCompound != null && itemStack.stackTagCompound.hasKey(EventHandler.ITEM_TAG);
    }

    public EventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Called by ItemStack.getTooltip().
     * EntityPlayer entityPlayer = the player looking at the tooltip.
     * boolean showAdvancedItemTooltips = true if advanced tooltips are enabled.
     * ItemStack itemStack = the item stack to display a tooltip for.
     * List<String> toolTip = the tooltip.
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onItemTooltip(ItemTooltipEvent event) {
        if (EventHandler.isShieldItem(event.itemStack)) {
            boolean canUse = event.entityPlayer.experienceLevel >= EventHandler.LEVEL_COST || event.entityPlayer.capabilities.isCreativeMode;
            if (EventHandler.LEVEL_COST > 0 && !event.entityPlayer.capabilities.isCreativeMode) {
                event.toolTip.add("\u00a7" + (canUse ? "7" : "c") + "Cost: " + Integer.toString(EventHandler.LEVEL_COST) + " levels");
            }
            if (canUse) {
                event.toolTip.add("\u00a77Right click to activate");
            }
        }
    }

    /**
     * Called by GuiInGame.
     * float partialTicks = the partial tick.
     * ScaledResolution resolution = the game's resolution.
     * int mouseX = the cursor's x position.
     * int mouseY = the cursor's y position.
     * ElementType type = the type of render event.
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void beforeRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (ShieldManager.REPLACE_ARMOR && EventHandler.HIDE_ARMOR_BAR && event.type == RenderGameOverlayEvent.ElementType.ARMOR) {
            event.setCanceled(true);
        }
    }

    /**
     * Called by EntityPlayer.
     * EntityPlayer entityPlayer = the player interacting.
     * PlayerInteractEvent.Action action = the action this event represents.
     * int x, y, z = the coords of the clicked-on block (if there is one).
     * int face = the side the block was clicked on (if there is one).
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.entityPlayer.worldObj.isRemote && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
            ItemStack held = event.entityPlayer.getHeldItem();
            if (held != null && EventHandler.isShieldItem(held)) {
                boolean canUse = event.entityPlayer.experienceLevel >= EventHandler.LEVEL_COST || event.entityPlayer.capabilities.isCreativeMode;
                if (canUse) {
                    boolean hasEffect = false;
                    if (EventHandler.SHIELD_INCREASE > 0.0F) {
                        NBTTagCompound shieldData = ShieldManager.getShieldData(event.entityPlayer);
                        float shieldCapacity = ShieldManager.getData(shieldData, ShieldManager.CAPACITY_TAG, ShieldManager.STARTING_SHIELD);
                        if (shieldCapacity < ShieldManager.MAX_SHIELD) {
                            hasEffect = true;
                            shieldCapacity += EventHandler.SHIELD_INCREASE;
                            if (shieldCapacity > ShieldManager.MAX_SHIELD) {
                                shieldCapacity = ShieldManager.MAX_SHIELD;
                            }
                            shieldData.setFloat(ShieldManager.CAPACITY_TAG, shieldCapacity);
                        }
                        shieldCapacity += ShieldManager.getArmorAbsorption(event.entityPlayer);
                        if (shieldCapacity > ShieldManager.GLOBAL_MAX_SHIELD) {
                            shieldCapacity = ShieldManager.GLOBAL_MAX_SHIELD;
                        }
                        shieldCapacity += ShieldManager.getPotionAbsorption(event.entityPlayer);

                        float currentShield = event.entityPlayer.getAbsorptionAmount();
                        if (currentShield < shieldCapacity) {
                            hasEffect = true;
                            currentShield += EventHandler.SHIELD_INCREASE;
                            if (currentShield > shieldCapacity) {
                                event.entityPlayer.setAbsorptionAmount(shieldCapacity);
                            }
                            else {
                                event.entityPlayer.setAbsorptionAmount(currentShield);
                            }
                        }
                    }
                    if (hasEffect && !event.entityPlayer.capabilities.isCreativeMode) {
                        if (EventHandler.LEVEL_COST > 0) {
                            event.entityPlayer.addExperienceLevel(-EventHandler.LEVEL_COST);
                        }
                        held.stackSize--;
                        if (held.stackSize <= 0) {
                            event.entityPlayer.setCurrentItemOrArmor(0, (ItemStack) null);
                        }
                    }
                }
            }
        }
    }

    /**
     * Called by World.spawnEntityInWorld().
     * Entity entity = the entity being spawned.
     * World world = the world being spawned into.
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            NBTTagCompound shieldData = ShieldManager.getShieldData(player);
            if (EventHandler.RECOVER_ON_SPAWN && !shieldData.hasKey(ShieldManager.DELAY_TAG) || shieldData.getInteger(ShieldManager.DELAY_TAG) < 0) {
                float shieldCapacity = ShieldManager.getData(shieldData, ShieldManager.CAPACITY_TAG, ShieldManager.STARTING_SHIELD) + ShieldManager.getArmorAbsorption(player);
                if (shieldCapacity > ShieldManager.GLOBAL_MAX_SHIELD) {
                    shieldCapacity = ShieldManager.GLOBAL_MAX_SHIELD;
                }
                shieldCapacity += ShieldManager.getPotionAbsorption(player);
                player.setAbsorptionAmount(shieldCapacity);
            }
            shieldData.setInteger(ShieldManager.DELAY_TAG, 0);
        }
    }

    /**
     * Called by EntityLivingBase.onDeath().
     * EntityLivingBase entityLiving = the entity dying.
     * DamageSource source = the damage source that killed the entity.
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            NBTTagCompound shieldData = ShieldManager.getShieldData((EntityPlayer) event.entityLiving);
            if (EventHandler.DEATH_PENALTY > 0.0F) {
                float shieldCapacity = ShieldManager.getData(shieldData, ShieldManager.CAPACITY_TAG, ShieldManager.STARTING_SHIELD);
                if (shieldCapacity > EventHandler.MIN_SHIELD) {
                    shieldCapacity -= EventHandler.DEATH_PENALTY;
                    if (shieldCapacity < EventHandler.MIN_SHIELD) {
                        shieldData.setFloat(ShieldManager.CAPACITY_TAG, EventHandler.MIN_SHIELD);
                    }
                    else {
                        shieldData.setFloat(ShieldManager.CAPACITY_TAG, shieldCapacity);
                    }
                }
            }
            shieldData.setInteger(ShieldManager.DELAY_TAG, -1);
        }
    }

    /**
     * Called by EntityLiving.damageEntity().
     * EntityLivingBase entityLiving = the entity being damaged.
     * DamageSource source = the source of the damage.
     * float ammount = the amount of damage being dealt. (Setting this <= 0 cancels event.)
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            if (ShieldManager.RECOVER_DELAY >= 0) {
                ShieldManager.getShieldData((EntityPlayer) event.entityLiving).setInteger(ShieldManager.DELAY_TAG, ShieldManager.RECOVER_DELAY);
            }

            if (ShieldManager.REPLACE_ARMOR) {
                if (!event.source.isUnblockable()) {
                    ShieldManager.modifySource(event.source);
                }
                ((EntityPlayer) event.entityLiving).inventory.damageArmor(event.ammount);
            }
        }
        else if (ShieldManager.REPLACE_ARMOR && ShieldManager.isSourceModified(event.source)) {
            ShieldManager.unmodifySource(event.source);
        }
    }
}