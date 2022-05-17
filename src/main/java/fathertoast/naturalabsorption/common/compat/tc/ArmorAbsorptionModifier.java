package fathertoast.naturalabsorption.common.compat.tc;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.health.HeartData;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class ArmorAbsorptionModifier extends Modifier {

    public ArmorAbsorptionModifier() {
        super(0xFFF923);
    }

    @Override
    public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
        IModifiable.setRarity(volatileData, Rarity.UNCOMMON);
    }

    @Override
    public void addInformation(IModifierToolStack tool, int level, @Nullable PlayerEntity player, List<ITextComponent> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        tooltip.add(new TranslationTextComponent(TextFormatting.YELLOW + new TranslationTextComponent(References.TC_ARMOR_ABSORPTION, Config.EQUIPMENT.ENCHANTMENT.potencyPerLevel.get() * level).getString()));
    }

    @Override
    public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
        if (context.getEntity() instanceof ServerPlayerEntity && context.getChangedSlot().getType() == EquipmentSlotType.Group.ARMOR) {
            ServerPlayerEntity player = (ServerPlayerEntity) context.getEntity();
            HeartData heartData = HeartData.get(player);

            float absorption = heartData.getTCModifierAbsorption() - (float) (level * Config.EQUIPMENT.ENCHANTMENT.potencyPerLevel.get());
            heartData.setTcModifierAbsorption(Math.max(0.0F, absorption));
        }
    }

    @Override
    public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
        if (context.getEntity() instanceof ServerPlayerEntity && context.getChangedSlot().getType() == EquipmentSlotType.Group.ARMOR) {
            ServerPlayerEntity player = (ServerPlayerEntity) context.getEntity();
            HeartData heartData = HeartData.get(player);

            heartData.setTcModifierAbsorption(heartData.getTCModifierAbsorption() + (float) (level * Config.EQUIPMENT.ENCHANTMENT.potencyPerLevel.get()));
        }
    }
}
