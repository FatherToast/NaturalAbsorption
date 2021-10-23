package fathertoast.naturalabsorption.common.config;

import fathertoast.naturalabsorption.common.config.field.*;
import fathertoast.naturalabsorption.common.config.file.ToastConfigSpec;

import java.io.File;

public class MainConfig extends Config.AbstractConfig {
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    MainConfig( File dir, String fileName ) {
        super( dir, fileName,
                "This config contains options that apply to the mod as a whole, including some master disable",
                "toggles for convenience."
        );
        
        GENERAL = new General( SPEC );
    }
    
    public static class General extends Config.AbstractCategory {
        
        public final IntField updateTime;
        
        public final BooleanField disableRegenGameRule;
        
        public final BooleanField disableAbsorptionFeatures;
        public final BooleanField disableHealthFeatures;
        //public final BooleanField disableHungerFeatures;
        
        General( ToastConfigSpec parent ) {
            super( parent, "general",
                    "Options for customizing the mod as a whole." );
            
            updateTime = SPEC.define( new IntField( "update_time", 5, IntField.Range.POSITIVE,
                    "The number of ticks between this mod's logic/recovery updates (20 ticks = 1 second)." ) );
            
            SPEC.newLine();
            
            disableRegenGameRule = SPEC.define( new BooleanField( "disable_regen_game_rule", true,
                    "When set to true, this mod will constantly set the vanilla regeneration game rule",
                    "\"naturalRegeneration\" to \"false\" to disable other sources of health (red hearts) regeneration." ) );
            
            SPEC.newLine();
            
            disableAbsorptionFeatures = SPEC.define( new BooleanField( "disable_absorption_features", false,
                    "If set to 'true', disables all features in this mod related to absorption (yellow hearts)." ) );
            disableHealthFeatures = SPEC.define( new BooleanField( "disable_health_features", false,
                    "If set to 'true', disables all features in this mod related to health (red hearts)." ) );
        }
    }
}