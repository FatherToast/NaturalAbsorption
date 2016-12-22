package toast.naturalAbsorption;

import java.util.Arrays;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;

/**
 * This helper class loads, stores, and retrieves config options.
 */
public class Properties {

	public static Properties get() {
		return Properties.INSTANCE;
	}
	public static void load(Configuration configuration) {
		Properties.config = configuration;
        Properties.config.load();
        Properties.INSTANCE = new Properties();
        Properties.config.save();
		Properties.config = null;
	}


	public final GENERAL GENERAL = new GENERAL();
	public class GENERAL extends PropertyCategory {
		@Override
		public String name() { return "_general"; }
		@Override
		protected String comment() {
			return "General and/or miscellaneous options.\n"
				+ "All absorption amounts are in half hearts.";
		}

        public final boolean DEBUG = this.prop("_debug_mode", false,
        	"If true, the mod will start up in debug mode.");

        public final float DEATH_PENALTY = this.prop("death_penalty", 4.0F,
        	"The amount of natural absorption a player loses with each death. Will not reduce below \'min_absorption\'.");

        public final float GLOBAL_MAX_SHIELD = this.prop("global_max_absorption", Float.POSITIVE_INFINITY,
        	"The total maximum absorption a player may obtain through natural, enchant, and/or armor replacement absorption.\n"
        	+ "Does not include max absorption gained from potions.");

        public final float MAX_SHIELD = this.prop("max_absorption", 20.0F,
        	"The maximum natural absorption a player may obtain.\n"
        	+ "Does not include max absorption gained from potions or armor.");

        public final float MIN_SHIELD = this.prop("min_absorption", 8.0F,
        	"A player will not drop below this much natural absorption due to death penalty.");

        public final boolean RECOVER_ON_SPAWN = this.prop("recover_on_spawn", true,
        	"If true, players will start out and respawn with full absorption shields (instead of 0).");

        public final float STARTING_SHIELD = this.prop("starting_absorption", 4.0F,
        	"The amount of natural absorption a new player starts with.");

    };

	public final ARMOR ARMOR = new ARMOR();
	public class ARMOR extends PropertyCategory {
		@Override
		public String name() { return "armor"; }
		@Override
		protected String comment() {
			return "Options related to armor replacement.\n"
				+ "This part of the mod is disabled by default; enable \"_replace_armor\" to activate it.";
		}

		public final boolean REPLACE_ARMOR = this.prop("_replace_armor", false,
			"If true, player armor will provide max absorption instead of damage reduction.");

		public final boolean FRIENDLY_DURABILITY = this.prop("durability_friendly", true,
			"If true, armor will only take durability damage based on damage dealt to your absorption when \"_replace_armor\" is enabled.");

		public final float DURABILITY_MULT = this.prop("durability_multiplier", 2.0F,
        	"The multiplier applied to durability damage when \"_replace_armor\" is enabled.");

        public final String DURABILITY_TRIGGER = this.prop("durability_trigger", "all",
        	"How armor durability is damaged when \"_replace_armor\" is enabled.\n"
        	+ " all     - all damage\n"
        	+ " vanilla - only damage normally affected by armor\n"
        	+ " hits    - all damage except damage-over-time effects (poison, burning, etc.)",
        	"all", "vanilla", "hits");

		public final boolean HIDE_ARMOR_BAR = this.prop("hide_armor_bar", true,
			"If true, the armor bar will not be rendered when \"_replace_armor\" is enabled.");

		public final float MULTIPLIER = this.prop("multiplier", 1.0F,
			"If \"_replace_armor\" or \"multiplier_override\" is enabled, this is the amount of max absorption that armor grants per armor point.\n"
			+ "Limited by \"global_max_absorption\".");

		public final boolean MULTIPLIER_OVERRIDE = this.prop("multiplier_override", false,
			"If true, players will gain absorption from their armor, even if \"_replace_armor\" is not enabled.\n"
			+ "Note that this does not enable the armor durability controls.");

    }

	public final ENCHANT ENCHANT = new ENCHANT();
	public class ENCHANT extends PropertyCategory {
		@Override
		public String name() { return "enchantment"; }
		@Override
		protected String comment() {
			return "Options for the Absorption enchantment.";
		}

		public final boolean BOOKS = this.prop("books", true,
			"If false, the Absorption enchantment will not be allowed on books.");

		public final int ID = this.prop("id", 216,
			"The enchantment id for the Absorption enchantment. Set to 0 to disable the enchantment entirely.\n"
			+ "This is for savegame data only. The id is \"" + ModNaturalAbsorption.MODID + ":" + "absorption" + "\" for practical purposes.",
			PropertyCategory.RINT_SRT_POS);

