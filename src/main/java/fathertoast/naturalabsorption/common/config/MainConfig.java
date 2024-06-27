package fathertoast.naturalabsorption.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.InjectionWrapperField;
import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.world.level.GameRules;

import java.io.File;

public class MainConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    MainConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options that apply to the mod as a whole, including some master disable",
                "toggles for convenience."
        );
        
        GENERAL = new General( this );
    }
    
    public static class General extends AbstractConfigCategory<MainConfig> {
        
        public final IntField updateTime;
        
        public final InjectionWrapperField<BooleanField> defaultGameRuleNoRegen;
        
        public final BooleanField foodExtraTooltipInfo;
        
        public final BooleanField disableAbsorptionFeatures;
        public final BooleanField disableHealthFeatures;
        //public final BooleanField disableHungerFeatures; // NOTE: If/when hunger features are added, also move food configs from health
        
        General( MainConfig parent ) {
            super( parent, "general",
                    "Options for customizing the mod as a whole." );
            
            updateTime = SPEC.define( new IntField( "update_time", 5, IntField.Range.POSITIVE,
                    "The number of ticks between this mod's logic/recovery updates (20 ticks = 1 second)." ) );
            
            SPEC.newLine();
            
            defaultGameRuleNoRegen = SPEC.define( new InjectionWrapperField<>(
                    new BooleanField( "default_regen_game_rule_disabled", true,
                            "When set to true, this mod will alter the vanilla regeneration game rule \"naturalRegeneration\" to",
                            "be \"false\" by default when creating new worlds.",
                            "Regardless of this config setting, you can still create a world with vanilla health regen ON or OFF",
                            "by using the Game Rules button on the new world options screen or by using commands in-game." ),
                    ( wrapped ) -> {
                        // Note, we are assuming the default is always true without this mod (ie, no other mod changes the default)
                        GameRules.GAME_RULE_TYPES.put( GameRules.RULE_NATURAL_REGENERATION,
                                GameRules.BooleanValue.create( !wrapped.get() ) );
                    } ) );
            
            SPEC.newLine();
            
            foodExtraTooltipInfo = SPEC.define( new BooleanField( "food.extra_tooltip_info", true,
                    "Set to true to display nutritional value on the tooltips of food items.",
                    "Lists hunger and saturation that can be restored from eating. (See health config for healing display.)" ) );
            
            SPEC.newLine();
            
            disableAbsorptionFeatures = SPEC.define( new BooleanField( "disable_absorption_features", false,
                    "If set to 'true', disables all features in this mod related to absorption (yellow hearts)." ) );
            disableHealthFeatures = SPEC.define( new BooleanField( "disable_health_features", false,
                    "If set to 'true', disables all features in this mod related to health (red hearts)." ) );
        }
    }
}