package fathertoast.naturalabsorption.common.compat.tc;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.register.NAAttributes;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.entity.ai.attributes.AttributeModifier;
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
import java.util.UUID;

public class ArmorAbsorptionModifier extends Modifier {

    private static final UUID[] modifierSlotUuids = new UUID[] {
            UUID.fromString("96676b21-1425-498a-b509-0d8f2709ce8c"),
            UUID.fromString("6c425498-9c9a-4bed-9c61-e96e653b99cd"),
            UUID.fromString("eb43f9cb-54f1-4e8c-a6ad-0a07e7a23dc2"),
            UUID.fromString("a2c9e2e9-3a2c-4357-af24-fa13900970d3")
    };

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
            UUID uuid = modifierSlotUuids[context.getChangedSlot().getIndex()];

            player.getAttribute(NAAttributes.EQUIPMENT_ABSORPTION.get()).removeModifier(uuid);
        }
    }

    @Override
    public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
        if (context.getEntity() instanceof ServerPlayerEntity && context.getChangedSlot().getType() == EquipmentSlotType.Group.ARMOR) {
            ServerPlayerEntity player = (ServerPlayerEntity) context.getEntity();

            double absorptionBonus = level * Config.EQUIPMENT.ENCHANTMENT.potencyPerLevel.get();
            AttributeModifier modifier = new AttributeModifier(modifierSlotUuids[context.getChangedSlot().getIndex()], "TC Modifier absorption boost", absorptionBonus, AttributeModifier.Operation.ADDITION);
            player.getAttribute(NAAttributes.EQUIPMENT_ABSORPTION.get()).addTransientModifier(modifier);
        }
    }
}
