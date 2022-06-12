package fathertoast.naturalabsorption.common.util;

import fathertoast.naturalabsorption.common.compat.tc.NAModifiers;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.register.NAEnchantments;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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
    
    // Compat
    private static final String ED_SUFFIX = ".desc";
    public static final String ED_ABSORPTION_INFO = NAEnchantments.ABSORPTION_ENCHANTMENT.get().getDescriptionId() + ED_SUFFIX;
    public static final String TC_ARMOR_ABSORPTION = NAModifiers.ARMOR_ABSORPTION.get().getTranslationKey() + ".tooltip.info";
    
    //---------------- FORMAT METHODS -------------------
    
    public static ITextComponent translate( String key, Object... args ) { return new TranslationTextComponent( key, args ); }
    
    public static String prettyToString( float value ) {
        return Math.round( value ) == value ? Integer.toString( Math.round( value ) ) : Float.toString( Math.round( value * 100.0F ) / 100.0F );
    }
    
    //---------------- COOL SYMBOLS -------------------
    
    /** The plus or minus symbol (+/-). */
    public static final String PLUS_OR_MINUS = "\u00b1";
    /** The less than or equal to symbol (<=). */
    public static final String LESS_OR_EQUAL = "\u2264";
    /** The greater than or equal to symbol (>=). */
    public static final String GREATER_OR_EQUAL = "\u2265";
    
    
    //------------ THE OBJECT SUPPLIER SUPPLIER --------------
    
    /** A supplier that returns a supplier that creates a new object. */
    public static final Supplier<Supplier<?>> OBJECT_SUPPLIER_SUPPLIER = () -> Object::new;
}