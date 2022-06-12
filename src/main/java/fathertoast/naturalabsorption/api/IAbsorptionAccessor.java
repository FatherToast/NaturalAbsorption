package fathertoast.naturalabsorption.api;

import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * This interface allows modders to easily<br>
 * access and modify a player's absorption attribute values<br>
 * while also applying config-based logic and restrictions.
 */
public interface IAbsorptionAccessor {

    /**
     * Adds/subtracts the given value from the player's natural absorption attribute base value.<br>
     * <br>
     *
     * <strong>Note:</strong> when possible, try to avoid using this and use attribute modifiers instead.<br>
     * <br>
     *
     * @param player The player to change the value for.
     * @param updateActualAbsorption If true, the player's ACTUAL absorption hearts will
     *                               be updated to match the new natural absorption capacity.
     * @param amount The amount of natural absorption to add/remove.
     */
    void addNaturalAbsorption(@Nonnull PlayerEntity player, boolean updateActualAbsorption, double amount);

    /**
     * Adds/subtracts the given value from the player's equipment absorption attribute base value.<br>
     * <br>
     *
     * <strong>Note:</strong> when possible, try to avoid using this and use attribute modifiers instead.<br>
     * <br>
     *
     * @param player The player to change the value for.
     * @param updateActualAbsorption If true, the player's ACTUAL absorption hearts will
     *                               be updated to match the new natural absorption capacity.
     * @param amount The amount of equipment absorption to add/remove.
     */
    void addEquipmentAbsorption(@Nonnull PlayerEntity player, boolean updateActualAbsorption, double amount);

    /**
     * @return The given Player's total natural absorption amount.
     */
    double getNaturalAbsorption(@Nonnull PlayerEntity player);

    /**
     * @return The given Player's total equipment absorption amount.
     */
    double getEquipmentAbsorption(@Nonnull PlayerEntity player);

    /**
     * @return The given Player's total absorption capacity from all sources.<br>
     * <br>
     * (Natural absorption, Equipment absorption and Potion effect absorption)
     */
    double getSteadyStateMaxAbsorption(@Nonnull PlayerEntity player);

    /**
     * Helper method for effectively changing the value of an existing absorption attribute modifier.<br>
     * <br>
     *
     * @param player The Player to change a modifier value for.
     * @param natural If true, look for a natural absorption modifier. If not, look for an equipment absorption modifier.
     * @param modifierId The UUID of the target modifier.
     * @param amount The amount to add/subtract from the modifier.
     */
    void changeAbsorptionModifier(@Nonnull PlayerEntity player, boolean natural, UUID modifierId, double amount);
}
