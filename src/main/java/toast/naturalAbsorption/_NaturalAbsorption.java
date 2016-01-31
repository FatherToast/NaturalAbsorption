package toast.naturalAbsorption;

import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = _NaturalAbsorption.MODID, name = "Natural Absorption", version = _NaturalAbsorption.VERSION)
public class _NaturalAbsorption {
    /* TO DO *\
     * Way to see current and max shield caps
    \* ** ** */

    // This mod's id.
    public static final String MODID = "NaturalAbsorption";
    // This mod's version.
    public static final String VERSION = "1.2.1";

    // If true, this mod starts up in debug mode.
    public static final boolean debug = false;
    // The random number generator for this mod.
    public static final Random random = new Random();
    // The enchantment for this mod.
    public static Enchantment absorbEnchant;

    // Called before initialization. Loads the properties/configurations.
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        _NaturalAbsorption.debugConsole("Loading in debug mode!");
        Properties.init(new Configuration(event.getSuggestedConfigurationFile()));
    }

    // Called during initialization. Registers entities, mob spawns, and renderers.
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        int enchantId = Properties.getInt(Properties.ENCHANT, "id");
        if (enchantId > 0 && enchantId < Enchantment.enchantmentsList.length) {
            int enchantWeight = Math.max(0, Properties.getInt(Properties.ENCHANT, "weight"));
            _NaturalAbsorption.absorbEnchant = new EnchantmentAbsorption(enchantId, enchantWeight);
        }
        new ShieldManager();
        new EventHandler();
        this.addRecipes();
    }

    // Registers the crafting and smelting recipes in this mod.
    private void addRecipes() {
        int recipe = Properties.getInt(Properties.UPGRADES, "recipe");
        if (recipe > 0 && recipe <= 3) {
            if (Properties.getBoolean(Properties.UPGRADES, "compatible_recipe")) {
                GameRegistry.addRecipe(new RecipeAbsorption(recipe));
            }
            else {
                ItemStack absorptionBook = new ItemStack(Items.enchanted_book);
                EventHandler.setShieldItem(absorptionBook);

                ItemStack book = Properties.getBoolean(Properties.UPGRADES, "recipe_alt") ? new ItemStack(Items.writable_book) : new ItemStack(Items.book);
                String id = Properties.getString(Properties.UPGRADES, "recipe_item");
                Item recipeItem = (Item) Item.itemRegistry.getObject(id);

                // Compatibility with old numerical ids.
                if (recipeItem == null) {
                    try {
                        recipeItem = Item.getItemById(Integer.parseInt(id));
                        if (recipeItem != null) {
                            _NaturalAbsorption.console("[WARNING] Usage of numerical item id for recipe item! (" + id + ")");
                        }
                    }
                    catch (NumberFormatException ex) {
                        // Do nothing
                    }
                }

                if (recipeItem != null) {
                    ItemStack item = new ItemStack(recipeItem, 1, Properties.getInt(Properties.UPGRADES, "recipe_item_damage"));

                    switch (recipe) {
                        case 1:
                            GameRegistry.addShapelessRecipe(absorptionBook, new Object[] { book, item });
                            break;
                        case 2:
                            GameRegistry.addRecipe(absorptionBook, new Object[] { " $ ", "$#$", " $ ", Character.valueOf('#'), book, Character.valueOf('$'), item });
                            break;
                        case 3:
                            GameRegistry.addRecipe(absorptionBook, new Object[] { "$$$", "$#$", "$$$", Character.valueOf('#'), book, Character.valueOf('$'), item });
                            break;
                    }
                }
            }
        }
    }

    // Prints the message to the console with this mod's name tag.
    public static void console(String message) {
        System.out.println("[Natural Absorption] " + message);
    }

    // Prints the message to the console with this mod's name tag if debugging is enabled.
    public static void debugConsole(String message) {
        if (_NaturalAbsorption.debug) {
            System.out.println("[Natural Absorption] (debug) " + message);
        }
    }

    // Throws a runtime exception with a message and this mod's name tag.
    public static void exception(String message) {
        throw new RuntimeException("[Natural Absorption] " + message);
    }

    // Throws a runtime exception with a message and this mod's name tag if debugging is enabled.
    public static void debugException(String message) {
        if (_NaturalAbsorption.debug)
            throw new RuntimeException("[Natural Absorption] " + message);
    }
}