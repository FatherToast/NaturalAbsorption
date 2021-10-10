package fathertoast.naturalabsorption.common.config;

import fathertoast.naturalabsorption.*;
import fathertoast.naturalabsorption.common.health.HealthManager;
import net.minecraft.block.Block;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashSet;


/**
 * This helper class manages and stores references to user-defined configurations.
 */
public
class Config
{
	// Returns the main config.
	public static
	Config get( )
	{
		return Config.INSTANCE;
	}
	
	public static
	void load( Logger logger, String fileName, File configDir )
	{
		Config.log = logger;
		Config.log.info( "Loading configs..." );
		long startTime = System.nanoTime( );
		
		// Global mod config
		Config.configLoading = new Configuration( new File( configDir, fileName + ".cfg" ) );
		Config.configLoading.load( );
		Config.INSTANCE = new Config( );
		Config.configLoading.save( );
		Config.configLoading = null;
		
		long estimatedTime = System.nanoTime( ) - startTime;
		Config.log.info( "Loaded configs in {} ms", estimatedTime / 1.0E6 );
	}
	
	private
	Config( ) { }
	
	public final GENERAL GENERAL = new GENERAL( );
	
	public
	class GENERAL extends PropertyCategory
	{
		@Override
		String name( ) { return "_general"; }
		
		@Override
		String comment( )
		{
			return "General and/or miscellaneous options.";
		}
		
		public final int UPDATE_TIME = prop(
			"update_time", 5,
			"The number of ticks between recovery updates.\n" +
			"(20 ticks = 1 second)",
			R_INT_POS1
		);
	}
	
	public final ABSORPTION_HEALTH ABSORPTION_HEALTH = new ABSORPTION_HEALTH( );
	
	public
	class ABSORPTION_HEALTH extends PropertyCategory
	{
		@Override
		String name( ) { return "absorption_health"; }
		
		@Override
		String comment( )
		{
			return "Options relating to absorption health (yellow hearts) in general.\n" +
			       "All absorption amounts are in half-hearts.";
		}
		
		public final boolean ENABLED = prop(
			"_enabled", true,
			"Set this to false to disable all features from this mod related to absorption health.\n" +
			"That is, every feature in the mod except for the features in the \"normal_health\" category."
		);
		
		public final float DEATH_PENALTY = prop(
			"death_penalty", 2.0F,
			"The amount of natural absorption a player loses with each death. Will not drop below the death penalty limit."
		);
		
		public final float DEATH_PENALTY_LIMIT = prop(
			"death_penalty_limit", 8.0F,
			"A player will not drop below this much natural absorption due to death penalty."
		);
		
		public final float GLOBAL_MAXIMUM = prop(
			"global_max_absorption", Float.POSITIVE_INFINITY,
			"The total maximum absorption a player may obtain through natural, enchantments, and/or armor replacement.\n" +
			"Does not include max absorption gained from potions."
		);
		
		public final int RECOVER_DELAY = prop(
			"recover_delay", 120,
			"The amount of time (in ticks) a player must go without taking damage before their absorption shield\n" +
			"begins to recover. If this is set less than 0, players will not naturally recover lost absorption shields.\n" +
			"(20 ticks = 1 second)",
			R_INT_TOKEN_NEG
		);
		
		public final float RECOVER_RATE = prop(
			"recover_rate", 2.0F,
			"The amount of absorption health regenerated each second while recovering."
		) / 20.0F; // Conversion from health/second to health/tick
		
		public final float RECOVERY_ON_RESPAWN = prop(
			"recovery_on_respawn", 0.0F,
			"Players will respawn with up to this much absorption health, limited by their personal max absorption."
		);
		
		public final boolean RENDER_CAPACITY_BACKGROUND = prop(
			"render_capacity_bg", true,
			"If true, the mod will render the empty heart background behind absorption hearts you are missing,\n" +
			"but can regenerate back. This may not work right if another mod changes health bar rendering."
		);
		
