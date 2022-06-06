package fathertoast.naturalabsorption.api;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Helper class that contains easy-access
 * registry entries from Natural Absorption.
 */

@ObjectHolder("naturalabsorption")
public class ModObjects {

    /**
     * The attribute for player natural absorption.<br>
     * <br>
     *
     * <strong>Note:</strong> This attribute's value
     * persists upon player death.
     */
    @ObjectHolder("natural_absorption")
    public static final Attribute NATURAL_ABSORPTION = null;

    /**
     *  The attribute for player equipment absorption.<br>
     *  Equipment absorption is intended not just for armor,<br>
     *  but also for anything else that is not natural absorption.
     */
    @ObjectHolder("equipment_absorption")
    public static final Attribute EQUIPMENT_ABSORPTION = null;
}
