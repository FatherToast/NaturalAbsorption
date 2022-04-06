package fathertoast.naturalabsorption.common.item;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.health.HeartData;
import fathertoast.naturalabsorption.common.health.HeartManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class AbsorptionAbsorbingBookItem extends Item {

    public AbsorptionAbsorbingBookItem() {
        super(new Item.Properties()
                .tab(ItemGroup.TAB_MISC)
                .stacksTo(1));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand ) {
        // Check if natural absorption can be upgraded
        if(!HeartManager.isAbsorptionEnabled() || !Config.ABSORPTION.GENERAL.spongeBookEnabled.get()) {
            return super.use(world, player, hand);
        }
        final boolean isCreative = player.isCreative();
        final ItemStack spongeBook = player.getItemInHand(hand);

        if(!world.isClientSide) {
            final HeartData data = HeartData.get(player);
            final float naturalAbsorption = data.getNaturalAbsorption();

            // Give the player feedback on failure
            if(naturalAbsorption >= 2.0F) {
                data.setNaturalAbsorption(naturalAbsorption - 2.0F);
                player.addItem(new ItemStack(NAItems.ABSORPTION_BOOK.get()));

                if (!isCreative) {
                    spongeBook.shrink(1);
                }
                world.playSound(null, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), SoundEvents.WOOL_PLACE, SoundCategory.PLAYERS, 0.9F, 1.0F);
                player.awardStat(Stats.ITEM_USED.get(this));
                return ActionResult.consume(spongeBook);
            }
            else {
                return ActionResult.fail(spongeBook);
            }
        }
        return ActionResult.fail(spongeBook);
    }

    @Override
    public Rarity getRarity(ItemStack stack ) {
        return Rarity.UNCOMMON;
    }
}