		public final float STARTING_AMOUNT = prop(
			"starting_absorption", 4.0F,
			"The amount of natural absorption a new player starts with."
		);
		
		public final float HUNGER_COST = prop(
			"hunger_cost", 0F,
			"The amount of hunger or saturation drained for each point of absorption health regenerated.\n" +
			"Players can't lose more than 10 points of hunger or saturation at a time in this way."
		) * 4.0F; // Conversion from hunger/health to exhaustion/health
		
		public final int HUNGER_REQUIRED = prop(
			"hunger_required", 0,
			"Players need to have at least this much hunger to regenerate absorption health."
		);
	}
	
	public final ABSORPTION_UPGRADES ABSORPTION_UPGRADES = new ABSORPTION_UPGRADES( );
	
	public
	class ABSORPTION_UPGRADES extends PropertyCategory
	{
		@Override
		String name( ) { return "absorption_upgrades"; }
		
		@Override
		String comment( )
		{
			return "Options relating to increasing maximum absorption health (yellow hearts).\n" +
			       "All absorption amounts are in half-hearts.";
		}
		
		public final boolean ENABLED = prop(
			"_upgrades_enabled", true,
			"Set this to false to prevent players from upgrading their absorption health.\n" +
			"Also disables the Book of Absorption and its recipe."
		);
		
		public final float CAPACITY_GAIN = prop(
			"capacity_gain", 2.0F,
			"The amount of maximum natural absorption gained from each upgrade."
		);
		
		public final float LEVEL_COST = prop(
			"level_cost", -5.0F,
			"The base number of levels required to use a Book of Absorption.\n" +
			"The final level cost is rounded down to the nearest whole number and clamped between 0 and \"level_cost_limit\".",
			R_FLT_ALL
		);
		
		public final int LEVEL_COST_MAX = prop(
			"level_cost_limit", 30,
			"The maximum number of levels that will be required to use a Book of Absorption."
		);
		
		public final float LEVEL_COST_PER_POINT = prop(
			"level_cost_per_point", 2.5F,
			"The number of levels required to use a Book of Absorption for each point of maximum absorption\n" +
			"health the player already has.\n" +
			"The final level cost is rounded down to the nearest whole number and clamped between 0 and \"level_cost_limit\".",
			R_FLT_ALL
		);
		
		public final float MAXIMUM = prop(
			"max_absorption", 20.0F,
			"The maximum natural absorption a player may obtain from upgrades.\n" +
			"Does not include max absorption gained from potions or equipment.",
			0.0F, Float.MAX_VALUE
		);
		
		public final RecipeStyle.Type RECIPE = prop(
			"recipe", RecipeStyle.Type.CROSS,
			"The recipe for making a Book of Absorption.\n" +
			"  none     - <no recipe>\n" +
			"  simple   - aB  (book + apple, shapeless)\n" +
			"  sandwich - aBa (book + 2 apples)\n" +
			"  cross    -  a  (book + 4 apples)\n" +
			"             aBa \n" +
			"              a  \n" +
			"  surround - aaa (book + 8 apples)\n" +
			"             aBa \n" +
			"             aaa \n" +
			"B = book & quill, a = golden apple"
		);
		
		public final boolean SHOW_INFO_IN_TOOLTIP = prop(
			"show_info_in_tooltip", false,
			"Set to true to display current and max natural absorption on the Book of Absorption tooltip.\n" +
			"Particularly helpful if you must disable the heart background rendering."
		);
	}
	
	public final ARMOR ARMOR = new ARMOR( );
	
	public
	class ARMOR extends PropertyCategory
	{
		@Override
		String name( ) { return "armor"; }
		
		@Override
		String comment( )
		{
			return "Options related to the replacement of armor with absorption health.\n" +
			       "This part of the mod is disabled by default; enable armor replacement to activate it.";
		}
		
