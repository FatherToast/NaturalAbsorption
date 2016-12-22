package toast.naturalAbsorption;

import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import toast.naturalAbsorption.network.MessageSyncShield;

@Mod(modid = ModNaturalAbsorption.MODID, name = "Natural Absorption", version = ModNaturalAbsorption.VERSION)
public class ModNaturalAbsorption {
	/* TO DO *\
	 * Way to see current and max shield caps
	\* ** ** */

	// This mod's id.
	public static final String MODID = "natural_absorption";
	// This mod's version.
	public static final String VERSION = "1.2.2";

    /** The sided proxy. This points to a "common" proxy if and only if we are on a dedicated
     * server. Otherwise, it points to a client proxy. */
    @SidedProxy(clientSide = "toast.naturalAbsorption.client.ClientProxy", serverSide = "toast.naturalAbsorption.CommonProxy")
    public static CommonProxy proxy;
    /** The network channel for this mod. */
    public static SimpleNetworkWrapper CHANNEL;

	// The random number generator for this mod.
	public static final Random random = new Random();
	// The upgrade book for this mod.
	public static Item ABSORB_BOOK;
	// The enchantment for this mod.
	public static Enchantment ABSORB_ENCHANT;

	// Called before initialization. Loads the properties/configurations.
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Properties.load(new Configuration(event.getSuggestedConfigurationFile()));
		ModNaturalAbsorption.logDebug("Loading in debug mode!");

        int id = 0;
        ModNaturalAbsorption.CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("NA|CH1");
        if (event.getSide() == Side.CLIENT) {
            ModNaturalAbsorption.CHANNEL.registerMessage(MessageSyncShield.Handler.class, MessageSyncShield.class, id++, Side.CLIENT);
        }

