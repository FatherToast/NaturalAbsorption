# NaturalAbsorption
Provides the player with a regenerating shield, intended for play with no health regen (or very slow regen). Also can be configured to replace armor's damage reduction with absorption.

Changelog is located at src/main/resources/changelog.txt



# Note to developers:

To obtain the mod's API instance at runtime, you can do the following:

1. Create a class that implements "Function<INaturalAbsorption, Void>" 
    - Example class:
      ```
      public class NaturalAbsorptionPlugin implements Function<INaturalAbsorption, Void> {
      
          public static INaturalAbsorption naturalAbsorptionApi = null;
          
          @Override
          public Void apply(INaturalAbsorption iNaturalAbsorption) {
              naturalAbsorptionApi = iNaturalAbsorption;
              return null;
          }
      }
      ```

2. Send an IMC message to naturalabsorption with method name "getNaturalAbsorptionAPI" and provide a Supplier of the class created in step 1.
    - Example IMC event method:
      ```
      public void sendIMCMessages(InterModEnqueueEvent event) {
            InterModComms.sendTo("naturalabsorption", "getNaturalAbsorptionAPI", NaturalAbsorptionPlugin::new);
      }
      ```

3. Swoosh! Once the IMC message is processed, the API instance can be accessed in the "apply()" method of the class object sent in step 2.