		public final boolean REPLACE_ARMOR = prop(
			"_armor_replacement", false,
			"If true, player armor will provide max absorption instead of damage reduction.\n" +
			"This option must be set to \'true\' for the majority of this config section to function."
		);
		
		public final float ARMOR_MULT = prop(
			"armor_multiplier", 1.0F,
			"The amount of max absorption that armor grants per armor point.\n" +
			"No effect if armor replacement AND armor multiplier override are disabled."
		);
		
		public final float ARMOR_TOUGHNESS_RECOVERY = prop(
			"armor_toughness_recovery", 0.07F,
			"The increase in absorption recovery rate for each point of armor toughness.\n" +
			"For reference, the maximum attainable toughness in vanilla is 8 (full diamond armor).\n" +
			"No effect if armor replacement is disabled."
		);
		
		public final boolean ARMOR_MULT_OVERRIDE = prop(
			"armor_multiplier_override", false,
			"Enable this option to grant the player absorption based on their armor points without disabling\n" +
			"armor\'s damage reduction. Enables ONLY the armor multiplier option in this section."
		);
		
		public final boolean DURABILITY_FRIENDLY = prop(
			"durability_friendly", true,
			"If true, armor will only take durability damage based on damage dealt to your absorption.\n" +
			"No effect if armor replacement is disabled."
		);
		
		public final float DURABILITY_MULT = prop(
			"durability_multiplier", 2.0F,
			"The multiplier applied to armor durability damage.\n" +
			"No effect if armor replacement is disabled."
		);
		
		public final float DURABILITY_THRESHOLD = prop(
			"durability_threshold", 1.0F,
			"Damage dealt to health must bypass this threshold value to cause durability damage.\n" +
			"No effect if armor replacement is disabled."
		);
		
		public final HealthManager.EnumDurabilityTrigger DURABILITY_TRIGGER = prop(
			"durability_trigger", HealthManager.EnumDurabilityTrigger.ALL,
			"Decide which damage sources can inflict durability damage.\n" +
			"No effect if armor replacement is disabled.\n" +
			"  all   - all damage except thorns\n" +
			"  hits  - all damage except thorns and damage-over-time (poison, burning, etc.)\n" +
			"  none  - no damage hurts armor"
		);
		
		public final boolean HIDE_ARMOR_BAR = prop(
			"hide_armor_bar", true,
			"If true, the (now much less useful) armor bar will not be rendered.\n" +
			"No effect if armor replacement is disabled."
		);
	}
	
	public final ENCHANTMENT ENCHANTMENT = new ENCHANTMENT( );
	
	public
	class ENCHANTMENT extends PropertyCategory
	{
		@Override
		String name( ) { return "enchantment"; }
		
		@Override
		String comment( )
		{
			return "Options related to the \'Absorption\' armor enchantment.";
		}
		
		public final boolean ENABLED = prop(
			"_enabled", true,
			"Set this to false to disable the Absorption enchantment entirely."
		);
		
		public final boolean BOOKS = prop(
			"books", true,
			"If false, the Absorption enchantment will not be allowed on books."
		);
		
		public final int ENCHANTIBILITY_BASE = prop(
			"enchantibility_base", 3,
			"Base enchantibility required.\n" +
			"Don't mess with this unless you are very familiar with enchanting mechanics."
		);
		
		public final int ENCHANTIBILITY_PER_LEVEL = prop(
			"enchantibility_per_level", 6,
			"Enchantibility required per enchantment level.\n" +
			"Don't mess with this unless you are very familiar with enchanting mechanics."
		);
		
		public final int MAXIMUM_LEVEL = prop(
			"level_max", 4,
			"Maximum level for the Absorption enchantment.\n" +
			"Without messing with enchantibility, it is recommended you only alter this to +/- 1 default max level."
		);
		
		public final float POTENCY = prop(
			"potency", 2.0F,
			"Max absorption gained for each rank of the Absorption enchantment."
		);
		
