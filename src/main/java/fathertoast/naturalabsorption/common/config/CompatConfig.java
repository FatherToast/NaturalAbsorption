package fathertoast.naturalabsorption.common.config;

import fathertoast.naturalabsorption.common.config.field.BooleanField;
import fathertoast.naturalabsorption.common.config.file.ToastConfigSpec;

import java.io.File;

public class CompatConfig extends Config.AbstractConfig {

    public final CompatConfig.TinkersConstruct TC;

    /** Builds the config spec that should be used for this config. */
    CompatConfig(File dir, String fileName ) {
        super( dir, fileName,
                "This config contains config options for compatibility features for various supported mods."
        );
        TC = new CompatConfig.TinkersConstruct( SPEC );
    }

    public static class TinkersConstruct extends Config.AbstractCategory {

        public final BooleanField modifierEnabled;

        TinkersConstruct( ToastConfigSpec parent ) {
            super( parent, "tconstruct",
                    "Compatibility options for Tinkers Construct" );

            modifierEnabled = SPEC.define( new BooleanField( "modifierEnabled", true,
                    "Set this to false to disable the equipment absorption modifier that Natural Absorption adds when TC is installed." ) );
        }
    }
}
