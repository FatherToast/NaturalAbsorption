package fathertoast.naturalabsorption.api;

/**
 * Contains information about a player's absorption from the Natural Absorption mod,
 * along with methods to interact with it.
 */
public interface IHeartData {
    /** @return The player's natural absorption. */
    float getNaturalAbsorption();
    
    /** Sets the player's natural absorption. The player will gain or lose current absorption to match. */
    void setNaturalAbsorption(float value);
    
    /** Starts the player's recovery delay timers. */
    void startRecoveryDelay();
    
    /** @return The player's max absorption not counting buffs, limited by the global max absorption config. */
    float getSteadyStateMaxAbsorption();
    
    /** @return The player's max absorption actually granted by equipment. That is, how much they would lose by unequipping everything. */
    float getTrueEquipmentAbsorption();
    
    /** @return The player's max absorption, from all sources combined. */
    float getMaxAbsorption();
    
    /** Helper method to set the player's current absorption; clamps the value between 0 and the player's personal maximum. */
    void setAbsorption(float value);
}