		public final float POTENCY_BASE = prop(
			"potency_base", 2.0F,
			"Max absorption gained for for having at least one rank of the Absorption enchantment.\n" +
			"A negative value reduces the effect of the first rank(s).",
			PropertyCategory.R_FLT_ALL
		);
		
		public final float POTENCY_MAX = prop(
			"potency_max", 20.0F,
			"The limit on max absorbtion that can be gained from Absorption enchantments on a single player."
		);
		
		public final ModObjects.EnchantRarity RARITY = prop(
			"rarity", ModObjects.EnchantRarity.RARE,
			"The rarity of the Absorption enchantment. Relates to how often it is selected when enchanting a valid item."
		);
		
		public final ModObjects.EnchantArmorType SLOT = prop(
			"slot", ModObjects.EnchantArmorType.ALL,
			"The slot the Absorption enchantment is normally applicable to. Will still work on any armor piece\n" +
			"if force-applied (e.g., creative mode anvil)."
		);
		
		public final boolean STACKING = prop(
			"stacking", true,
			"If false, only the highest level Absorption enchantment will be counted. Otherwise, all equipped\n" +
			"Absorption enchantments are added together (like vanilla Protection enchants)."
		);
		
		public final boolean TREASURE = prop(
			"treasure_only", false,
			"If true, the Absorption enchantment will not be generated by enchanting tables."
		);
	}
	
	public final NORMAL_HEALTH NORMAL_HEALTH = new NORMAL_HEALTH( );
	
	public
	class NORMAL_HEALTH extends PropertyCategory
	{
		@Override
		String name( ) { return "normal_health"; }
		
		@Override
		String comment( )
		{
			return "Options relating to normal health (red hearts).\n" +
			       "All normal health amounts are in half-hearts, hunger amounts are in half-shanks.";
		}
		
		public final boolean ENABLED = prop(
			"_enabled", true,
			"Set this to false to disable all features from this mod related to normal health.\n" +
			"That is, every feature in this specific category. You will need to reset the regen game rule manually."
		);
		
		public final boolean DISABLE_GAMERULE_REGEN = prop(
			"disable_gamerule_regen", true,
			"When set to true, this mod will constantly set the vanilla regeneration game rule \"naturalRegeneration\" to \"false\"\n" +
			"to disable other sources of normal health regeneration."
		);
		
		public final float FOOD_HEALING = prop(
			"food_healing", 0.25F,
			"The amount of normal health recovered for each point of hunger granted by eaten food.\n" +
			"Set this to 0 to disable healing from eating."
		);
		
		public final float HUNGER_COST = prop(
			"hunger_cost", 1.0F,
			"The amount of hunger or saturation drained for each point of normal health regenerated.\n" +
			"Players can't lose more than 10 points of hunger or saturation at a time in this way."
		) * 4.0F; // Conversion from hunger/health to exhaustion/health
		
		public final int HUNGER_REQUIRED = prop(
			"hunger_required", 6,
			"Players need to have at least this much hunger to regenerate normal health."
		);
		
		public final float MAXIMUM = prop(
			"max_regen", 6.0F,
			"The maximum that normal health that can be restored to by this mod's regeneration."
		);
		
		public final int RECOVER_DELAY = prop(
			"recover_delay", 20,
			"The amount of time (in ticks) a player must go without taking damage before their normal health\n" +
			"begins to recover. If this is set less than 0, the normal health recovery from this mod is disabled.\n" +
			"This ignores the regeneration game rule.\n" +
			"(20 ticks = 1 second)",
			R_INT_TOKEN_NEG
		);
		
		public final float RECOVER_RATE = prop(
			"recover_rate", 0.25F,
			"The amount of normal health regenerated each second while recovering."
		) / 20.0F; // Conversion from health/second to health/tick
		
