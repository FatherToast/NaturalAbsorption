package fathertoast.naturalabsorption.api.impl;

import fathertoast.naturalabsorption.api.IHeartData;
import fathertoast.naturalabsorption.api.INaturalAbsorption;
import fathertoast.naturalabsorption.common.health.HeartData;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

/**
 * This is the API implementation. Woah!
 */
public class NaturalAbsorptionAPI implements INaturalAbsorption {

    @Nullable
    @Override
    public IHeartData get(PlayerEntity player) {
        return HeartData.get(player);
    }
}