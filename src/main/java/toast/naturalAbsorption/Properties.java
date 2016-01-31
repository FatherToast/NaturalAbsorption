package toast.naturalAbsorption;

import java.util.HashMap;
import java.util.Random;

import net.minecraftforge.common.config.Configuration;

/**
 * This helper class automatically creates, stores, and retrieves properties.
 * Supported data types:
 * String, boolean, int, double
 * 
 * Any property can be retrieved as an Object or String.
 * Any non-String property can also be retrieved as any other non-String property.
 * Retrieving a number as a boolean will produce a randomized output depending on the value.
 */
public abstract class Properties {
    // Mapping of all properties in the mod to their values.
    private static final HashMap<String, Object> map = new HashMap();
    // Common category names.
    public static final String GENERAL = "_general";
    public static final String ARMOR = "armor";
    public static final String ENCHANT = "enchantment";
    public static final String RECOVERY = "recovery";
    public static final String UPGRADES = "upgrades";

    // Initializes these properties.
    public static void init(Configuration config) {
        config.load();

        Properties.add(config, Properties.GENERAL, "death_penalty", 4.0, "The amount of natural absorption a player loses with each death. Will not reduce below \'min_absorption\'. Default is 4.0.");
        Properties.add(config, Properties.GENERAL, "global_max_absorption", Double.POSITIVE_INFINITY, "The total maximum absorption a player may obtain through natural, enchant, and/or armor replacement absorption. Absorption from potions is unaffected. (Set to Infinity for no max.) Default is Infinity.");
        Properties.add(config, Properties.GENERAL, "max_absorption", 20.0, "The maximum natural absorption a player may obtain. Default is 20.0.");
        Properties.add(config, Properties.GENERAL, "min_absorption", 8.0, "A player will not drop below this much natural absorption due to death penalty. Default is 8.0.");
        Properties.add(config, Properties.GENERAL, "recover_on_spawn", true, "If true, players will start out and respawn with full absorption shields (instead of 0). Default is true.");
        Properties.add(config, Properties.GENERAL, "starting_absorption", 4.0, "The amount of natural absorption a player starts with. Default is 4.0.");

        Properties.add(config, Properties.ARMOR, "_replace_armor", false, "If true, player armor will provide max absorption instead of damage reduction (does not work well without passive recovery enabled). Default is false.");
        Properties.add(config, Properties.ARMOR, "hide_armor_bar", true, "If true, the armor bar will not be rendered when \"replace_armor\" is enabled. Default is true.");
        Properties.add(config, Properties.ARMOR, "multiplier", 1.0, "If \"_replace_armor\" or \"multiplier_override\" is enabled, this is the amount of max absorption that armor grants per armor point. Default is 1.0.");
        Properties.add(config, Properties.ARMOR, "multiplier_override", false, "If true, players will gain absorption from their armor, even if \"replace_armor\" is disabled. Default is false.");

        Properties.add(config, Properties.ENCHANT, "id", 216, "The enchantment id for the absorption enchantment. Set to 0 to disable the enchantment entirely. Default is 216.");
        Properties.add(config, Properties.ENCHANT, "weight", 2, "The weight for the absorption enchantment. In other words, the higher this is, the more likely you are to get it when enchanting. Default is 2.\nReference weights: Protection=10, Fire/Projectile/Fall Protection=5, Blast Protection=2, Thorns=1.");
        Properties.add(config, Properties.ENCHANT, "potency", 4.0, "Max absorption gained for each rank of the absorption enchantment. Default is 4.0.");
        Properties.add(config, Properties.ENCHANT, "potency_base", 2.0, "Max absorption gained for for having at least one rank of the absorption enchantment (in addition to the potency). Default is 2.0.");
        Properties.add(config, Properties.ENCHANT, "potency_max", 20.0, "The most max absorbtion that can be gained from the absorption enchantment. Set this to -1 for no limit. Default is 20.0.");
        Properties.add(config, Properties.ENCHANT, "stacking", true, "If false, only the highest level absorption enchantment will be counted. Otherwise, all equipped absorption enchantments are added together. Default is true.");

        Properties.add(config, Properties.RECOVERY, "recover_delay", 200, "The amount of time (in ticks) a player must go without taking damage before his/her absorption shield begins to recover. If this is less than 0, players will not naturally recover lost absorption shields. Default is 200.");
        Properties.add(config, Properties.RECOVERY, "recover_rate", 0.1, "The amount of absorption health regenerated each tick while recovering. Default is 0.1.");
        Properties.add(config, Properties.RECOVERY, "update_time", 5, "The number of ticks between shield recovery updates. Default is 5.");

        Properties.add(config, Properties.UPGRADES, "absorption_gain", 4.0, "The amount of maximum absorption gained when a book of absorption is used. Default is 4.0.");
        Properties.add(config, Properties.UPGRADES, "compatible_recipe", false, "If true, the recipe will be registered differently, which will hide it from mods (including recipe guides). Only use this for compatibility reasons. Default is false.");
        Properties.add(config, Properties.UPGRADES, "level_cost", 30, "The number of levels required to use a book of absorption. Default is 30.");
        Properties.add(config, Properties.UPGRADES, "recipe", 3, "The recipe for making a book of absorption. Default is 3.\n 0 - cannot be crafted\n 1 - book + item\n 2 - book surrounded by 4 items\n 3 - book surrounded by 8 items");
        Properties.add(config, Properties.UPGRADES, "recipe_alt", false, "If true, a book and quill will be required to craft a book of absorption instead of a regular book. Default is false.");
        Properties.add(config, Properties.UPGRADES, "recipe_item", "golden_apple", "The item id of the item required in the absorption book recipe. Default is golden_apple.");
        Properties.add(config, Properties.UPGRADES, "recipe_item_damage", 0, "The item damage required for the item in the recipe. 32767 will match any damage value. Default is 0 (regular golden apple; 1 is epic).");

        config.addCustomCategoryComment(Properties.GENERAL, "General and/or miscellaneous options.\nAll absorption amounts are in half hearts.");
        config.addCustomCategoryComment(Properties.ARMOR, "Options related to armor replacement.");
        config.addCustomCategoryComment(Properties.ENCHANT, "Options for the Absorption enchantment.");
        config.addCustomCategoryComment(Properties.RECOVERY, "Options relating to absorption shield recovery.");
        config.addCustomCategoryComment(Properties.UPGRADES, "Options related to upgrading your absorption shield.");
        config.save();
    }