		public final float RECOVERY_ON_RESPAWN = prop(
			"recovery_on_respawn", 6.0F,
			"Players will respawn with up to this much health, limited by their personal max health.\n" +
			"Set this to 0 to leave respawn health unchanged."
		);
	}
	
	
	static         Logger        log;
	// Config file currently being loaded. Null when not loading any file.
	private static Configuration configLoading;
	private static Config        INSTANCE;
	
	// Contains basic implementations for all config option types, along with some useful constants.
	@SuppressWarnings( { "unused", "SameParameterValue" } )
	private static abstract
	class PropertyCategory
	{
		/** Range: { -INF, INF } */
		static final double[] R_DBL_ALL = { Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY };
		/** Range: { 0.0, INF } */
		static final double[] R_DBL_POS = { 0.0, Double.POSITIVE_INFINITY };
		/** Range: { 0.0, 1.0 } */
		static final double[] R_DBL_ONE = { 0.0, 1.0 };
		
		/** Range: { -INF, INF } */
		static final float[] R_FLT_ALL = { Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY };
		/** Range: { 0.0, INF } */
		static final float[] R_FLT_POS = { 0.0F, Float.POSITIVE_INFINITY };
		/** Range: { 0.0, 1.0 } */
		static final float[] R_FLT_ONE = { 0.0F, 1.0F };
		
		/** Range: { MIN, MAX } */
		static final int[] R_INT_ALL       = { Integer.MIN_VALUE, Integer.MAX_VALUE };
		/** Range: { -1, MAX } */
		static final int[] R_INT_TOKEN_NEG = { -1, Integer.MAX_VALUE };
		/** Range: { 0, MAX } */
		static final int[] R_INT_POS0      = { 0, Integer.MAX_VALUE };
		/** Range: { 1, MAX } */
		static final int[] R_INT_POS1      = { 1, Integer.MAX_VALUE };
		/** Range: { 0, SRT } */
		static final int[] R_INT_SRT_POS   = { 0, Short.MAX_VALUE };
		/** Range: { 0, 255 } */
		static final int[] R_INT_BYT_UNS   = { 0, 0xff };
		/** Range: { 0, 127 } */
		static final int[] R_INT_BYT_POS   = { 0, Byte.MAX_VALUE };
		
		// Support for dynamically generated config categories.
		final String KEY;
		
		PropertyCategory( String key )
		{
			KEY = key;
			Config.configLoading.addCustomCategoryComment( name( ), comment( ) );
		}
		
		PropertyCategory( )
		{
			this( null );
		}
		
		abstract
		String name( );
		
		abstract
		String comment( );
		
		double[] defaultDblRange( )
		{
			return PropertyCategory.R_DBL_POS;
		}
		
		float[] defaultFltRange( )
		{
			return PropertyCategory.R_FLT_POS;
		}
		
		int[] defaultIntRange( )
		{
			return PropertyCategory.R_INT_POS0;
		}
		
		< T extends Enum< T > > T prop( String key, T defaultValue, String comment )
		{
			String name = cprop( key, defaultValue, comment ).getString( );
			
			//noinspection unchecked
			T[] enumValues = ((Class< T >) defaultValue.getClass( )).getEnumConstants( );
			
			for( T value : enumValues ) {
				if( value.name( ).equalsIgnoreCase( name ) ) {
					return value;
				}
			}
			Config.log.error(
				"Invalid enum value '{}' in config (category:{}, option:{}) - falling back to default value '{}'",
				name, name( ), key, defaultValue.name( ).toLowerCase( )
			);
			return defaultValue;
		}
		
		< T extends Enum< T > > Property cprop( String key, T defaultValue, String comment )
		{
			//noinspection unchecked
			T[] enumValues = ((Class< T >) defaultValue.getClass( )).getEnumConstants( );
			
			String   defaultId   = defaultValue.name( ).toLowerCase( );
			String[] validValues = new String[ enumValues.length ];
			for( int i = 0; i < validValues.length; i++ ) {
				validValues[ i ] = enumValues[ i ].name( ).toLowerCase( );
			}
			comment = amendComment( comment, "Enum", defaultId, validValues );
			return Config.configLoading.get( name( ), key, defaultId, comment );
		}
		
