package fathertoast.naturalabsorption.api;

import net.minecraft.world.entity.player.Player;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This interface allows mod developers to easily access and modify a player's absorption modifier
 * values using config-based logic and restrictions.
 */
@SuppressWarnings( "unused" )
@ParametersAreNonnullByDefault
public interface IAbsorptionAccessor {
    /**
     * @return The player's max absorption, from all sources combined. In other words, the actual limit on absorption recovery.
     */
    double getMaxAbsorption( Player player );
    
    /**
     * @return The player's max absorption not counting buffs, limited by the global max absorption config.
     */
    double getSteadyStateMaxAbsorption( Player player );
    
    /**
     * @return The player's max absorption granted by natural absorption.
     */
    double getNaturalAbsorption( Player player );
    
    /**
     * @return The player's natural absorption, ignoring all attribute modifiers.
     */
    double getBaseNaturalAbsorption( Player player );
    
    /**
     * Sets base natural absorption, clamped in a valid range, optionally reducing actual absorption as needed.<br>
     * <br>
     *
     * <strong>Note:</strong>
     * This changes the value used by the Natural Absorption mod for upgrade/sponge books.
     * To change natural absorption without affecting Natural Absorption's books, use attribute modifiers.
     *
     * @param player                 The player to change the value for.
     * @param updateActualAbsorption If true, the player's ACTUAL absorption hearts will be reduced by the difference
     *                               in max value, if it was lowered (accounting for modifiers).
     * @param value                  The new natural absorption value.
     */
    void setBaseNaturalAbsorption( Player player, boolean updateActualAbsorption, double value );
    
    /**
     * Adds (or removes) base natural absorption, clamped in a valid range, optionally reducing actual absorption as needed.<br>
     * <br>
     *
     * <strong>Note:</strong>
     * This changes the value used by the Natural Absorption mod for upgrade/sponge books.
     * To change natural absorption without affecting Natural Absorption's books, use attribute modifiers.
     *
     * @param player                 The player to change the value for.
     * @param updateActualAbsorption If true, the player's ACTUAL absorption hearts will be reduced by the difference
     *                               in max value, if it was lowered (accounting for modifiers).
     * @param value                  The amount of natural absorption to add (or remove, if negative).
     */
    void addBaseNaturalAbsorption( Player player, boolean updateActualAbsorption, double value );
    
    /**
     * Removes base natural absorption equal to the death penalty, down to a limit, reducing actual absorption to match.
     *
     * @param player The player to apply death penalty to.
     */
    void applyDeathPenalty( Player player );
    
    /**
     * @return The player's max absorption granted by equipment. That is, how much they would lose by unequipping everything.
     */
    double getEquipmentAbsorption( Player player );
    
    /**
     * @return The player's equipment absorption from enchantments, ignoring all attribute modifiers.
     */
    double getEnchantmentAbsorption( Player player );
    
    /**
     * @return The player's equipment absorption from armor replacement, ignoring all attribute modifiers.
     */
    double getArmorReplacementAbsorption( Player player );
}