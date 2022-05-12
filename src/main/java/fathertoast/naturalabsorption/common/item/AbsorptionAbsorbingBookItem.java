package fathertoast.naturalabsorption.common.item;

import fathertoast.naturalabsorption.client.RenderEventHandler;
import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.health.HeartData;
import fathertoast.naturalabsorption.common.health.HeartManager;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

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

            if(naturalAbsorption >= upgradeGain) {
                final float newAbsorption = naturalAbsorption - upgradeGain;
                data.setNaturalAbsorption(newAbsorption, true);

                if (!isCreative) {
                    spongeBook.shrink(1);
                    Block.popResource(world, player.blockPosition(), new ItemStack(NAItems.ABSORPTION_BOOK.get()));

                    double xpReturnMult = Config.ABSORPTION.GENERAL.spongeBookXpReturn.get();

                    if (xpReturnMult > 0) {
                        final int levelsReturned = (int) (xpReturnMult * getLevelCost(newAbsorption));
                        player.giveExperienceLevels(levelsReturned);
                    }
                }
                world.playSound(null, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), SoundEvents.WOOL_PLACE, SoundCategory.PLAYERS, 0.9F, 1.0F);
                world.playSound(null, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.75F, 1.0F);
                player.awardStat(Stats.ITEM_USED.get(this));
                return ActionResult.consume(spongeBook);
            }
            else {
                player.displayClientMessage(new TranslationTextComponent(References.NOT_ENOUGH_ABSORPTION), true);
                return ActionResult.fail(spongeBook);
            }
        }
        return ActionResult.fail(spongeBook);
    }

    @Override
    @OnlyIn( value = Dist.CLIENT )
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag ) {
        if (!Config.ABSORPTION.GENERAL.spongeBookExtraTooltipInfo.get())
            return;

        final PlayerEntity player = Minecraft.getInstance().player;

        if (player == null)
            return;

        final float capacity = RenderEventHandler.PLAYER_NATURAL_ABSORPTION;
        final float upgradeGain = (float) Config.ABSORPTION.NATURAL.upgradeGain.get();

        if (capacity >= upgradeGain) {
            final float subtractedAbsorption = capacity - upgradeGain;
            double xpReturnMult = Config.ABSORPTION.GENERAL.spongeBookXpReturn.get();

            final int levelsReturned = (int) (xpReturnMult * getLevelCost(subtractedAbsorption));
            tooltip.add(new StringTextComponent(""));
            tooltip.add(new TranslationTextComponent(TextFormatting.GRAY + translate(References.BOOK_GAIN).getString()));
            tooltip.add(new TranslationTextComponent((xpReturnMult > 0 ? TextFormatting.GREEN : TextFormatting.YELLOW) + translate(References.SPONGE_BOOK_XP_GAIN, levelsReturned).getString()));
            tooltip.add(new TranslationTextComponent(TextFormatting.RED + translate(References.SPONGE_BOOK_ABSORPTION_LOST, upgradeGain).getString()));
            tooltip.add(new StringTextComponent(""));
            tooltip.add(new TranslationTextComponent(TextFormatting.GRAY + translate(References.BOOK_CAN_USE).getString()));
        }
        else {
            tooltip.add(new TranslationTextComponent(TextFormatting.RED + translate(References.BOOK_NO_USE).getString()));
        }
    }

    private ITextComponent translate(String key, Object... args) {
        return new TranslationTextComponent(key, args);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }
}