		IBlockState prop( String key, IBlockState defaultValue, String comment )
		{
			String   target = cprop( key, defaultValue, comment ).getString( );
			String[] pair   = target.split( " ", 2 );
			
			IBlockState block = TargetBlock.getStringAsBlock( pair[ 0 ] );
			if( pair.length > 1 ) {
				//noinspection deprecation Block#getStateFromMeta(int) will be removed in the future. Ignore this for now.
				return block.getBlock( ).getStateFromMeta( Integer.parseInt( pair[ 1 ].trim( ) ) );
			}
			return block;
		}
		
		Property cprop( String key, IBlockState defaultValue, String comment )
		{
			String defaultId = Block.REGISTRY.getNameForObject( defaultValue.getBlock( ) ).toString( )
			                   + " " + defaultValue.getBlock( ).getMetaFromState( defaultValue );
			comment = amendComment( comment, "Block", defaultId, "mod_id:block_id, mod_id:block_id meta" );
			return Config.configLoading.get( name( ), key, defaultId, comment );
		}
		
		HashSet< TargetBlock > prop( String key, Block[] defaultValues, String comment )
		{
			TargetBlock[] wrappedDefaultValues = new TargetBlock[ defaultValues.length ];
			for( int i = 0; i < wrappedDefaultValues.length; i++ ) {
				wrappedDefaultValues[ i ] = new TargetBlock( defaultValues[ i ] );
			}
			return prop( key, wrappedDefaultValues, comment );
		}
		
		HashSet< TargetBlock > prop( String key, TargetBlock[] defaultValues, String comment )
		{
			return TargetBlock.newBlockSet( cprop( key, defaultValues, comment ).getStringList( ) );
		}
		
		Property cprop( String key, TargetBlock[] defaultValues, String comment )
		{
			String[] defaultIds = new String[ defaultValues.length ];
			for( int i = 0; i < defaultIds.length; i++ ) {
				defaultIds[ i ] = Block.REGISTRY.getNameForObject( defaultValues[ i ].BLOCK ).toString( );
				if( defaultValues[ i ].BLOCK_DATA >= 0 ) {
					defaultIds[ i ] = defaultIds[ i ] + " " + defaultValues[ i ].BLOCK_DATA;
				}
			}
			comment = amendComment( comment, "Block_Array", defaultIds, "mod_id:block_id, mod_id:block_id meta, mod_id:*" );
			return Config.configLoading.get( name( ), key, defaultIds, comment );
		}
		
		EntityListConfig prop( String key, EntryEntity[] defaultValues, String comment )
		{
			return new EntityListConfig( cprop( key, defaultValues, comment ).getStringList( ) );
		}
		
		Property cprop( String key, EntryEntity[] defaultValues, String comment )
		{
			String[] defaultIds = new String[ defaultValues.length ];
			for( int i = 0; i < defaultIds.length; i++ ) {
				defaultIds[ i ] = defaultValues[ i ].toString( );
			}
			comment = amendComment( comment, "Entity_Array", defaultIds, "entity_id <extra_data>, ~entity_id <extra_data>" );
			return Config.configLoading.get( name( ), key, defaultIds, comment );
		}
		
		EnvironmentListConfig prop( String key, TargetEnvironment[] defaultValues, String comment )
		{
			return new EnvironmentListConfig( cprop( key, defaultValues, comment ).getStringList( ) );
		}
		
