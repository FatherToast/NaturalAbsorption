package toast.naturalAbsorption;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ShieldManager {
    public static final int UPDATE_TIMEn = Properties.get().RECOVERY.UPDATE_TIME - 1;

    // The NBT tags used to store all of this mod's NBT info.
    public static final String BASE_TAG = "NaturalAbsorption";
    public static final String CAPACITY_TAG = "Capacity";
    public static final String DELAY_TAG = "Delay";

    // Set of all currently altered damage sources.
    private static final Set<DamageSource> MODDED_SOURCES = new HashSet<DamageSource>();

    // Returns true if the given damage source is modified to ignore armor.
    public static boolean isSourceModified(DamageSource source) {
        return ShieldManager.MODDED_SOURCES.contains(source);
    }

    // Modifies or unmodifies a source to ignore armor.
    public static void modifySource(DamageSource source) {
    	if (source.isUnblockable()) return;
        try {
        	ObfuscationReflectionHelper.setPrivateValue(DamageSource.class, source, true, new String[] { "field_76374_o", "isUnblockable" });
            ShieldManager.MODDED_SOURCES.add(source);
        }
        catch (Exception ex) {
            ModNaturalAbsorption.logError("Failed to apply armor ignore to source (" + source.toString() + ")!");
            ex.printStackTrace();
        }
    }
    public static void unmodifySource(DamageSource source) {
        try {
        	ObfuscationReflectionHelper.setPrivateValue(DamageSource.class, source, false, new String[] { "field_76374_o", "isUnblockable" });
        }
        catch (Exception ex) {
            ModNaturalAbsorption.logError("Failed to remove armor ignore from source (" + source.toString() + ")!");
            ex.printStackTrace();
        }
        ShieldManager.MODDED_SOURCES.remove(source);
    }
    public static void clearSources() {
        try {
            for (Iterator<DamageSource> iterator = ShieldManager.MODDED_SOURCES.iterator(); iterator.hasNext();) {
                DamageSource source = iterator.next();
            	ObfuscationReflectionHelper.setPrivateValue(DamageSource.class, source, false, new String[] { "field_76374_o", "isUnblockable" });
            }
        }
        catch (Exception ex) {
            ModNaturalAbsorption.logError("Failed to clear armor ignore!");
            ex.printStackTrace();
        }
        ShieldManager.MODDED_SOURCES.clear();
    }

    // Gets the NBT tag compound that holds all of this mod's data for the given player.
    public static NBTTagCompound getShieldData(EntityPlayer player) {
        NBTTagCompound tag = player.getEntityData();
        if (!tag.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            tag.setTag(EntityPlayer.PERSISTED_NBT_TAG, tag = new NBTTagCompound());
        }
        else {
            tag = tag.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        }
        if (!tag.hasKey(ShieldManager.BASE_TAG)) {
            tag.setTag(ShieldManager.BASE_TAG, tag = new NBTTagCompound());
        }
        else {
            tag = tag.getCompoundTag(ShieldManager.BASE_TAG);
        }
        return tag;
    }

    // Used client-side to sync shield data from the server.
    @SideOnly(Side.CLIENT)
    public static void setShieldData(EntityPlayer player, NBTTagCompound shieldData) {
        NBTTagCompound tag = player.getEntityData();
        if (!tag.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            tag.setTag(EntityPlayer.PERSISTED_NBT_TAG, tag = new NBTTagCompound());
        }
        else {
            tag = tag.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        }
        if (tag.hasKey(ShieldManager.BASE_TAG)) {
            tag.removeTag(ShieldManager.BASE_TAG);
        }
        tag.setTag(ShieldManager.BASE_TAG, shieldData);
    }

    // Gets the tag data. Sets and returns the default if the tag is not present.
    public static int getData(NBTTagCompound tag, String name, int defaultValue) {
        if (!tag.hasKey(name)) {
            tag.setInteger(name, defaultValue);
            return defaultValue;
        }
        return tag.getInteger(name);
    }

    public static float getData(NBTTagCompound tag, String name, float defaultValue) {
        if (!tag.hasKey(name)) {
            tag.setFloat(name, defaultValue);
            return defaultValue;
        }
        return tag.getFloat(name);
    }

    // Returns the absorption bonus gained from potions and armor.
    public static float getArmorAbsorption(EntityPlayer player) {
        float bonus = 0.0F;
        if (Properties.get().ARMOR.REPLACE_ARMOR || Properties.get().ARMOR.MULTIPLIER_OVERRIDE) {
            bonus += Properties.get().ARMOR.MULTIPLIER * player.getTotalArmorValue();
        }

        if (ModNaturalAbsorption.ABSORB_ENCHANT != null) {
            int absorbLevel = 0;
            if (Properties.get().ENCHANT.STACKING) {
            	Iterable<ItemStack> equipment = ModNaturalAbsorption.ABSORB_ENCHANT.getEntityEquipment(player);
                if (equipment != null) for (ItemStack itemStack : equipment) {
                    absorbLevel += EnchantmentHelper.getEnchantmentLevel(ModNaturalAbsorption.ABSORB_ENCHANT, itemStack);
                }
            }
            else {
                absorbLevel = EnchantmentHelper.getMaxEnchantmentLevel(ModNaturalAbsorption.ABSORB_ENCHANT, player);
            }

            if (absorbLevel > 0) {
                bonus += Math.min(Properties.get().ENCHANT.POTENCY_MAX, Properties.get().ENCHANT.POTENCY_BASE + Properties.get().ENCHANT.POTENCY * absorbLevel);
            }
        }
        return bonus;
    }

    // Returns the absorption bonus gained from potions and armor.
    public static float getPotionAbsorption(EntityPlayer player) {
        float bonus = 0.0F;
        PotionEffect absorptionPotion = player.getActivePotionEffect(MobEffects.ABSORPTION);
        if (absorptionPotion != null) {
            bonus += absorptionPotion.getAmplifier() + 1 << 2;
        }
        return bonus;
    }

    // Updates the player's shield by the number of ticks since it was last updated.
    public static void updateShield(EntityPlayer player) {
        NBTTagCompound shieldData = ShieldManager.getShieldData(player);
        float shieldCapacity = ShieldManager.getData(shieldData, ShieldManager.CAPACITY_TAG, Properties.get().GENERAL.STARTING_SHIELD);
        if (shieldCapacity > Properties.get().GENERAL.MAX_SHIELD) {
            shieldData.setFloat(ShieldManager.CAPACITY_TAG, shieldCapacity = Properties.get().GENERAL.MAX_SHIELD);
        }
        shieldCapacity += ShieldManager.getArmorAbsorption(player);
        if (shieldCapacity > Properties.get().GENERAL.GLOBAL_MAX_SHIELD) {
            shieldCapacity = Properties.get().GENERAL.GLOBAL_MAX_SHIELD;
        }
        shieldCapacity += ShieldManager.getPotionAbsorption(player);

        float currentShield = player.getAbsorptionAmount();
        if (currentShield > shieldCapacity) {
            player.setAbsorptionAmount(shieldCapacity);
        }
        else {
            float recoveredShield = 0.0F;
            int delayTime = shieldData.getInteger(ShieldManager.DELAY_TAG);
            if (delayTime >= 0) {
                if (delayTime > 0) {
                    if (delayTime <= Properties.get().RECOVERY.UPDATE_TIME) {
                        delayTime = Properties.get().RECOVERY.UPDATE_TIME - delayTime;
                        if (delayTime > 0) {
                            recoveredShield = Properties.get().RECOVERY.RATE * delayTime;
                        }
                        shieldData.setInteger(ShieldManager.DELAY_TAG, 0);
                    }
                    else {
                        shieldData.setInteger(ShieldManager.DELAY_TAG, delayTime - Properties.get().RECOVERY.UPDATE_TIME);
                    }
                }
                else {
                    recoveredShield = Properties.get().RECOVERY.RATE * Properties.get().RECOVERY.UPDATE_TIME;
                }

                if (recoveredShield > 0.0F && currentShield < shieldCapacity) {
                    recoveredShield += currentShield;
                    if (recoveredShield > shieldCapacity) {
                        player.setAbsorptionAmount(shieldCapacity);
                    }
                    else {
                        player.setAbsorptionAmount(recoveredShield);
                    }
                }
            }
        }
    }

    // The counter to the next damage source purge.
    private int cleanupCounter = 0;
    // The counter to the next update.
    private int updateCounter = 0;

    public ShieldManager() {
        if (Properties.get().RECOVERY.DELAY >= 0) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    /**
     * Called each tick.
     * TickEvent.Type type = the type of tick.
     * Side side = the side this tick is on.
     * TickEvent.Phase phase = the phase of this tick (START, END).
     *
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Counter for damage source cleanup.
            if (++this.cleanupCounter >= 100) {
                this.cleanupCounter = 0;
                ShieldManager.clearSources();
            }
            // Counter for player shield update.
            if (++this.updateCounter >= Properties.get().RECOVERY.UPDATE_TIME) {
                this.updateCounter = 0;
                WorldServer[] worlds = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers;
                for (WorldServer world : worlds) {
                    if (world != null) {
                        for (Object entity : new ArrayList(world.playerEntities)) {
                            if (entity instanceof EntityPlayer) {
                                ShieldManager.updateShield((EntityPlayer) entity);
                            }
                        }
                    }
                }
            }
        }
    }
}