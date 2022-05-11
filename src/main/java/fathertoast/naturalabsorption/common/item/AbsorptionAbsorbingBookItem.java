package fathertoast.naturalabsorption.common.item;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.health.HeartData;
import fathertoast.naturalabsorption.common.health.HeartManager;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import static fathertoast.naturalabsorption.common.item.AbsorptionBookItem.getLevelCost;

public class AbsorptionAbsorbingBookItem extends Item {

    public AbsorptionAbsorbingBookItem() {
        super(new Item.Properties()
                .tab(ItemGroup.TAB_MISC)
                .stacksTo(1));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand ) {
        // Check if this item is enabled in the config
        if(!HeartManager.isAbsorptionEnabled() || !Config.ABSORPTION.GENERAL.spongeBookEnabled.get()) {
            return super.use(world, player, hand);
        }
        final boolean isCreative = player.isCreative();
        final ItemStack spongeBook = player.getItemInHand(hand);

        if(!world.isClientSide) {
            final HeartData data = HeartData.get(player);
            final float naturalAbsorption = data.getNaturalAbsorption();
            final float upgradeGain = (float) Config.ABSORPTION.NATURAL.upgradeGain.get();

            // Give the player feedback on failure
            if(naturalAbsorption >= upgradeGain) {
                final float newAbsorption = naturalAbsorption - upgradeGain;
                data.setNaturalAbsorption(newAbsorption);

                if (!isCreative) {
                    spongeBook.shrink(1);
                    Block.popResource(world, player.blockPosition(), new ItemStack(NAItems.ABSORPTION_BOOK.get()));

                    final int levelsReturned = getLevelCost(newAbsorption);
                    player.giveExperienceLevels(levelsReturned);
                }
                world.playSound(null, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), SoundEvents.WOOL_PLACE, SoundCategory.PLAYERS, 0.9F, 1.0F);
                player.awardStat(Stats.ITEM_USED.get(this));
                return ActionResult.consume(spongeBook);
            }
            else {
                player.displayClientMessage(new TranslationTextComponent(References.BOOK_NO_USE), true);
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
