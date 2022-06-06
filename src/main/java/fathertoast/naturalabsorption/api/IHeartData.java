package fathertoast.naturalabsorption.api;

/**
 * Contains information about a player's absorption from the Natural Absorption mod,
 * along with methods to interact with it. Only available server-side.
 */
public interface IHeartData {

    /** Starts the player's recovery delay timers. */
    void startRecoveryDelay();

    /** @return The player's current absorption delay. */
    int getAbsorptionDelay();

    /** Sets the player's current absorption delay. */
    void setAbsorptionDelay(int value);

    /** @return The player's current health delay. */
    int getHealthDelay();

    /** Sets the player's current health delay. */
    void setHealthDelay(int value);
}