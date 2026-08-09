package fathertoast.naturalabsorption.api;

/**
 * Contains information about a living entity's absorption from the Natural Absorption mod,
 * along with methods to interact with it. Only available server-side.
 */
public interface IHeartData {
    
    /** Starts the entity's recovery delay timers. */
    void startRecoveryDelay();
    
    /** @return The entity's current absorption delay. */
    int getAbsorptionDelay();
    
    /** Sets the entity's current absorption delay. */
    void setAbsorptionDelay( int value );
    
    /** @return The entity's current health delay. */
    int getHealthDelay();
    
    /** Sets the entity's current health delay. */
    void setHealthDelay( int value );
}