		public final float POTENCY = this.prop("potency", 4.0F,
			"Max absorption gained for each rank of the Absorption enchantment.");

        public final float POTENCY_BASE = this.prop("potency_base", 2.0F,
        	"Max absorption gained for for having at least one rank of the Absorption enchantment. A negative value reduces the effect of the first rank(s).",
        	PropertyCategory.RFLT_ALL);

        public final float POTENCY_MAX = this.prop("potency_max", 20.0F,
        	"The limit on max absorbtion that can be gained from Absorption enchantments on a single player.");

        public final String RARITY = this.prop("rarity", "rare",
        	"The rarity of the Absorption enchantment. Relates to how often it is selected when enchanting a valid item.",
    		"common", "uncommon", "rare", "very_rare");

        public final String SLOT = this.prop("slot", "any",
        	"The slot the Absorption enchantment is normally applicable to. Will still work on any armor piece if force-applied (e.g., creative mode anvil).",
        	"any", "head", "chest", "legs", "feet");

        public final boolean STACKING = this.prop("stacking", true,
        	"If false, only the highest level Absorption enchantment will be counted. Otherwise, all equipped Absorption enchantments are added together.");

        public final boolean TREASURE = this.prop("treasure", false,
        	"If true, the Absorption enchantment will not be generated by enchanting tables.");

    }

	public final RECOVERY RECOVERY = new RECOVERY();
	public class RECOVERY extends PropertyCategory {
		@Override
		public String name() { return "recovery"; }
		@Override
		protected String comment() {
			return "Options relating to absorption shield recovery.";
		}

		public final int DELAY = this.prop("recover_delay", 8 * 20,
			"The amount of time (in ticks) a player must go without taking damage before thier absorption shield begins to recover. (20 ticks = 1 second)\n"
			+ "If this is less than 0, players will not naturally recover lost absorption shields.",
			PropertyCategory.RINT_TOKEN_NEG);

		public final float RATE = this.prop("recover_rate", 0.1F,
			"The amount of absorption health regenerated each tick while recovering. (0.1 health/tick = 1 heart/second)");

		public final int UPDATE_TIME = this.prop("update_time", 5,
        	"The number of ticks between shield recovery updates. (20 ticks = 1 second)",
			PropertyCategory.RINT_POS1);

    }

	public final UPGRADES UPGRADES = new UPGRADES();
	public class UPGRADES extends PropertyCategory {
		@Override
		public String name() { return "upgrades"; }
		@Override
		protected String comment() {
			return "Options related to upgrading your natural absorption shield.";
		}

		public final float ABSORPTION_GAIN = this.prop("absorption_gain", 4.0F,
			"The amount of max natural absorption gained when a book of absorption is used.");

		public final int LEVEL_COST = this.prop("level_cost", 20,
        	"The number of levels required to use a book of absorption.");

        public final int RECIPE = this.prop("recipe", 3,
        	"The recipe for making a book of absorption.\n"
        	+ " 0 - cannot be crafted\n"
        	+ " 1 - book + item (shapeless)\n"
        	+ " 2 - book surrounded by 4 items\n"
        	+ " 3 - book surrounded by 8 items",
        	0, 3);

        public final boolean RECIPE_ALT = this.prop("recipe_alt", true,
        	"If true, a book and quill will be required to craft a book of absorption instead of a regular book.");

        public final String RECIPE_ITEM = this.prop("recipe_item", Item.REGISTRY.getNameForObject(Items.GOLDEN_APPLE).toString(),
        	"The item id of the item required in the absorption book recipe.", "mod_id:item_name");

        public final int RECIPE_ITEM_DAMAGE = this.prop("recipe_item_damage", 0,
        	"The item damage required for the item in the recipe. " + OreDictionary.WILDCARD_VALUE + " will match any damage value.",
        	PropertyCategory.RINT_SRT_POS);

    }


	private static Configuration config;
	private static Properties INSTANCE;

    // Contains basic implementations for all config option types, along with some useful constants.
	private static abstract class PropertyCategory {

		/** Range: { -INF, INF } */
		protected static final double[] RDBL_ALL = { Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY };
		/** Range: { 0.0, INF } */
		protected static final double[] RDBL_POS = { 0.0, Double.POSITIVE_INFINITY };
		/** Range: { 0.0, 1.0 } */
		protected static final double[] RDBL_ONE = { 0.0, 1.0 };

