package fathertoast.naturalabsorption.datagen;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.register.NAEnchantments;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.HashMap;

public class NALanguageProvider extends LanguageProvider {
    
    /** All supported languages. */
    public enum TranslationKey {
        ENGLISH( "en" ), SPANISH( "es" ), PORTUGUESE( "pt" ), FRENCH( "fr" ), ITALIAN( "it" ),
        GERMAN( "de" ), PIRATE( "en" );
        
        public final String code;
        
        TranslationKey( String id ) { code = id; }
    }
    
    /** This method provides helper tags to make linking translations up easier, and also enforces the correct array length. */
    private static String[] Translations( String key, String en, String es, String pt, String fr, String it, String de, String pir ) {
        // Note that this must match up EXACTLY to the TranslationKey enum above
        String[] translation = { key, en, es, pt, fr, it, de, pir };
        for( int i = 1; i < translation.length; i++ ) {
            //noinspection ConstantConditions This is a dumb thing to do, but for some reason it fixes the encoding
            translation[i] = translation[i]
                    .replace( "à", "\u00E0" ).replace( "á", "\u00E1" ).replace( "ã", "\u00E3" )
                    .replace( "ç", "\u00E7" ).replace( "é", "\u00E9" ).replace( "ê", "\u00EA" )
                    .replace( "í", "\u00ED" ).replace( "ó", "\u00F3" ).replace( "ö", "\u00F6" )
                    .replace( "ù", "\u00F9" ).replace( "û", "\u00FB" ).replace( "ü", "\u00FC" );
        }
        return translation;
    }
    
    /** Matrix linking the actual translations to their lang key. */
    @SuppressWarnings( "SpellCheckingInspection" )
    private static final String[][] TRANSLATIONS = {
            
            Translations( NAItems.ABSORPTION_BOOK.get().getDescriptionId(), "Book of Absorption",
                    "Libro de absorción", "Livro de absorção", "Livre d'absorption",
                    "Libro di assorbimento", "Buch der Absorption", "Book of Magic Hearts" ),
            Translations( NAItems.ABSORPTION_ABSORBING_BOOK.get().getDescriptionId(), "Absorption Absorbing Book",
                    "Absorción libro absorbente", "Livro absorção absorção", "Absorption livre absorbant",
                    "Assorbimento libro assorbente", "Absorption Absorptionsbuch", "Magic Heart Plundering Book" ),
            Translations( NAEnchantments.ABSORPTION_ENCHANTMENT.get().getDescriptionId(), "Absorption",
                    "Absorción", "Absorção", "Absorption", "Assorbimento", "Absorption", "Heart o' Magic" ),
            
            Translations( References.BOOK_GAIN, "When used:",
                    "Al usarse:", "Quando usado:", "Quand utilisé :",
                    "Quando usato:", "Auswirkungen:", "When us'd:" ),
            Translations( References.BOOK_MAX, "%s Max Absorption",
                    "Máx absorción: %s", "Absorção máxima: %s", "%s Max Absorption",
                    "Assorbimento massimo: %s", "%s Max Absorption", "%s Max Magic Hearts" ),
            Translations( References.BOOK_CAN_USE, "Right click to use",
                    "Haga clic derecho para usar", "Clique com o botão direito para usar", "Clic droit pour utiliser",
                    "Fare clic con il tasto destro per utilizzare", "Rechtsklick zum verwenden", "Right click to use" ),
            Translations( References.BOOK_NO_USE, "You can't use this",
                    "No puedes usar esto", "Você não pode usar isso", "Tu ne peux pas utiliser ça",
                    "Non puoi usare questo", "Das kannst du nicht benutzen", "Ye can't be usin this" ),
            Translations( References.ABSORPTION_BOOK_CURRENT, "Your absorption",
                    "Tu absorción", "Sua absorção", "Ton absorption",
                    "Il tuo assorbimento", "Deine absorption", "Yer magic hearts" ),
            Translations( References.ABSORPTION_BOOK_COST, "Cost: %d levels",
                    "Coste: %d niveles", "Custo: %d níveis", "Coût: %d niveaux",
                    "Costo: %d livelli", "Levelkosten: %d", "Cost: %d levels" ),
            Translations( References.SPONGE_BOOK_REFUND, "Refund: %d levels",
                    "Reembolso: %d niveles", "Reembolso: %d níveis", "Remboursement: %d niveaux",
                    "Rimborso: %d livelli", "Level-rückerstattung: %d", "Plunder: % levels" ),
            
            Translations( References.ALREADY_MAX, "Cannot upgrade your natural absorption any higher",
                    "No puedes mejorar tu absorción natural más", "Não é possível aumentar sua absorção natural",
                    "Tu ne peux pas améliorer ton absorption naturelle plus haut", "Non puoi migliorare il tuo assorbimento naturale più in alto",
                    "Kann ihre natürliche absorption nicht höher verbessern", "Ye can't hoist yer magic hearts any higher" ),
            Translations( References.NOT_ENOUGH_LEVELS, "Requires at least %s levels to use",
                    "Requiere al menos %s niveles para usar", "Requer pelo menos %s níveis para usar",
                    "Nécessite au moins %s niveaux à utiliser", "Richiede almeno %s livelli per essere utilizzato",
                    "Benötigt mindestens %s levels zu verwenden", "Ye need at least %s levels to be usin this" ),
            Translations( References.NOT_ENOUGH_ABSORPTION, "You have no natural absorption to convert",
                    "No tienes absorción natural para convertir", "Você não tem absorção natural para converter",
                    "Tu n'as pas d'absorption naturelle à convertir", "Non hai assorbimento naturale da convertire",
                    "Sie haben keine natürliche absorption zu konvertieren", "Ye don't be havin any magic hearts to be doin that" )
    };
    