		Property cprop( String key, TargetEnvironment[] defaultValues, String comment )
		{
			String[] defaultIds = new String[ defaultValues.length ];
			for( int i = 0; i < defaultIds.length; i++ ) {
				defaultIds[ i ] = defaultValues[ i ].toString( );
			}
			comment = amendComment( comment, "Environment_Array", defaultIds, "biome/mod_id:biome_id=value, biome/mod_id:prefix*=value, dimension/dimension_id=value" );
			return Config.configLoading.get( name( ), key, defaultIds, comment );
		}
		
		boolean prop( String key, boolean defaultValue, String comment )
		{
			return cprop( key, defaultValue, comment ).getBoolean( );
		}
		
		Property cprop( String key, boolean defaultValue, String comment )
		{
			comment = amendComment( comment, "Boolean", defaultValue, new Object[] { true, false } );
			return Config.configLoading.get( name( ), key, defaultValue, comment );
		}
		
		boolean[] prop( String key, boolean[] defaultValues, String comment )
		{
			return cprop( key, defaultValues, comment ).getBooleanList( );
		}
		
		Property cprop( String key, boolean[] defaultValues, String comment )
		{
			comment = amendComment( comment, "Boolean_Array", ArrayUtils.toObject( defaultValues ), new Object[] { true, false } );
			return Config.configLoading.get( name( ), key, defaultValues, comment );
		}
		
		int prop( String key, int defaultValue, String comment )
		{
			return cprop( key, defaultValue, comment ).getInt( );
		}
		
		int prop( String key, int defaultValue, String comment, int... range )
		{
			return cprop( key, defaultValue, comment, range ).getInt( );
		}
		
		Property cprop( String key, int defaultValue, String comment )
		{
			return cprop( key, defaultValue, comment, defaultIntRange( ) );
		}
		
		Property cprop( String key, int defaultValue, String comment, int... range )
		{
			comment = amendComment( comment, "Integer", defaultValue, range[ 0 ], range[ 1 ] );
			return Config.configLoading.get( name( ), key, defaultValue, comment, range[ 0 ], range[ 1 ] );
		}
		
		int[] prop( String key, int[] defaultValues, String comment )
		{
			return cprop( key, defaultValues, comment ).getIntList( );
		}
		
		int[] prop( String key, int[] defaultValues, String comment, int... range )
		{
			return cprop( key, defaultValues, comment, range ).getIntList( );
		}
		
		Property cprop( String key, int[] defaultValues, String comment )
		{
			return cprop( key, defaultValues, comment, defaultIntRange( ) );
		}
		
		Property cprop( String key, int[] defaultValues, String comment, int... range )
		{
			comment = amendComment( comment, "Integer_Array", ArrayUtils.toObject( defaultValues ), range[ 0 ], range[ 1 ] );
			return Config.configLoading.get( name( ), key, defaultValues, comment, range[ 0 ], range[ 1 ] );
		}
		
		float prop( String key, float defaultValue, String comment )
		{
			return (float) cprop( key, defaultValue, comment ).getDouble( );
		}
		
		float prop( String key, float defaultValue, String comment, float... range )
		{
			return (float) cprop( key, defaultValue, comment, range ).getDouble( );
		}
		
		Property cprop( String key, float defaultValue, String comment )
		{
			return cprop( key, defaultValue, comment, defaultFltRange( ) );
		}
		
		Property cprop( String key, float defaultValue, String comment, float... range )
		{
			comment = amendComment( comment, "Float", defaultValue, range[ 0 ], range[ 1 ] );
			return Config.configLoading.get( name( ), key, prettyFloatToDouble( defaultValue ), comment, prettyFloatToDouble( range[ 0 ] ), prettyFloatToDouble( range[ 1 ] ) );
		}
		
		double prop( String key, double defaultValue, String comment )
		{
			return cprop( key, defaultValue, comment ).getDouble( );
		}
		
		double prop( String key, double defaultValue, String comment, double... range )
		{
			return cprop( key, defaultValue, comment, range ).getDouble( );
		}
		
		Property cprop( String key, double defaultValue, String comment )
		{
			return cprop( key, defaultValue, comment, defaultDblRange( ) );
		}
		
