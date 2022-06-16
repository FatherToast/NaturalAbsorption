package fathertoast.naturalabsorption.common.config;

import fathertoast.naturalabsorption.client.ClientUtil;
import fathertoast.naturalabsorption.common.config.field.*;
import fathertoast.naturalabsorption.common.config.file.ToastConfigSpec;
import fathertoast.naturalabsorption.common.recipe.condition.BookRecipeCondition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.io.File;

public class AbsorptionConfig extends Config.AbstractConfig {
    
    public final General GENERAL;
    public final Natural NATURAL;
    
    /** Builds the config spec that should be used for this config. */
    AbsorptionConfig( File dir, String fileName ) {
        super( dir, fileName,
                "This config contains most options for features that apply to absorption (yellow hearts).",
                "Does NOT contain any armor or enchantment options - see the 'enchant_and_armor' config for those.",
                "Also contains hunger options related specifically to absorption recovery."
        );
        
        GENERAL = new General( SPEC );
        NATURAL = new Natural( SPEC );
    }
    
    public static class General extends Config.AbstractCategory {
        
        public final DoubleField globalMax;
        
        public final DoubleField respawnAmount;
        
        public final IntField recoveryDelay;
        public final DoubleField recoveryRate;
        
        public final IntField recoveryHungerRequired;
        public final DoubleField recoveryHungerCost;
        
        public final InjectionWrapperField<BooleanField> renderCapacityBackground;
        
        General( ToastConfigSpec parent ) {
            super( parent, "absorption",
                    "Options that apply to absorption (yellow hearts) from any source.",
                    "", "Note: All absorption amounts are in half-hearts." );
            
            globalMax = SPEC.define( new DoubleField( "global_maximum", -1.0, DoubleField.Range.ANY,
                    "The total maximum absorption a player may obtain from (almost) all sources combined.",
                    "This limit is ignored by potion effects. If this is set less than 0, the limit is disabled." ) );
            
            SPEC.newLine();
            
            respawnAmount = SPEC.define( new DoubleField( "respawn_amount", 0.0, DoubleField.Range.NON_NEGATIVE,
                    "Players will respawn with up to this much absorption, limited by their personal max absorption." ) );
            
            SPEC.newLine();
            
            recoveryDelay = SPEC.define( new IntField( "recovery.delay", 120, IntField.Range.TOKEN_NEGATIVE,
                    "The amount of time (in ticks) a player must go without taking damage before their absorption begins",
                    "to recover (20 ticks = 1 second). If this is set less than 0, players will not naturally recover lost absorption." ) );
            recoveryRate = SPEC.define( new ScaledDoubleField.Rate( "recovery.rate", 2.0, DoubleField.Range.NON_NEGATIVE,
                    "The amount of absorption regenerated each second while recovering (in half-hearts/second)." ) );
            
            SPEC.newLine();
            
            recoveryHungerRequired = SPEC.define( new IntField( "recovery.hunger_required", 0, IntField.Range.NON_NEGATIVE,
                    "Players need to have at least this much hunger (in half-drumsticks) to regenerate absorption." ) );
            recoveryHungerCost = SPEC.define( new ScaledDoubleField( "recovery.hunger_cost", 0.0, 4.0, DoubleField.Range.NON_NEGATIVE,
                    // Converted to exhaustion/half-heart
                    "The amount of hunger drained for each absorption regenerated (in drumsticks/heart).",
                    "Players can't lose over 1/2 drumstick per game tick or more than 5 drumsticks of hunger per recovery tick." ) );
            
            SPEC.newLine();
            
            renderCapacityBackground = SPEC.define( new InjectionWrapperField<>(
                    new BooleanField( "render_capacity_background", true,
                            "If true, the mod will render the empty heart background behind absorption hearts you are missing,",
                            "but can regenerate back. This may not work right if another mod changes heart bar rendering, or may override",
                            "other mods' heart rendering (for example, Mantle's heart stacker option)." ),
                    ( wrapped ) -> DistExecutor.safeRunWhenOn( Dist.CLIENT, () ->
                            ClientUtil.toggleAbsorptionBackgroundRender( wrapped.get() ) ) ) );
        }
    }
    
    public static class Natural extends Config.AbstractCategory {
        
        public final DoubleField startingAmount;
        public final DoubleField maximumAmount;
        
        public final DoubleField deathPenalty;
        public final DoubleField deathPenaltyLimit;
        
        public final DoubleField upgradeGain;
        
        public final DoubleField upgradeLevelCostBase;
        public final IntField upgradeLevelCostMax;
        public final DoubleField upgradeLevelCostPerPoint;
        
        public final EnumField<BookRecipeCondition.Type> upgradeBookRecipe;
        public final BooleanField upgradeBookExtraTooltipInfo;
        
