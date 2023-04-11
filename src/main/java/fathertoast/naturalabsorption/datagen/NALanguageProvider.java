package fathertoast.naturalabsorption.datagen;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.register.NAEnchantments;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.HashMap;

public class NALanguageProvider extends LanguageProvider {
    
    /** All supported translations. */
    public enum TranslationKey {
        ENGLISH( "en" ), SPANISH( "es" ), PORTUGUESE( "pt" ), FRENCH( "fr" ), ITALIAN( "it" ),
        GERMAN( "de" ), PIRATE( "en" );
        
        public final String code;
        
        TranslationKey( String id ) { code = id; }
    }
    
    /** This method provides helper tags to make linking translations up easier, and also enforces the correct array length. */
    private static String[] translations( String key, String en, String es, String pt, String fr, String it, String de, String pir ) {
        // Note that this must match up EXACTLY to the TranslationKey enum above
        String[] translation = { key, en, es, pt, fr, it, de, pir };
        
        // Fix the encoding to allow us to use accented characters in the translation string literals
        // Note: If a translation uses any non-ASCII characters, make sure they are all in this matrix! (case-sensitive)
        final String[][] utf8ToUnicode = {
                { "à", "\u00E0" }, { "á", "\u00E1" }, { "ã", "\u00E3" }, { "ä", "\u00E4" },
                { "ç", "\u00E7" },
                { "è", "\u00E8" }, { "é", "\u00E9" }, { "ê", "\u00EA" },
                { "í", "\u00ED" },
                { "ó", "\u00F3" }, { "õ", "\u00F5" }, { "ö", "\u00F6" },
                { "ù", "\u00F9" }, { "û", "\u00FB" }, { "ü", "\u00FC" },
                { "œ", "\u0153" }
        };
        for( int i = 1; i < translation.length; i++ ) {
            for( String[] fix : utf8ToUnicode )
                translation[i] = translation[i].replace( fix[0], fix[1] ); // Note: This is kinda dumb, but it works so idc
        }
        return translation;
    }
    