    // Gets the mod's random number generator.
    public static Random random() {
        return _NaturalAbsorption.random;
    }

    // Passes to the mod.
    public static void debugException(String message) {
        _NaturalAbsorption.debugException(message);
    }

    // Loads the property as the specified value.
    public static void add(Configuration config, String category, String field, String defaultValue, String comment) {
        Properties.map.put(category + "@" + field, config.get(category, field, defaultValue, comment).getString());
    }

    public static void add(Configuration config, String category, String field, int defaultValue, String comment) {
        Properties.map.put(category + "@" + field, Integer.valueOf(config.get(category, field, defaultValue, comment).getInt(defaultValue)));
    }

    public static void add(Configuration config, String category, String field, boolean defaultValue, String comment) {
        Properties.map.put(category + "@" + field, Boolean.valueOf(config.get(category, field, defaultValue, comment).getBoolean(defaultValue)));
    }

    public static void add(Configuration config, String category, String field, double defaultValue, String comment) {
        Properties.map.put(category + "@" + field, Double.valueOf(config.get(category, field, defaultValue, comment).getDouble(defaultValue)));
    }

    // Gets the Object property.
    public static Object getProperty(String category, String field) {
        return Properties.map.get(category + "@" + field);
    }

    // Gets the value of the property (instead of an Object representing it).
    public static String getString(String category, String field) {
        return Properties.getProperty(category, field).toString();
    }

    public static boolean getBoolean(String category, String field) {
        Object property = Properties.getProperty(category, field);
        if (property instanceof Boolean)
            return ((Boolean) property).booleanValue();
        if (property instanceof Integer)
            return Properties.random().nextInt( ((Number) property).intValue()) == 0;
        if (property instanceof Double)
            return Properties.random().nextDouble() < ((Number) property).doubleValue();
        Properties.debugException("Tried to get boolean for invalid property! @" + property == null ? "(null)" : property.getClass().getName());
        return false;
    }

    public static int getInt(String category, String field) {
        Object property = Properties.getProperty(category, field);
        if (property instanceof Number)
            return ((Number) property).intValue();
        if (property instanceof Boolean)
            return ((Boolean) property).booleanValue() ? 1 : 0;
        Properties.debugException("Tried to get int for invalid property! @" + property == null ? "(null)" : property.getClass().getName());
        return 0;
    }

    public static double getDouble(String category, String field) {
        Object property = Properties.getProperty(category, field);
        if (property instanceof Number)
            return ((Number) property).doubleValue();
        if (property instanceof Boolean)
            return ((Boolean) property).booleanValue() ? 1.0 : 0.0;
        Properties.debugException("Tried to get double for invalid property! @" + property == null ? "(null)" : property.getClass().getName());
        return 0.0;
    }
}