		ModNaturalAbsorption.ABSORB_BOOK = GameRegistry.register(new Item().setRegistryName(ModNaturalAbsorption.MODID, "absorption_book")
			.setUnlocalizedName("absorption_book").setCreativeTab(CreativeTabs.MISC).setMaxStackSize(1));
	}

	// Called during initialization. Registers entities, mob spawns, and renderers.
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		this.registerEnchantments();

		new ShieldManager();
		new EventHandler();

		this.registerRecipes();
		ModNaturalAbsorption.proxy.registerRenderers();
	}

	// Registers the enchantments in this mod.
	private void registerEnchantments() {
		if (Properties.get().ENCHANT.ID > 0) {
			if (Enchantment.getEnchantmentByID(Properties.get().ENCHANT.ID) != null) {
				ModNaturalAbsorption.exception("Enchantment id [" + Properties.get().ENCHANT.ID + "] is already taken by "
					+ Enchantment.REGISTRY.getNameForObject(Enchantment.getEnchantmentByID(Properties.get().ENCHANT.ID))
					+ "! Please choose another id or disable the absorption enchantment.");
			}

			final Enchantment.Rarity rarity;
			if ("COMMON".equalsIgnoreCase(Properties.get().ENCHANT.RARITY)) {
				rarity = Enchantment.Rarity.COMMON;
			}
			else if ("UNCOMMON".equalsIgnoreCase(Properties.get().ENCHANT.RARITY)) {
				rarity = Enchantment.Rarity.UNCOMMON;
			}
			else if ("RARE".equalsIgnoreCase(Properties.get().ENCHANT.RARITY)) {
				rarity = Enchantment.Rarity.RARE;
			}
			else if ("VERY_RARE".equalsIgnoreCase(Properties.get().ENCHANT.RARITY)) {
				rarity = Enchantment.Rarity.VERY_RARE;
			}
			else {
				ModNaturalAbsorption.logWarning("Unrecognized enchantment rarity (" + Properties.get().ENCHANT.RARITY + "). Defaulting to RARE.");
				rarity = Enchantment.Rarity.RARE;
			}

			final EnumEnchantmentType type;
			if ("ANY".equalsIgnoreCase(Properties.get().ENCHANT.SLOT)) {
				type = EnumEnchantmentType.ARMOR;
			}
			else if ("HEAD".equalsIgnoreCase(Properties.get().ENCHANT.SLOT)) {
				type = EnumEnchantmentType.ARMOR_HEAD;
			}
			else if ("CHEST".equalsIgnoreCase(Properties.get().ENCHANT.SLOT)) {
				type = EnumEnchantmentType.ARMOR_CHEST;
			}
			else if ("LEGS".equalsIgnoreCase(Properties.get().ENCHANT.SLOT)) {
				type = EnumEnchantmentType.ARMOR_LEGS;
			}
			else if ("FEET".equalsIgnoreCase(Properties.get().ENCHANT.SLOT)) {
				type = EnumEnchantmentType.ARMOR_FEET;
			}
			else {
				ModNaturalAbsorption.logWarning("Unrecognized enchantment slot (" + Properties.get().ENCHANT.SLOT + "). Defaulting to ANY.");
				type = EnumEnchantmentType.ARMOR;
			}

			EntityEquipmentSlot[] allArmorSlots = { EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };
			ModNaturalAbsorption.ABSORB_ENCHANT = new EnchantmentAbsorption(rarity, type, allArmorSlots);
			Enchantment.REGISTRY.register(Properties.get().ENCHANT.ID,
				new ResourceLocation(ModNaturalAbsorption.MODID, "absorption"), ModNaturalAbsorption.ABSORB_ENCHANT);
		}
	}

	// Registers the crafting and smelting recipes in this mod.
	private void registerRecipes() {
		if (Properties.get().UPGRADES.RECIPE > 0 && Properties.get().UPGRADES.RECIPE <= 3) {
			Item recipeItem = Item.getByNameOrId(Properties.get().UPGRADES.RECIPE_ITEM);
			if (recipeItem != null) {
				// Output
				ItemStack absorptionBook = new ItemStack(ModNaturalAbsorption.ABSORB_BOOK);
				// Input
				ItemStack item = new ItemStack(recipeItem, 1, Properties.get().UPGRADES.RECIPE_ITEM_DAMAGE);
				ItemStack book = Properties.get().UPGRADES.RECIPE_ALT ? new ItemStack(Items.WRITABLE_BOOK) : new ItemStack(Items.BOOK);

				switch (Properties.get().UPGRADES.RECIPE) {
					case 1:
						GameRegistry.addShapelessRecipe(absorptionBook, new Object[] { book, item });
						break;
					case 2:
						GameRegistry.addRecipe(absorptionBook, new Object[] {
							" $ ",
							"$#$",
							" $ ", Character.valueOf('#'), book, Character.valueOf('$'), item });
						break;
					case 3:
						GameRegistry.addRecipe(absorptionBook, new Object[] {
							"$$$",
							"$#$",
							"$$$", Character.valueOf('#'), book, Character.valueOf('$'), item });
						break;
				}
			}
		}
	}

	public static boolean debug() {
		return Properties.get().GENERAL.DEBUG;
	}

	// Prints the message to the console with this mod's name tag if debugging is enabled.
	public static void logDebug(String message) {
		if (ModNaturalAbsorption.debug()) ModNaturalAbsorption.log("(debug) " + message);
	}

	// Prints the message to the console with this mod's name tag.
	public static void log(String message) {
		System.out.println("[" + ModNaturalAbsorption.MODID + "] " + message);
	}

	// Prints the message to the console with this mod's name tag if debugging is enabled.
	public static void logWarning(String message) {
		ModNaturalAbsorption.log("[WARNING] " + message);
	}

	// Prints the message to the console with this mod's name tag if debugging is enabled.
	public static void logError(String message) {
		if (ModNaturalAbsorption.debug())
			throw new RuntimeException("[" + ModNaturalAbsorption.MODID + "] [ERROR] " + message);
		ModNaturalAbsorption.log("[ERROR] " + message);
	}

	// Throws a runtime exception with a message and this mod's name tag.
	public static void exception(String message) {
		throw new RuntimeException("[" + ModNaturalAbsorption.MODID + "] [FATAL ERROR] " + message);
	}
}
