package fathertoast.naturalabsorption.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;

public class CompatConfig extends AbstractConfigFile {
    
    public final TinkersConstruct TC;
    
    /** Builds the config spec that should be used for this config. */
    CompatConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains config options for compatibility features for various supported mods."
        );
        TC = new TinkersConstruct( this );
    }

    public static class TinkersConstruct extends AbstractConfigCategory<CompatConfig> {
        
        //public final BooleanField modifierEnabled;
        public final DoubleField potencyPerLevel;
        
        TinkersConstruct( CompatConfig parent ) {
            super( parent, "tconstruct", "Compatibility options for Tinkers Construct" );
            
            //modifierEnabled = SPEC.define( new BooleanField( "modifier.enabled", true,
            //        "Set this to false to disable the absorption modifier for armor that Natural Absorption adds to TC." ) );
            potencyPerLevel = SPEC.define( new DoubleField( "modifier.potency_per_level", 1.0, DoubleField.Range.NON_NEGATIVE,
                    "Maximum absorption gained for each level of the Absorption modifier for Tinkers Construct armor.",
                    "By default, this matches Revitalizing's scaling (max health), but costs one Book of Absorption per level." ) );
        }
    }
}