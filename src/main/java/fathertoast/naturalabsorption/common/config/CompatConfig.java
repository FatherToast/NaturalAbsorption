package fathertoast.naturalabsorption.common.config;

import fathertoast.naturalabsorption.common.config.field.DoubleField;
import fathertoast.naturalabsorption.common.config.file.ToastConfigSpec;

import java.io.File;

public class CompatConfig extends Config.AbstractConfig {
    
    public final CompatConfig.TinkersConstruct TC;
    
    /** Builds the config spec that should be used for this config. */
    CompatConfig( File dir, String fileName ) {
        super( dir, fileName,
                "This config contains config options for compatibility features for various supported mods."
        );
        TC = new CompatConfig.TinkersConstruct( SPEC );
    }

    public static class TinkersConstruct extends Config.AbstractCategory {
        
        //public final BooleanField modifierEnabled;
        public final DoubleField potencyPerLevel;
        
        TinkersConstruct( ToastConfigSpec parent ) {
            super( parent, "tconstruct", "Compatibility options for Tinkers Construct" );
            
            //modifierEnabled = SPEC.define( new BooleanField( "modifier.enabled", true,
            //        "Set this to false to disable the absorption modifier for armor that Natural Absorption adds to TC." ) );
            potencyPerLevel = SPEC.define( new DoubleField( "modifier.potency_per_level", 1.0, DoubleField.Range.NON_NEGATIVE,
                    "Maximum absorption gained for each level of the Absorption modifier for Tinkers Construct armor.",
                    "By default, this matches Revitalizing's scaling (max health), but costs one Book of Absorption per level." ) );
        }
    }
}