package fathertoast.naturalabsorption.common.config;

import fathertoast.naturalabsorption.common.config.field.*;
import fathertoast.naturalabsorption.common.config.file.ToastConfigSpec;

import java.io.File;

public class HealthConfig extends Config.AbstractConfig {
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    HealthConfig( File dir, String fileName ) {
        super( dir, fileName,
                "This config contains most options for features that apply to health (red hearts).",
                "Does NOT contain any armor or enchantment options - see the 'armor_and_enchant' config for those.",
                "Also contains hunger options related specifically to health recovery and healing from food."
        );
        
        GENERAL = new General( SPEC );
    }
    
    public static class General extends Config.AbstractCategory {
        
        public final DoubleField respawnAmount;
        
        public final DoubleField recoveryMax;
        
        public final IntField recoveryDelay;
        public final DoubleField recoveryRate;
        
        public final IntField recoveryHungerRequired;
        public final DoubleField recoveryHungerCost;
        
        public final DoubleField foodHealingMax;
        
        public final DoubleField foodHealingPerHunger;
        public final DoubleField foodHealingPerSaturation;
        
        General( ToastConfigSpec parent ) {
            super( parent, "health",
                    "Options that apply to health (red hearts).",
                    "", "Note: All health amounts are in half-hearts." );
            
            respawnAmount = SPEC.define( new DoubleField( "respawn_amount", 6.0, DoubleField.Range.NON_NEGATIVE,
                    "Players will respawn with up to this much health, limited by their personal max health.",
                    "Set this to 0 to leave respawn health unchanged." ) );
            
            SPEC.newLine();
            
            recoveryMax = SPEC.define( new DoubleField( "recovery.maximum", 6.0, DoubleField.Range.ANY,
                    "The maximum health a player may recover to from this mod's regeneration.",
                    "This limit is ignored by potion effects. If this is set less than 0, the limit is disabled." ) );
            
            SPEC.newLine();
            
            recoveryDelay = SPEC.define( new IntField( "recovery.delay", 40, IntField.Range.TOKEN_NEGATIVE,
                    "The amount of time (in ticks) a player must go without taking damage before their health begins",
                    "to recover (20 ticks = 1 second). If this is set less than 0, players will not naturally recover lost health",
                    "from this mod." ) );
            recoveryRate = SPEC.define( new ScaledDoubleField.Rate( "recovery.rate", 0.25, DoubleField.Range.NON_NEGATIVE,
                    "The amount of health regenerated each second while recovering (in half-hearts/second).",
                    "This ignores the vanilla health regeneration game rule." ) );
            
            SPEC.newLine();
            
            recoveryHungerRequired = SPEC.define( new IntField( "recovery.hunger_required", 6, IntField.Range.NON_NEGATIVE,
                    "Players need to have at least this much hunger (in half-drumsticks) to regenerate health." ) );
            recoveryHungerCost = SPEC.define( new ScaledDoubleField( "recovery.hunger_cost", 1.0, 4.0, DoubleField.Range.NON_NEGATIVE,
                    // Converted to exhaustion/half-heart
                    "The amount of hunger drained for each health regenerated (in drumsticks/heart).",
                    "Players can't lose over 1/2 drumstick per game tick or more than 5 drumsticks of hunger per recovery tick." ) );
            
            SPEC.newLine();
            
            foodHealingMax = SPEC.define( new DoubleField( "food_healing.maximum", -1.0, DoubleField.Range.ANY,
                    "The maximum health a player may heal to from eating food.",
                    "If this is set less than 0, the limit is disabled. Setting it to 0 effectively disables food healing." ) );
            
            SPEC.newLine();
            
            foodHealingPerHunger = SPEC.define( new DoubleField( "food_healing.per_hunger", 0.25, DoubleField.Range.NON_NEGATIVE,
                    "The amount of health recovered for each hunger and saturation (in hearts/drumstick) granted by",
                    "eaten food." ) );
            //TODO Not yet implemented
            //"eaten food. Health is only granted for the hunger/saturation actually restored (no gain from over-eating)." ) );
            foodHealingPerSaturation = SPEC.define( new DoubleField( "food_healing.per_saturation", 0.5, DoubleField.Range.NON_NEGATIVE ) );
        }
    }
}