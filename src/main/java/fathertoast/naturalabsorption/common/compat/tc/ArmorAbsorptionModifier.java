package fathertoast.naturalabsorption.common.compat.tc;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.register.NAAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Rarity;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class ArmorAbsorptionModifier extends Modifier implements EquipmentChangeModifierHook, VolatileDataModifierHook {
    
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
    protected void registerHooks( ModuleHookMap.Builder hookBuilder ) {
        super.registerHooks( hookBuilder );
        hookBuilder.addHook(this, ModifierHooks.VOLATILE_DATA );
        hookBuilder.addHook( this, ModifierHooks.EQUIPMENT_CHANGE );
    }


    @Override
    public void addVolatileData( IToolContext context, ModifierEntry modifier, ModDataNBT volatileData ) {
        IModifiable.setRarity( volatileData, Rarity.UNCOMMON );
    }

    @Override
    public MutableComponent applyStyle(MutableComponent component) {
        return component.withStyle( ChatFormatting.YELLOW );
    }

    @Override
    public void onUnequip( IToolStackView tool, ModifierEntry entry, EquipmentChangeContext context ) {
        if( context.getEntity() instanceof ServerPlayer player && context.getChangedSlot().getType() == EquipmentSlot.Type.ARMOR ) {
            UUID uuid = modifierSlotUuids[context.getChangedSlot().getIndex()];
            
            player.getAttribute( NAAttributes.EQUIPMENT_ABSORPTION.get() ).removeModifier( uuid );
        }
    }
    
    @Override
    public void onEquip( IToolStackView tool, ModifierEntry entry, EquipmentChangeContext context ) {
        if( context.getEntity() instanceof ServerPlayer player && context.getChangedSlot().getType() == EquipmentSlot.Type.ARMOR ) {
            double absorptionBonus = entry.getLevel() * Config.COMPAT.TC.potencyPerLevel.get();
            AttributeModifier modifier = new AttributeModifier( modifierSlotUuids[context.getChangedSlot().getIndex()],
                    "TC Modifier absorption boost", absorptionBonus, AttributeModifier.Operation.ADDITION );
            player.getAttribute( NAAttributes.EQUIPMENT_ABSORPTION.get() ).addTransientModifier( modifier );
        }
    }
}