		Property cprop( String key, double defaultValue, String comment, double... range )
		{
			comment = amendComment( comment, "Double", defaultValue, range[ 0 ], range[ 1 ] );
			return Config.configLoading.get( name( ), key, defaultValue, comment, range[ 0 ], range[ 1 ] );
		}
		
		double[] prop( String key, double[] defaultValues, String comment )
		{
			return cprop( key, defaultValues, comment ).getDoubleList( );
		}
		
		double[] prop( String key, double[] defaultValues, String comment, double... range )
		{
			return cprop( key, defaultValues, comment, range ).getDoubleList( );
		}
		
		Property cprop( String key, double[] defaultValues, String comment )
		{
			return cprop( key, defaultValues, comment, defaultDblRange( ) );
		}
		
		Property cprop( String key, double[] defaultValues, String comment, double... range )
		{
			comment = amendComment( comment, "Double_Array", ArrayUtils.toObject( defaultValues ), range[ 0 ], range[ 1 ] );
			return Config.configLoading.get( name( ), key, defaultValues, comment, range[ 0 ], range[ 1 ] );
		}
		
		String prop( String key, String defaultValue, String comment, String valueDescription )
		{
			return cprop( key, defaultValue, comment, valueDescription ).getString( );
		}
		
		String prop( String key, String defaultValue, String comment, String... validValues )
		{
			return cprop( key, defaultValue, comment, validValues ).getString( );
		}
		
		Property cprop( String key, String defaultValue, String comment, String valueDescription )
		{
			comment = amendComment( comment, "String", defaultValue, valueDescription );
			return Config.configLoading.get( name( ), key, defaultValue, comment, new String[ 0 ] );
		}
		
		Property cprop( String key, String defaultValue, String comment, String... validValues )
		{
			comment = amendComment( comment, "String", defaultValue, validValues );
			return Config.configLoading.get( name( ), key, defaultValue, comment, validValues );
		}
		
		private
		String amendComment( String comment, String type, Object[] defaultValues, String description )
		{
			return amendComment( comment, type, "{ " + toReadable( defaultValues ) + " }", description );
		}
		
		private
		String amendComment( String comment, String type, Object[] defaultValues, Object min, Object max )
		{
			return amendComment( comment, type, "{ " + toReadable( defaultValues ) + " }", min, max );
		}
		
		private
		String amendComment( String comment, String type, Object[] defaultValues, Object[] validValues )
		{
			return amendComment( comment, type, "{ " + toReadable( defaultValues ) + " }", validValues );
		}
		
		private
		String amendComment( String comment, String type, Object defaultValue, String description )
		{
			return comment + "\n   >> " + type + ":[ " + "Value={ " + description + " }, Default=" + defaultValue + " ]";
		}
		
		private
		String amendComment( String comment, String type, Object defaultValue, Object min, Object max )
		{
			return comment + "\n   >> " + type + ":[ " + "Range={ " + min + ", " + max + " }, Default=" + defaultValue + " ]";
		}
		
		private
		String amendComment( String comment, String type, Object defaultValue, Object[] validValues )
		{
			if( validValues.length < 2 )
				throw new IllegalArgumentException( "Attempted to create config with no options!" );
			
			return comment + "\n   >> " + type + ":[ " + "Valid_Values={ " + toReadable( validValues ) + " }, Default=" + defaultValue + " ]";
		}
		
		private
		double prettyFloatToDouble( float f )
		{
			return Double.parseDouble( Float.toString( f ) );
		}
		
		private
		String toReadable( Object[] array )
		{
			if( array.length <= 0 )
				return "";
			
			StringBuilder commentBuilder = new StringBuilder( );
			for( Object value : array ) {
				commentBuilder.append( value ).append( ", " );
			}
			return commentBuilder.substring( 0, commentBuilder.length( ) - 2 );
		}
	}
}
