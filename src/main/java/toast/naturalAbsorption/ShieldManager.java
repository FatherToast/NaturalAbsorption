package toast.naturalAbsorption;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ShieldManager {
    // Useful properties for this class.
    public static final boolean REPLACE_ARMOR = Properties.getBoolean(Properties.ARMOR, "_replace_armor");
    public static final float ARMOR_MULT = (float) Properties.getDouble(Properties.ARMOR, "multiplier");
    public static final boolean ARMOR_MULT_OVERRIDE = Properties.getBoolean(Properties.ARMOR, "multiplier_override");

    public static final float ENCHANT_POTENCY = Math.max(0.0F, (float) Properties.getDouble(Properties.ENCHANT, "potency"));
    public static final float ENCHANT_BASE = Math.max(0.0F, (float) Properties.getDouble(Properties.ENCHANT, "potency_base"));
    public static final float ENCHANT_MAX = (float) Properties.getDouble(Properties.ENCHANT, "potency_max");
    public static final boolean ENCHANT_STACK = Properties.getBoolean(Properties.ENCHANT, "stacking");

    public static final float STARTING_SHIELD = (float) Properties.getDouble(Properties.GENERAL, "starting_absorption");
    public static final float MAX_SHIELD = (float) Properties.getDouble(Properties.GENERAL, "max_absorption");

    public static final float GLOBAL_MAX_SHIELD = (float) Properties.getDouble(Properties.GENERAL, "global_max_absorption");

    public static final int RECOVER_DELAY = Properties.getInt(Properties.RECOVERY, "recover_delay");
    public static final float RECOVER_RATE = (float) Properties.getDouble(Properties.RECOVERY, "recover_rate");
    public static final int TICKS_PER_UPDATE = Math.max(1, Properties.getInt(Properties.RECOVERY, "update_time"));
    public static final int UPDATE_TIME = ShieldManager.TICKS_PER_UPDATE - 1;

    // The NBT tags used to store all of this mod's NBT info.
    public static final String BASE_TAG = "NaturalAbsorption";
    public static final String CAPACITY_TAG = "Capacity";
    public static final String DELAY_TAG = "Delay";

    // Map of all currently altered damage sources to their.
    private static final HashMap<DamageSource, Field> MODDED_SOURCES = new HashMap<DamageSource, Field>();

    // Returns true if the given damage source is modified to ignore armor.
    public static boolean isSourceModified(DamageSource source) {
        return ShieldManager.MODDED_SOURCES.containsKey(source);
    }

    // Modifies or unmodifies a source to ignore armor.
    public static void modifySource(DamageSource source) {
        try {
            Field field = DamageSource.class.getDeclaredFields()[14]; /// isUnblockable
            field.setAccessible(true);
            field.setBoolean(source, true);
            ShieldManager.MODDED_SOURCES.put(source, field);
        }
        catch (Exception ex) {
            _NaturalAbsorption.console("Error forcing armor ignore!");
        }
    }

    public static void unmodifySource(DamageSource source) {
        try {
            Field field = ShieldManager.MODDED_SOURCES.get(source);
            if (field != null) {
                field.setBoolean(source, false);
            }
        }
        catch (Exception ex) {
            _NaturalAbsorption.console("Error removing armor ignore! (active)");
        }
        ShieldManager.MODDED_SOURCES.remove(source);
    }

    public static void clearSource() {
        try {
            for (Iterator<Map.Entry<DamageSource, Field>> iterator = ShieldManager.MODDED_SOURCES.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<DamageSource, Field> entry = iterator.next();
                Field field = entry.getValue();
                if (field != null) {
                    field.setBoolean(entry.getKey(), false);
                }
            }
        }
        catch (Exception ex) {
            _NaturalAbsorption.console("Error removing armor ignore!");
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
        if (ShieldManager.REPLACE_ARMOR || ShieldManager.ARMOR_MULT_OVERRIDE) {
            bonus += ShieldManager.ARMOR_MULT * player.getTotalArmorValue();
        }

        if (_NaturalAbsorption.absorbEnchant != null) {
            int absorbLevel = 0;
            if (ShieldManager.ENCHANT_STACK) {
                for (ItemStack itemStack : player.getLastActiveItems()) {
                    absorbLevel += EnchantmentHelper.getEnchantmentLevel(_NaturalAbsorption.absorbEnchant.effectId, itemStack);
                }
            }
            else {
                absorbLevel = EnchantmentHelper.getMaxEnchantmentLevel(_NaturalAbsorption.absorbEnchant.effectId, player.getLastActiveItems());
            }

            if (absorbLevel > 0) {
                bonus += ShieldManager.ENCHANT_MAX < 0.0F ? ShieldManager.ENCHANT_BASE + ShieldManager.ENCHANT_POTENCY * absorbLevel : Math.min(ShieldManager.ENCHANT_MAX, ShieldManager.ENCHANT_BASE + ShieldManager.ENCHANT_POTENCY * absorbLevel);
            }
        }
        return bonus;
    }

    // Returns the absorption bonus gained from potions and armor.
    public static float getPotionAbsorption(EntityPlayer player) {
        float bonus = 0.0F;
        PotionEffect absorptionPotion = player.getActivePotionEffect(Potion.field_76444_x); /// absorption
        if (absorptionPotion != null) {
            bonus += absorptionPotion.getAmplifier() + 1 << 2;
        }
        return bonus;
    }

    // Updates the player's shield by the number of ticks since it was last updated.
    public static void updateShield(EntityPlayer player) {
        NBTTagCompound shieldData = ShieldManager.getShieldData(player);
        float shieldCapacity = ShieldManager.getData(shieldData, ShieldManager.CAPACITY_TAG, ShieldManager.STARTING_SHIELD);
        if (shieldCapacity > ShieldManager.MAX_SHIELD) {
            shieldData.setFloat(ShieldManager.CAPACITY_TAG, shieldCapacity = ShieldManager.MAX_SHIELD);
        }
        shieldCapacity += ShieldManager.getArmorAbsorption(player);
        if (shieldCapacity > ShieldManager.GLOBAL_MAX_SHIELD) {
            shieldCapacity = ShieldManager.GLOBAL_MAX_SHIELD;
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
                    if (delayTime <= ShieldManager.TICKS_PER_UPDATE) {
                        delayTime = ShieldManager.TICKS_PER_UPDATE - delayTime;
                        if (delayTime > 0) {
                            recoveredShield = ShieldManager.RECOVER_RATE * delayTime;
                        }
                        shieldData.setInteger(ShieldManager.DELAY_TAG, 0);
                    }
                    else {
                        shieldData.setInteger(ShieldManager.DELAY_TAG, delayTime - ShieldManager.TICKS_PER_UPDATE);
                    }
                }
                else {
                    recoveredShield = ShieldManager.RECOVER_RATE * ShieldManager.TICKS_PER_UPDATE;
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
        if (ShieldManager.RECOVER_DELAY >= 0) {
            FMLCommonHandler.instance().bus().register(this);
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
                ShieldManager.clearSource();
            }
            // Counter for player shield update.
            if (++this.updateCounter > ShieldManager.UPDATE_TIME) {
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