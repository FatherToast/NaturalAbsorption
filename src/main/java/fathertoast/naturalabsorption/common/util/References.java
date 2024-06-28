package fathertoast.naturalabsorption.common.util;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.register.NAEnchantments;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class References {
    
    //--------------- TRANSLATION KEYS ----------------
    
    private static final String ITEM_ROOT = "item." + NaturalAbsorption.MOD_ID + ".";
    
    public static final String ALREADY_MAX = ITEM_ROOT + "absorption_book.already_max";
    public static final String NOT_ENOUGH_LEVELS = ITEM_ROOT + "absorption_book.not_enough_levels";
    public static final String NOT_ENOUGH_ABSORPTION = ITEM_ROOT + "sponge_book.not_enough_absorption";
    
    public static final String FOOD_HUNGER = ITEM_ROOT + "food.tooltip.hunger";
    public static final String FOOD_SATURATION = ITEM_ROOT + "food.tooltip.saturation";
    public static final String FOOD_HEALTH = ITEM_ROOT + "food.tooltip.health";
    
    public static final String BOOK_GAIN = ITEM_ROOT + "book.tooltip.gain";
    public static final String BOOK_MAX = ITEM_ROOT + "book.tooltip.max";
    public static final String BOOK_CAN_USE = ITEM_ROOT + "book.tooltip.can_use";
    public static final String BOOK_NO_USE = ITEM_ROOT + "book.tooltip.no_use";
    public static final String ABSORPTION_BOOK_CURRENT = ITEM_ROOT + "absorption_book.tooltip.current";
    public static final String ABSORPTION_BOOK_COST = ITEM_ROOT + "absorption_book.tooltip.cost";
    public static final String SPONGE_BOOK_REFUND = ITEM_ROOT + "sponge_book.tooltip.refund";

    public static final String CMD_CHANGE_ABSORPTION_SINGLE = "commands.naturalabsorption.change_absorption.single.success";
    public static final String CMD_CHANGE_ABSORPTION_MULTIPLE = "commands.naturalabsorption.change_absorption.multiple.success";
    
    // Compat
    private static final String ED_SUFFIX = ".desc";
    public static final String ED_ABSORPTION_INFO = NAEnchantments.ABSORPTION_ENCHANTMENT.get().getDescriptionId() + ED_SUFFIX;
    // Lang key is derived below; commented out to avoid NoClassDefFoundError
    // public static final String TC_ARMOR_ABSORPTION = NAModifiers.ARMOR_ABSORPTION.get().getTranslationKey();
    public static final String TC_ARMOR_ABSORPTION = "modifier.naturalabsorption.armor_absorption";
    public static final String TC_ARMOR_ABSORPTION_TOOLTIP = TC_ARMOR_ABSORPTION + ".tooltip";
    public static final String TC_ARMOR_ABSORPTION_FLAVOR = TC_ARMOR_ABSORPTION + ".flavor";
    public static final String TC_ARMOR_ABSORPTION_DESCRIPTION = TC_ARMOR_ABSORPTION + ".description";
    
    //---------------- FORMAT METHODS -------------------
    
    public static Component translate(String key, Object... args ) { return Component.translatable( key, args ); }
    
    public static String prettyToString( float value ) {
        return Math.round( value ) == value ? Integer.toString( Math.round( value ) ) : Float.toString( Math.round( value * 100.0F ) / 100.0F );
    }
    
    
    //------------ THE OBJECT SUPPLIER SUPPLIER SUPPLIER --------------
    
    /** A supplier that returns a supplier that returns a supplier that creates a new object. */
    public static final Supplier<Supplier<Supplier<?>>> OBJECT_SUPPLIER_SUPPLIER_SUPPLIER = () -> () -> Object::new;
}