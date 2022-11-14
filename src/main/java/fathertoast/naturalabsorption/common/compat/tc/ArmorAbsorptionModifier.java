package fathertoast.naturalabsorption.common.compat.tc;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.register.NAAttributes;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class ArmorAbsorptionModifier extends Modifier {
    
    private static final UUID[] modifierSlotUuids = new UUID[] {
            UUID.fromString( "96676b21-1425-498a-b509-0d8f2709ce8c" ),
            UUID.fromString( "6c425498-9c9a-4bed-9c61-e96e653b99cd" ),
            UUID.fromString( "eb43f9cb-54f1-4e8c-a6ad-0a07e7a23dc2" ),
            UUID.fromString( "a2c9e2e9-3a2c-4357-af24-fa13900970d3" )
    };
    
    public ArmorAbsorptionModifier() {
        super( );
    }

    @Override
    public void addVolatileData( ToolRebuildContext context, int level, ModDataNBT volatileData ) {
        IModifiable.setRarity( volatileData, Rarity.UNCOMMON );
    }
    
    @Override
    public void addInformation( IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag ) {
        tooltip.add( new TranslatableComponent( ChatFormatting.YELLOW + new TranslatableComponent(
                References.TC_ARMOR_ABSORPTION_TOOLTIP, References.prettyToString( (float) Config.COMPAT.TC.potencyPerLevel.get() * level ) ).getString() ) );
    }
    
    @Override
    public void onUnequip( IToolStackView tool, int level, EquipmentChangeContext context ) {
        if( context.getEntity() instanceof ServerPlayer player && context.getChangedSlot().getType() == EquipmentSlot.Type.ARMOR ) {
            UUID uuid = modifierSlotUuids[context.getChangedSlot().getIndex()];
            
            player.getAttribute( NAAttributes.EQUIPMENT_ABSORPTION.get() ).removeModifier( uuid );
        }
    }
    
    @Override
    public void onEquip( IToolStackView tool, int level, EquipmentChangeContext context ) {
        if( context.getEntity() instanceof ServerPlayer player && context.getChangedSlot().getType() == EquipmentSlot.Type.ARMOR ) {
            double absorptionBonus = level * Config.COMPAT.TC.potencyPerLevel.get();
            AttributeModifier modifier = new AttributeModifier( modifierSlotUuids[context.getChangedSlot().getIndex()],
                    "TC Modifier absorption boost", absorptionBonus, AttributeModifier.Operation.ADDITION );
            player.getAttribute( NAAttributes.EQUIPMENT_ABSORPTION.get() ).addTransientModifier( modifier );
        }
    }
}