    /**
     * Matrix linking the actual translations to their lang key.
     * <p>
     * Each row of the matrix is one translation array.
     * In each translation array, the lang key is at index 0 and the translation for a particular
     * translation key is at index (translationKey.ordinal() + 1).
     *
     * @see #addTranslations()
     */
    @SuppressWarnings( "SpellCheckingInspection" )
    private static final String[][] TRANSLATIONS = {
            
            // Items/equipment names
            translations( NAItems.ABSORPTION_BOOK.get().getDescriptionId(), "Book of Absorption",
                    "Libro de absorción", "Livro de absorção", "Livre d'absorption",
                    "Libro di assorbimento", "Buch der Absorption", "Book of Magic Hearts" ),
            translations( NAItems.ABSORPTION_ABSORBING_BOOK.get().getDescriptionId(), "Absorption Absorbing Book",
                    "Absorción libro absorbente", "Livro absorção absorção", "Absorption livre absorbant",
                    "Assorbimento libro assorbente", "Absorption Absorptionsbuch", "Magic Heart Plundering Book" ),
            translations( NAEnchantments.ABSORPTION_ENCHANTMENT.get().getDescriptionId(), "Absorption",
                    "Absorción", "Absorção", "Absorption", "Assorbimento", "Absorption", "Heart o' Magic" ),
            
            // Food extra tooltip info
            translations( References.FOOD_HUNGER, "%s Hunger",
                    "%s Hambre", "%s Fome", "%s Faim",
                    "%s Fame", "%s Hunger", "%s Scurvy" ),
            translations( References.FOOD_SATURATION, "%s Saturation",
                    "%s Saturación", "%s Saturação", "%s Saturation",
                    "%s Sazietà", "%s Sättigung", "%s Full Belly" ),
            translations( References.FOOD_HEALTH, "%s Health",
                    "%s Salud", "%s Vida", "%s Vie",
                    "%s Salute", "%s Gesundheit", "%s Healin'" ),
            
            // Book tooltips
            translations( References.BOOK_GAIN, "When used:",
                    "Al usarse:", "Quando usado:", "Quand utilisé :",
                    "Quando usato:", "Auswirkungen:", "When us'd:" ),
            translations( References.BOOK_MAX, "%s Max Absorption",
                    "Máx absorción: %s", "Absorção máxima: %s", "%s Max Absorption",
                    "Assorbimento massimo: %s", "%s Max Absorption", "%s Max Magic Hearts" ),
            translations( References.BOOK_CAN_USE, "Right click to use",
                    "Haga clic derecho para usar", "Clique com o botão direito para usar", "Clic droit pour utiliser",
                    "Fare clic con il tasto destro per utilizzare", "Rechtsklick zum verwenden", "Right click to use" ),
            translations( References.BOOK_NO_USE, "You can't use this",
                    "No puedes usar esto", "Você não pode usar isso", "Tu ne peux pas utiliser ça",
                    "Non puoi usare questo", "Das kannst du nicht benutzen", "Ye can't be usin this" ),
            translations( References.ABSORPTION_BOOK_CURRENT, "Your absorption",
                    "Tu absorción", "Sua absorção", "Ton absorption",
                    "Il tuo assorbimento", "Deine absorption", "Yer magic hearts" ),
            translations( References.ABSORPTION_BOOK_COST, "Cost: %d levels",
                    "Coste: %d niveles", "Custo: %d níveis", "Coût: %d niveaux",
                    "Costo: %d livelli", "Levelkosten: %d", "Cost: %d levels" ),
            translations( References.SPONGE_BOOK_REFUND, "Refund: %d levels",
                    "Reembolso: %d niveles", "Reembolso: %d níveis", "Remboursement: %d niveaux",
                    "Rimborso: %d livelli", "Level-rückerstattung: %d", "Plunder: % levels" ),
            
            // Feedback messages
            translations( References.ALREADY_MAX, "Cannot upgrade your natural absorption any higher",
                    "No puedes mejorar tu absorción natural más", "Não é possível aumentar sua absorção natural",
                    "Tu ne peux pas améliorer ton absorption naturelle plus haut", "Non puoi migliorare il tuo assorbimento naturale più in alto",
                    "Kann ihre natürliche absorption nicht höher verbessern", "Ye can't hoist yer magic hearts any higher" ),
            translations( References.NOT_ENOUGH_LEVELS, "Requires at least %s levels to use",
                    "Requiere al menos %s niveles para usar", "Requer pelo menos %s níveis para usar",
                    "Nécessite au moins %s niveaux à utiliser", "Richiede almeno %s livelli per essere utilizzato",
                    "Benötigt mindestens %s levels zu verwenden", "Ye need at least %s levels to be usin this" ),
            translations( References.NOT_ENOUGH_ABSORPTION, "You have no natural absorption to convert",
                    "No tienes absorción natural para convertir", "Você não tem absorção natural para converter",
                    "Tu n'as pas d'absorption naturelle à convertir", "Non hai assorbimento naturale da convertire",
                    "Sie haben keine natürliche absorption zu konvertieren", "Ye don't be havin any magic hearts to be doin that" ),

            // Commands
            translations( References.CMD_CHANGE_ABSORPTION_SINGLE, "Changed max absorption for %s",
                    "Absorción máx modificada para %s", "Absorção máxima alterada para %s",
                    "Absorption max modifiée pour %s", "Assorbimento massimo modificato per %s",
                    "Max absorption für %s geändert", "Changed max magic hearts for %s" ),
            translations( References.CMD_CHANGE_ABSORPTION_MULTIPLE, "Changed max absorption for %s players",
                    "Absorción máx modificada para %s jugadores", "Absorção máxima alterada para %s jogadores",
                    "Absorption max modifiée pour %s joueurs", "Assorbimento massimo modificato per %s giocatori",
                    "Max absorption für %s Spieler geändert", "Changed max magic hearts for %s sailors" ),

            // Compat Features
            translations( References.ED_ABSORPTION_INFO, "Increases your maximum absorption.",
                    "Aumenta su máxima absorción.", "Aumenta sua absorção máxima.",
                    "Augmente ton absorption maximale.", "Aumenta il massimo assorbimento.",
                    "Erhöht ihre maximale absorption.", "Hoists yer magic hearts." ),
            translations( References.TC_ARMOR_ABSORPTION, "Absorption",
                    "Absorción", "Absorção", "Absorption", "Assorbimento", "Absorption", "Heart o' Magic" ),
            translations( References.TC_ARMOR_ABSORPTION_TOOLTIP, "+%s Max Absorption",
                    "Máx absorción: +%s", "Absorção máxima: +%s", "+%s Max Absorption",
                    "Assorbimento massimo: +%s", "+%s Max Absorption", "+%s Max Magic Hearts" ),
            translations( References.TC_ARMOR_ABSORPTION_FLAVOR, "Heart of gold",
                    "Corazón de oro", "Coração de ouro", "Cœur d'or",
                    "Cuore d'oro", "Herz aus gold", "Heart o' gold" ),
            translations( References.TC_ARMOR_ABSORPTION_DESCRIPTION, "Generates additional absorption hearts",
                    "Genera corazones de absorción adicionales", "Gera corações de absorção adicionais",
                    "Génère des cœurs d'absorption supplémentaires", "Genera cuori di assorbimento aggiuntivi",
                    "Erzeugt zusätzliche absorptionsherzen", "Hoists yer magic hearts" )
    };
    
    /** Maps which translation key each lang code uses, allowing multiple lang codes to use the same translations. */
    public static final HashMap<String, TranslationKey> LANG_CODE_MAP = new HashMap<>();
    
    static {
        // Assign all specific locales to the translation we want to use
        mapAll( TranslationKey.ENGLISH, "us" ); // We can ignore other English locales, en_us is the fallback for all languages
        mapAll( TranslationKey.SPANISH, "es", "ar", "cl", "ec", "mx", "uy", "ve" );
        mapAll( TranslationKey.PORTUGUESE, "pt", "br" );
        mapAll( TranslationKey.FRENCH, "fr", "ca" );
        mapAll( TranslationKey.ITALIAN, "it" );
        mapAll( TranslationKey.GERMAN, "de", "at", "ch" );
        mapAll( TranslationKey.PIRATE, "pt" );
        
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
    
    /** Maps any number of locale codes to a single translation. */
    private static void mapAll( TranslationKey translation, String... locales ) {
        for( String locale : locales ) {
            LANG_CODE_MAP.put( translation.code + "_" + locale, translation );
        }
    }
    
    /** The translation key to use for this locale. */
    private final TranslationKey translationKey;
    
    /** Creates a language provider for a specific locale. This correlates to exactly one .json file. */
    public NALanguageProvider( DataGenerator gen, String locale, TranslationKey translateKey ) {
        super( gen, NaturalAbsorption.MOD_ID, locale );
        translationKey = translateKey;
    }
    
    /**
     * Build the .json file for this locale (based solely on its translation key).
     *
     * @see NALanguageProvider#TRANSLATIONS
     */
    @Override
    protected void addTranslations() {
        final int k = translationKey.ordinal() + 1;
        for( String[] translationArray : TRANSLATIONS ) {
            add( translationArray[0], translationArray[k] );
        }
    }
}