package fathertoast.naturalabsorption.api;

public interface IHeartData {

    float getNaturalAbsorption();

    void setNaturalAbsorption(float value);

    int getAbsorptionDelay();

    void setAbsorptionDelay(int value);

    void reduceAbsorptionDelay(int value);

    int getHealthDelay();

    void setHealthDelay(int value);

    void reduceHealthDelay(int value);

    void startRecoveryDelay();

    float getMaxAbsorption();

    void setAbsorption(float value);
}