        public final BooleanField spongeBookEnabled;
        public final DoubleField spongeBookLevelRefundMulti;
        public final BooleanField spongeBookBookRefund;
        
        // Insert here if a config for loot table injection for upgrade/sponge books is desired; see net.minecraftforge.event.LootTableLoadEvent
        
        Natural( ToastConfigSpec parent ) {
            super( parent, "natural_absorption",
                    "Options for natural absorption. Not to be confused with the Natural Absorption mod itself.",
                    "Natural absorption is one source of maximum absorption that is innate to the player - it does not",
                    "come from any items or potions/effects.",
                    "By default, each player starts with some natural absorption, gains more by crafting and consuming",
                    "Books of Absorption, and loses some upon death.",
                    "", "Note: All absorption amounts are in half-hearts." );
            
            startingAmount = SPEC.define( new DoubleField( "starting_absorption", 4.0, DoubleField.Range.NON_NEGATIVE,
                    "The amount of natural absorption a new player starts with." ) );
            maximumAmount = SPEC.define( new DoubleField( "max_absorption", 20.0, DoubleField.Range.NON_NEGATIVE,
                    "The maximum natural absorption a player may obtain from upgrades.",
                    "Does not include any other sources of max absorption (such as from potions or equipment)." ) );
            
            SPEC.newLine();
            
            deathPenalty = SPEC.define( new DoubleField( "death_penalty", 2.0, DoubleField.Range.NON_NEGATIVE,
                    "The amount of maximum absorption a player loses with each death. Will not drop below the death penalty limit." ) );
            deathPenaltyLimit = SPEC.define( new DoubleField( "death_penalty_limit", 10.0, DoubleField.Range.NON_NEGATIVE,
                    "A player will not drop below this much max absorption due to death penalty." ) );
            
            SPEC.newLine();
            
            upgradeGain = SPEC.define( new DoubleField( "upgrades.gain", 2.0, DoubleField.Range.NON_NEGATIVE,
                    "The amount of maximum natural absorption gained from each upgrade.",
                    "Set this to 0 to disable upgrades." ) );
            
            SPEC.newLine();
            
            upgradeLevelCostBase = SPEC.define( new DoubleField( "level_cost.base", 0.0, DoubleField.Range.ANY,
                    "The base number of levels required to use a Book of Absorption.",
                    "A negative value reduces the cost of the first upgrade(s).",
                    "The final level cost is rounded down to the nearest whole number and clamped between 0 and the cost limit." ) );
            upgradeLevelCostPerPoint = SPEC.define( new DoubleField( "level_cost.per_point", 2.5, DoubleField.Range.NON_NEGATIVE,
                    "The number of levels required to use a Book of Absorption for each point of natural absorption",
                    "the player already has." ) );
            upgradeLevelCostMax = SPEC.define( new IntField( "level_cost.limit", 30, IntField.Range.NON_NEGATIVE,
                    "The maximum number of levels that can be required to use a Book of Absorption.",
                    "Set this to 0 to disable level costs entirely." ) );
            
            SPEC.newLine();
            
            upgradeBookRecipe = SPEC.define( new EnumField<>( "upgrade_book.recipe", BookRecipeCondition.Type.CROSS,
                    "The recipe for making a Book of Absorption.",
                    "  none     - <no recipe>",
                    "  simple   - aB  (book + apple, shapeless)",
                    "  sandwich - aBa (book + 2 apples)",
                    "  cross    -  a  (book + 4 apples)",
                    "             aBa ",
                    "              a  ",
                    "  surround - aaa (book + 8 apples)",
                    "             aBa ",
                    "             aaa ",
                    "B = book & quill, a = golden apple" ) );
            upgradeBookExtraTooltipInfo = SPEC.define( new BooleanField( "upgrade_book.extra_tooltip_info", false,
                    "Set to true to display current and max natural absorption on the Book of Absorption tooltip.",
                    "Particularly helpful if you must disable the heart background rendering." ) );
            
            SPEC.newLine();
            
            spongeBookEnabled = SPEC.define( new BooleanField( "downgrade_book.enabled", true,
                    "If enabled, players can use the glorious Absorption Absorbing Book to convert some of their natural",
                    "absorption back into an absorption book. The natural absorption lost per use is exactly one upgrade." ) );
            spongeBookLevelRefundMulti = SPEC.define( new DoubleField( "downgrade_book.level_refund", 0.75, DoubleField.Range.PERCENT,
                    "The percentage of the experience levels refunded when using an Absorption Absorbing Book.",
                    "For example, a value of 0.5 (50%) will restore half of the levels consumed by a Book of Absorption,",
                    "rounded down to the nearest whole number." ) );
            spongeBookBookRefund = SPEC.define( new BooleanField( "downgrade_book.book_refund", true,
                    "If enabled, a Book of Absorption will be refunded when using an Absorption Absorbing Book." ) );
        }
    }
}