		/** Range: { -INF, INF } */
		protected static final float[] RFLT_ALL = { Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY };
		/** Range: { 0.0, INF } */
		protected static final float[] RFLT_POS = { 0.0F, Float.POSITIVE_INFINITY };
		/** Range: { 0.0, 1.0 } */
		protected static final float[] RFLT_ONE = { 0.0F, 1.0F };

		/** Range: { MIN, MAX } */
		protected static final int[] RINT_ALL = { Integer.MIN_VALUE, Integer.MAX_VALUE };
		/** Range: { -1, MAX } */
		protected static final int[] RINT_TOKEN_NEG = { -1, Integer.MAX_VALUE };
		/** Range: { 0, MAX } */
		protected static final int[] RINT_POS0 = { 0, Integer.MAX_VALUE };
		/** Range: { 1, MAX } */
		protected static final int[] RINT_POS1 = { 1, Integer.MAX_VALUE };
		/** Range: { 0, SRT } */
		protected static final int[] RINT_SRT_POS = { 0, Short.MAX_VALUE };
		/** Range: { 0, 255 } */
		protected static final int[] RINT_BYT_UNS = { 0, 0xff };
		/** Range: { 0, 127 } */
		protected static final int[] RINT_BYT_POS = { 0, Byte.MAX_VALUE };

		public PropertyCategory() {
	        Properties.config.addCustomCategoryComment(this.name(), this.comment());
		}

		public abstract String name();
		protected abstract String comment();

		protected double[] defaultDblRange() {
			return PropertyCategory.RDBL_POS;
		}
		protected float[] defaultFltRange() {
			return PropertyCategory.RFLT_POS;
		}
		protected int[] defaultIntRange() {
			return PropertyCategory.RINT_POS0;
		}

		protected boolean prop(String key, boolean defaultValue, String comment) {
	    	return this.cprop(key, defaultValue, comment).getBoolean();
	    }
		protected Property cprop(String key, boolean defaultValue, String comment) {
	    	comment = this.amendComment(comment, "Boolean", defaultValue, new Object[] { true, false });
	    	return Properties.config.get(this.name(), key, defaultValue, comment);
	    }

		protected boolean[] prop(String key, boolean[] defaultValues, String comment) {
	    	return this.cprop(key, defaultValues, comment).getBooleanList();
	    }
		protected Property cprop(String key, boolean[] defaultValues, String comment) {
	    	comment = this.amendComment(comment, "Boolean_Array", Arrays.asList(defaultValues).toArray(), new Object[] { true, false });
	    	return Properties.config.get(this.name(), key, defaultValues, comment);
	    }

		protected int prop(String key, int defaultValue, String comment) {
	    	return this.cprop(key, defaultValue, comment).getInt();
	    }
		protected int prop(String key, int defaultValue, String comment, int... range) {
	    	return this.cprop(key, defaultValue, comment, range).getInt();
	    }
		protected Property cprop(String key, int defaultValue, String comment) {
	    	return this.cprop(key, defaultValue, comment, this.defaultIntRange());
	    }
		protected Property cprop(String key, int defaultValue, String comment, int... range) {
	    	comment = this.amendComment(comment, "Integer", defaultValue, range[0], range[1]);
	    	return Properties.config.get(this.name(), key, defaultValue, comment, range[0], range[1]);
	    }

		protected int[] prop(String key, int[] defaultValues, String comment) {
	    	return this.cprop(key, defaultValues, comment).getIntList();
	    }
		protected int[] prop(String key, int[] defaultValues, String comment, int... range) {
	    	return this.cprop(key, defaultValues, comment, range).getIntList();
	    }
		protected Property cprop(String key, int[] defaultValues, String comment) {
	    	return this.cprop(key, defaultValues, comment, this.defaultIntRange());
	    }
		protected Property cprop(String key, int[] defaultValues, String comment, int... range) {
	    	comment = this.amendComment(comment, "Integer_Array", Arrays.asList(defaultValues).toArray(), range[0], range[1]);
	    	return Properties.config.get(this.name(), key, defaultValues, comment, range[0], range[1]);
	    }

		protected float prop(String key, float defaultValue, String comment) {
	    	return (float) this.cprop(key, defaultValue, comment).getDouble();
	    }
	    protected float prop(String key, float defaultValue, String comment, float... range) {
	    	return (float) this.cprop(key, defaultValue, comment, range).getDouble();
	    }
	    protected Property cprop(String key, float defaultValue, String comment) {
	    	return this.cprop(key, defaultValue, comment, this.defaultFltRange());
	    }
	    protected Property cprop(String key, float defaultValue, String comment, float... range) {
	    	comment = this.amendComment(comment, "Float", defaultValue, range[0], range[1]);
	    	return Properties.config.get(this.name(), key, this.prettyFloatToDouble(defaultValue), comment, this.prettyFloatToDouble(range[0]), this.prettyFloatToDouble(range[1]));
	    }

