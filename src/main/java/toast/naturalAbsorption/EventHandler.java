package toast.naturalAbsorption;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import toast.naturalAbsorption.network.MessageSyncShield;

public class EventHandler {

    // Returns true if the item is this mod's shield upgrade item.
    public static boolean isShieldItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getItem() == ModNaturalAbsorption.ABSORB_BOOK;
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
        if (EventHandler.isShieldItem(event.getItemStack())) {
        	String loc = ModNaturalAbsorption.ABSORB_BOOK.getUnlocalizedName() + ".tooltip.";
        	String defaultColor = TextFormatting.GRAY.toString();
        	String gold = TextFormatting.YELLOW.toString();

            boolean canUse = event.getEntityPlayer().experienceLevel >= Properties.get().UPGRADES.LEVEL_COST || event.getEntityPlayer().capabilities.isCreativeMode;
            boolean tooExpensive = !canUse;

            ITextComponent text;
            event.getToolTip().set(0, gold + event.getToolTip().get(0)); // Colors the name

            NBTTagCompound shieldData = ShieldManager.getShieldData(event.getEntityPlayer());
            float shieldCapacity = ShieldManager.getData(shieldData, ShieldManager.CAPACITY_TAG, Properties.get().GENERAL.STARTING_SHIELD);

            String[] shieldCap = {
            	Math.round(shieldCapacity) == shieldCapacity ? Integer.toString(Math.round(shieldCapacity)) : Float.toString(shieldCapacity),
            	Math.round(shieldCapacity) == shieldCapacity ? Integer.toString(Math.round(Properties.get().GENERAL.MAX_SHIELD)) : Float.toString(Properties.get().GENERAL.MAX_SHIELD)
            };
        	text = new TextComponentTranslation(loc + "info", new Object[] {
    			new StringBuilder(gold).append(shieldCap[0]).append(defaultColor)
    			.append(" / ").append(gold).append(shieldCap[1]).toString()
    		});
        	event.getToolTip().add("");
        	event.getToolTip().add(defaultColor + text.getUnformattedText());

        	float gain = Properties.get().UPGRADES.ABSORPTION_GAIN;
        	if (gain > Properties.get().GENERAL.MAX_SHIELD - shieldCapacity) {
        		gain = Properties.get().GENERAL.MAX_SHIELD - shieldCapacity;
        	}
        	if (gain > 0.0F) {
            	event.getToolTip().add("");
            	text = new TextComponentTranslation(loc + "gain", new Object[] { });
            	event.getToolTip().add(defaultColor + text.getUnformattedText());
            	text = new TextComponentTranslation(MobEffects.ABSORPTION.getName(), new Object[] { });
            	event.getToolTip().add(new StringBuilder(TextFormatting.BLUE.toString())
            		.append(" +").append(Float.toString(gain)).append(" ").append(text.getUnformattedText()).toString());
        	}
        	else {
        		canUse = false;
        	}

            if (Properties.get().UPGRADES.LEVEL_COST > 0 && !event.getEntityPlayer().capabilities.isCreativeMode) {
            	if (gain <= 0.0F)
                	event.getToolTip().add("");
            	text = new TextComponentTranslation(loc + "cost", new Object[] { Properties.get().UPGRADES.LEVEL_COST });
            	event.getToolTip().add((tooExpensive ? TextFormatting.RED.toString() : defaultColor) + text.getUnformattedText());
            }

            if (canUse) {
                text = new TextComponentTranslation(loc + "canuse", new Object[] { });
                event.getToolTip().add("");
            	event.getToolTip().add(defaultColor + text.getUnformattedText());
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
        if (Properties.get().ARMOR.REPLACE_ARMOR && Properties.get().ARMOR.HIDE_ARMOR_BAR && event.getType() == RenderGameOverlayEvent.ElementType.ARMOR) {
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
    public void onPlayerInteract(PlayerInteractEvent.RightClickItem event) {
        ItemStack held = event.getEntityPlayer().getHeldItemMainhand();
        if (held != null && EventHandler.isShieldItem(held)) {
            boolean canUse = event.getEntityPlayer().experienceLevel >= Properties.get().UPGRADES.LEVEL_COST || event.getEntityPlayer().capabilities.isCreativeMode;
            if (canUse) {
                boolean hasEffect = false;
                if (Properties.get().UPGRADES.ABSORPTION_GAIN > 0.0F) {
                    NBTTagCompound shieldData = ShieldManager.getShieldData(event.getEntityPlayer());
                    float shieldCapacity = ShieldManager.getData(shieldData, ShieldManager.CAPACITY_TAG, Properties.get().GENERAL.STARTING_SHIELD);
                    if (shieldCapacity < Properties.get().GENERAL.MAX_SHIELD) {
                        hasEffect = true;
                        shieldCapacity += Properties.get().UPGRADES.ABSORPTION_GAIN;
                        if (shieldCapacity > Properties.get().GENERAL.MAX_SHIELD) {
                            shieldCapacity = Properties.get().GENERAL.MAX_SHIELD;
                        }
                        shieldData.setFloat(ShieldManager.CAPACITY_TAG, shieldCapacity);
                    }
                    shieldCapacity += ShieldManager.getArmorAbsorption(event.getEntityPlayer());
                    if (shieldCapacity > Properties.get().GENERAL.GLOBAL_MAX_SHIELD) {
                        shieldCapacity = Properties.get().GENERAL.GLOBAL_MAX_SHIELD;
                    }
                    shieldCapacity += ShieldManager.getPotionAbsorption(event.getEntityPlayer());

                    float currentShield = event.getEntityPlayer().getAbsorptionAmount();
                    if (currentShield < shieldCapacity) {
                        hasEffect = true;
                        currentShield += Properties.get().UPGRADES.ABSORPTION_GAIN;
                        if (currentShield > shieldCapacity) {
                            event.getEntityPlayer().setAbsorptionAmount(shieldCapacity);
                        }
                        else {
                            event.getEntityPlayer().setAbsorptionAmount(currentShield);
                        }
                    }
                }
                if (hasEffect && !event.getEntityPlayer().capabilities.isCreativeMode) {
                    if (Properties.get().UPGRADES.LEVEL_COST > 0) {
                        event.getEntityPlayer().addExperienceLevel(-Properties.get().UPGRADES.LEVEL_COST);
                    }
                    held.stackSize--;
                    if (held.stackSize <= 0) {
                        event.getEntityPlayer().setHeldItem(EnumHand.MAIN_HAND, (ItemStack) null);
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
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            NBTTagCompound shieldData = ShieldManager.getShieldData(player);
            if (Properties.get().GENERAL.RECOVER_ON_SPAWN && !shieldData.hasKey(ShieldManager.DELAY_TAG) || shieldData.getInteger(ShieldManager.DELAY_TAG) < 0) {
                float shieldCapacity = ShieldManager.getData(shieldData, ShieldManager.CAPACITY_TAG, Properties.get().GENERAL.STARTING_SHIELD) + ShieldManager.getArmorAbsorption(player);
                if (shieldCapacity > Properties.get().GENERAL.GLOBAL_MAX_SHIELD) {
                    shieldCapacity = Properties.get().GENERAL.GLOBAL_MAX_SHIELD;
                }
                shieldCapacity += ShieldManager.getPotionAbsorption(player);
                player.setAbsorptionAmount(shieldCapacity);
            }
            shieldData.setInteger(ShieldManager.DELAY_TAG, 0);

            if (!event.getWorld().isRemote && player instanceof EntityPlayerMP) {
            	ModNaturalAbsorption.CHANNEL.sendTo(new MessageSyncShield(player), (EntityPlayerMP) player);
        	}
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
        if (event.getEntityLiving() instanceof EntityPlayer) {
            NBTTagCompound shieldData = ShieldManager.getShieldData((EntityPlayer) event.getEntityLiving());
            if (Properties.get().GENERAL.DEATH_PENALTY > 0.0F) {
                float shieldCapacity = ShieldManager.getData(shieldData, ShieldManager.CAPACITY_TAG, Properties.get().GENERAL.STARTING_SHIELD);
                if (shieldCapacity > Properties.get().GENERAL.MIN_SHIELD) {
                    shieldCapacity -= Properties.get().GENERAL.DEATH_PENALTY;
                    if (shieldCapacity < Properties.get().GENERAL.MIN_SHIELD) {
                        shieldData.setFloat(ShieldManager.CAPACITY_TAG, Properties.get().GENERAL.MIN_SHIELD);
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
     * float amount = the amount of damage being dealt. (Setting this <= 0 cancels event.)
     *
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
        	EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (Properties.get().RECOVERY.DELAY >= 0) {
                ShieldManager.getShieldData(player).setInteger(ShieldManager.DELAY_TAG, Properties.get().RECOVERY.DELAY);
            }

            if (Properties.get().ARMOR.REPLACE_ARMOR) {
                if (!event.getSource().isUnblockable()) {
                    ShieldManager.modifySource(event.getSource());
                    if ("VANILLA".equalsIgnoreCase(Properties.get().ARMOR.DURABILITY_TRIGGER))
                    	this.damageArmor(event);
                }

                if ("HITS".equalsIgnoreCase(Properties.get().ARMOR.DURABILITY_TRIGGER)) {
                	if (event.getSource() == DamageSource.inWall || event.getSource() == DamageSource.starve || event.getSource() == DamageSource.drown
                		|| event.getSource() == DamageSource.magic && event.getAmount() <= 1.0F || event.getSource() == DamageSource.wither
                		|| event.getSource() == DamageSource.onFire || event.getSource() == DamageSource.lava)
                    	this.damageArmor(event);
                }
                else if ("ALL".equalsIgnoreCase(Properties.get().ARMOR.DURABILITY_TRIGGER))
                	this.damageArmor(event);
            }
        }
        else if (Properties.get().ARMOR.REPLACE_ARMOR && ShieldManager.isSourceModified(event.getSource())) {
            ShieldManager.unmodifySource(event.getSource());
        }
    }

    private void damageArmor(LivingHurtEvent event) {
    	EntityPlayer player = (EntityPlayer) event.getEntityLiving();
    	float durabilityDamage = event.getAmount();
    	if (Properties.get().ARMOR.FRIENDLY_DURABILITY && durabilityDamage > player.getAbsorptionAmount()) {
    		durabilityDamage = player.getAbsorptionAmount();
    	}
    	durabilityDamage *= Properties.get().ARMOR.DURABILITY_MULT;

    	if (!event.getSource().canHarmInCreative() && durabilityDamage > 0.0F) {
    		player.inventory.damageArmor(durabilityDamage);
    	}
    }
}