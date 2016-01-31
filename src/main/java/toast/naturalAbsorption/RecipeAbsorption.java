package toast.naturalAbsorption;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeAbsorption implements IRecipe {

    // The recipe id to use.
    public final int RECIPE_ID;

    // The main recipe item.
    public final Item RECIPE_ITEM;
    // The main recipe item's damage value. 32767 will match anything.
    public final int RECIPE_DAMAGE;
    // The book required.
    public final Item BOOK_ITEM;

    public RecipeAbsorption(int recipe) {
        this.RECIPE_ID = recipe;

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

        this.RECIPE_ITEM = recipeItem;
        this.RECIPE_DAMAGE = Properties.getInt(Properties.UPGRADES, "recipe_item_damage");
        this.BOOK_ITEM = Properties.getBoolean(Properties.UPGRADES, "recipe_alt") ? Items.writable_book : Items.book;
    }

    /*
     * @see net.minecraft.item.crafting.IRecipe#matches(net.minecraft.inventory.InventoryCrafting, net.minecraft.world.World)
     */
    @Override
    public boolean matches(InventoryCrafting craftMatrix, World world) {
        if (this.RECIPE_ID < 1 || this.RECIPE_ID > 3)
            return false;
        switch (this.RECIPE_ID) {
            case 1:
                boolean book = false;
                boolean item = false;
                for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
                    ItemStack ingredient = craftMatrix.getStackInSlot(i);
                    if (ingredient != null) {
                        if (!book && ingredient.getItem() == this.BOOK_ITEM) {
                            book = true;
                        }
                        else if (!item && ingredient.getItem() == this.RECIPE_ITEM && (ingredient.getItemDamage() == this.RECIPE_DAMAGE || this.RECIPE_DAMAGE == 32767)) {
                            item = true;
                        }
                        else
                            return false;
                    }
                }
                if (!book || !item)
                    return false;
                break;
            case 2:
                for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
                    ItemStack ingredient = craftMatrix.getStackInSlot(i);
                    if (i == 4) {
                        if (ingredient == null || ingredient.getItem() != this.BOOK_ITEM)
                            return false;
                    }
                    else if (i % 2 == 1) {
                        if (ingredient == null || ingredient.getItem() != this.RECIPE_ITEM || ingredient.getItemDamage() != this.RECIPE_DAMAGE && this.RECIPE_DAMAGE != 32767)
                            return false;
                    }
                    else if (ingredient != null)
                        return false;
                }
                break;
            case 3:
                for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
                    ItemStack ingredient = craftMatrix.getStackInSlot(i);
                    if (i == 4) {
                        if (ingredient == null || ingredient.getItem() != this.BOOK_ITEM)
                            return false;
                    }
                    else {
                        if (ingredient == null || ingredient.getItem() != this.RECIPE_ITEM || ingredient.getItemDamage() != this.RECIPE_DAMAGE && this.RECIPE_DAMAGE != 32767)
                            return false;
                    }
                }
                break;
        }
        return true;
    }

    /*
     * @see net.minecraft.item.crafting.IRecipe#getCraftingResult(net.minecraft.inventory.InventoryCrafting)
     */
    @Override
    public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {
        ItemStack absorptionBook = new ItemStack(Items.enchanted_book);
        EventHandler.setShieldItem(absorptionBook);
        return absorptionBook;
    }

    /*
     * @see net.minecraft.item.crafting.IRecipe#getRecipeSize()
     */
    @Override
    public int getRecipeSize() {
        return 9;
    }

    /*
     * @see net.minecraft.item.crafting.IRecipe#getRecipeOutput()
     */
    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

}
