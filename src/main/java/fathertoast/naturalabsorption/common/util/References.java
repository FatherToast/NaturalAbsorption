package fathertoast.naturalabsorption.common.util;

import java.util.function.Supplier;

public class References {

    //--------------- TRANSLATION KEYS ----------------
    
    public static final String ALREADY_MAX = "item.naturalabsorption.absorption_book.already_max";
    public static final String NOT_ENOUGH_LEVELS = "item.naturalabsorption.absorption_book.not_enough_levels";
    
    public static final String BOOK_CURRENT = "item.naturalabsorption.absorption_book.tooltip.current";
    public static final String BOOK_GAIN = "item.naturalabsorption.absorption_book.tooltip.gain";
    public static final String BOOK_MAX = "item.naturalabsorption.absorption_book.tooltip.max";
    public static final String BOOK_COST = "item.naturalabsorption.absorption_book.tooltip.cost";
    public static final String BOOK_CAN_USE = "item.naturalabsorption.absorption_book.tooltip.canuse";
    public static final String BOOK_NO_USE = "item.naturalabsorption.absorption_book.tooltip.nouse";


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