    /** Maps which translation key each lang code uses, allowing multiple lang codes to use the same translations. */
    public static final HashMap<String, TranslationKey> LANG_CODE_MAP = new HashMap<>();
    
    static {
        // Assign all specific locales to the translation we want to use
        MapAll( TranslationKey.ENGLISH, "us" ); // We can ignore all other English locales, en_us is the fallback for all languages
        MapAll( TranslationKey.SPANISH, "es", "ar", "cl", "ec", "mx", "uy", "ve" );
        MapAll( TranslationKey.PORTUGUESE, "pt", "br" );
        MapAll( TranslationKey.FRENCH, "fr", "ca" );
        MapAll( TranslationKey.ITALIAN, "it" );
        MapAll( TranslationKey.GERMAN, "de", "at", "ch" );
        MapAll( TranslationKey.PIRATE, "pt" );
        
        // Make sure all supported languages are completely implemented
        NaturalAbsorption.LOG.info( "Starting translation key verification..." );
        for( TranslationKey key : TranslationKey.values() ) {
            if( !LANG_CODE_MAP.containsValue( key ) ) {
                NaturalAbsorption.LOG.error( "Translation key {} has no lang codes assigned!", key.name() );
            }
            final int k = key.ordinal() + 1;
            for( String[] translationArray : TRANSLATIONS ) {
                if( translationArray[k] == null || translationArray[k].equals( "" ) ) {
                    NaturalAbsorption.LOG.error( "Translation key {} is missing a translation for lang key \"{}\"!",
                            key.name(), translationArray[0] );
                }
            }
        }
        NaturalAbsorption.LOG.info( "Translation key verification complete!" );
    }
    
    private static void MapAll( TranslationKey translation, String... locales ) {
        for( String locale : locales ) {
            LANG_CODE_MAP.put( translation.code + "_" + locale, translation );
        }
    }
    
    private final TranslationKey translationKey;
    
    public NALanguageProvider( DataGenerator gen, String locale, TranslationKey translateKey ) {
        super( gen, NaturalAbsorption.MOD_ID, locale );
        translationKey = translateKey;
    }
    
    @Override
    protected void addTranslations() {
        final int k = translationKey.ordinal() + 1;
        for( String[] translationArray : TRANSLATIONS ) {
            add( translationArray[0], translationArray[k] );
        }
    }
}