		protected double prop(String key, double defaultValue, String comment) {
	    	return this.cprop(key, defaultValue, comment).getDouble();
	    }
	    protected double prop(String key, double defaultValue, String comment, double... range) {
	    	return this.cprop(key, defaultValue, comment, range).getDouble();
	    }
	    protected Property cprop(String key, double defaultValue, String comment) {
	    	return this.cprop(key, defaultValue, comment, this.defaultDblRange());
	    }
	    protected Property cprop(String key, double defaultValue, String comment, double... range) {
	    	comment = this.amendComment(comment, "Double", defaultValue, range[0], range[1]);
	    	return Properties.config.get(this.name(), key, defaultValue, comment, range[0], range[1]);
	    }

	    protected double[] prop(String key, double[] defaultValues, String comment) {
	    	return this.cprop(key, defaultValues, comment).getDoubleList();
	    }
	    protected double[] prop(String key, double[] defaultValues, String comment, double... range) {
	    	return this.cprop(key, defaultValues, comment, range).getDoubleList();
	    }
	    protected Property cprop(String key, double[] defaultValues, String comment) {
	    	return this.cprop(key, defaultValues, comment, this.defaultDblRange());
	    }
	    protected Property cprop(String key, double[] defaultValues, String comment, double... range) {
	    	comment = this.amendComment(comment, "Double_Array", Arrays.asList(defaultValues).toArray(), range[0], range[1]);
	    	return Properties.config.get(this.name(), key, defaultValues, comment, range[0], range[1]);
	    }

	    protected String prop(String key, String defaultValue, String comment, String valueDescription) {
	    	return this.cprop(key, defaultValue, comment, valueDescription).getString();
	    }
	    protected String prop(String key, String defaultValue, String comment, String... validValues) {
	    	return this.cprop(key, defaultValue, comment, validValues).getString();
	    }
	    protected Property cprop(String key, String defaultValue, String comment, String valueDescription) {
	    	comment = this.amendComment(comment, "String", defaultValue, valueDescription);
	    	return Properties.config.get(this.name(), key, defaultValue, comment, new String[0]);
	    }
	    protected Property cprop(String key, String defaultValue, String comment, String... validValues) {
	    	comment = this.amendComment(comment, "String", defaultValue, validValues);
	    	return Properties.config.get(this.name(), key, defaultValue, comment, validValues);
	    }

	    private String amendComment(String comment, String type, Object[] defaultValues, String description) {
	    	return this.amendComment(comment, type, this.toReadable(defaultValues), description);
	    }
	    private String amendComment(String comment, String type, Object[] defaultValues, Object min, Object max) {
	    	return this.amendComment(comment, type, this.toReadable(defaultValues), min, max);
	    }
	    private String amendComment(String comment, String type, Object[] defaultValues, Object[] validValues) {
	    	return this.amendComment(comment, type, this.toReadable(defaultValues), validValues);
	    }
	    private String amendComment(String comment, String type, Object defaultValue, String description) {
	    	return new StringBuilder(comment).append("\n   >> ").append(type).append(":[ ")
	    		.append("Value={ ").append(description).append(" }, Default=").append(defaultValue)
	    		.append(" ]").toString();
	    }
	    private String amendComment(String comment, String type, Object defaultValue, Object min, Object max) {
	    	return new StringBuilder(comment).append("\n   >> ").append(type).append(":[ ")
	    		.append("Range={ ").append(min).append(", ").append(max).append(" }, Default=").append(defaultValue)
	    		.append(" ]").toString();
	    }
	    private String amendComment(String comment, String type, Object defaultValue, Object[] validValues) {
	    	if (validValues.length < 2) throw new IllegalArgumentException("Attempted to create config with no options!");

	    	return new StringBuilder(comment).append("\n   >> ").append(type).append(":[ ")
	    		.append("Valid_Values={ ").append(this.toReadable(validValues)).append(" }, Default=").append(defaultValue)
	    		.append(" ]").toString();
	    }

	    private double prettyFloatToDouble(float f) {
	    	return Double.parseDouble(Float.toString(f));
	    }
	    private String toReadable(Object[] array) {
	    	if (array.length <= 0) return "";

	    	StringBuilder commentBuilder = new StringBuilder();
    		for (Object value : array) {
    			commentBuilder.append(value).append(", ");
    		}
    		return commentBuilder.substring(0, commentBuilder.length() - 2).toString();
	    }
	}
}