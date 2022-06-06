package fathertoast.naturalabsorption.api;

import net.minecraft.entity.player.PlayerEntity;

/**
 * This interface allows modders to easily<br>
 * access and modify a player's absorption attribute values<br>
 * while also applying config-based logic and restrictions.
 */
public interface IAbsorptionAccessor {

    /**
     * Sets the given player's natural absorption attribute
     * value to the desired value.<br>
     * <br>
     *
     * @param player The player to change the value for.
     * @param updateActualAbsorption If true, the player's ACTUAL absorption hearts will
     *                               be updated to match the new natural absorption level.
     * @param value The new natural absorption value.
     */
    void setNaturalAbsorption(PlayerEntity player, boolean updateActualAbsorption, double value);

    void addNaturalAbsorption(PlayerEntity player, boolean updateActualAbsorption, double amount);

    void removeNaturalAbsorption(PlayerEntity player, double amount);

    double getNaturalAbsorption(PlayerEntity player);

    void setEquipmentAbsorption(PlayerEntity player, boolean updateActualAbsorption, double value);

    void addEquipmentAbsorption(PlayerEntity player, boolean updateActualAbsorption, double amount);

    void removeEquipmentAbsorption(PlayerEntity player, double amount);

    double getEquipmentAbsorption(PlayerEntity player);

    double getSteadyStateMaxAbsorption(PlayerEntity player);
}
