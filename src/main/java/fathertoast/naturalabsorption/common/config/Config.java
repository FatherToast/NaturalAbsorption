package fathertoast.naturalabsorption.common.config;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;

/**
 * The initial loading for this is done during the common setup event.
 */
public class Config {

    private static final ConfigManager MANAGER = ConfigManager.create( "NaturalAbsorption", NaturalAbsorption.MOD_ID );
    
    public static final MainConfig MAIN = new MainConfig( MANAGER, "main" );
    public static final AbsorptionConfig ABSORPTION = new AbsorptionConfig( MANAGER, "absorption" );
    public static final EquipmentConfig EQUIPMENT = new EquipmentConfig( MANAGER, "enchant_and_armor" );
    public static final HealthConfig HEALTH = new HealthConfig( MANAGER, "health" );
    public static final CompatConfig COMPAT = new CompatConfig( MANAGER, "compat");

    /** Performs initial loading of all configs in this mod. */
    public static void initialize() {
        MAIN.SPEC.initialize();
        ABSORPTION.SPEC.initialize();
        EQUIPMENT.SPEC.initialize();
        HEALTH.SPEC.initialize();
        COMPAT.SPEC.initialize();
    }
}