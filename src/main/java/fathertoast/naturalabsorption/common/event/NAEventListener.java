package fathertoast.naturalabsorption.common.event;

import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.register.NAAttributes;
import fathertoast.naturalabsorption.common.core.register.NAItems;
import fathertoast.naturalabsorption.common.enchantment.AbsorptionEnchantment;
import fathertoast.naturalabsorption.common.health.HeartData;
import fathertoast.naturalabsorption.common.health.HeartManager;
import fathertoast.naturalabsorption.common.network.NetworkHelper;
import fathertoast.naturalabsorption.common.recipe.condition.BookRecipeCondition;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NAEventListener {

    private static final ResourceLocation ADV_BOOK_RECIPE = NaturalAbsorption.resourceLoc("recipes/" + NAItems.ABSORPTION_BOOK.getId().getPath());

    private static final UUID armorAbsorptionUUID = UUID.fromString("a6a0e621-2ca3-4606-81b3-0cd17308262c");
    private static final UUID armorReplacementAbsorptionUUID = UUID.fromString("447dbb9f-2995-45c6-a1be-c65d26328afc");


    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onAdvancement(AdvancementEvent event) {
        if(HeartManager.isAbsorptionEnabled() && Config.ABSORPTION.NATURAL.upgradeBookRecipe.get() != BookRecipeCondition.Type.NONE) {
            if(event.getAdvancement().getId().equals(ADV_BOOK_RECIPE)) {

                // The advancement for unlocking the book of absorption recipe
                ResourceLocation recipe = new ResourceLocation(
                        NaturalAbsorption.toString( NAItems.ABSORPTION_BOOK.get().getRegistryName() ) + "_" +
                                Config.ABSORPTION.NATURAL.upgradeBookRecipe.get().name().toLowerCase()
                );
                try {
                    event.getPlayer().awardRecipesByKey( new ResourceLocation[] { recipe } );
                }
                catch( Exception e ) {
                    NaturalAbsorption.LOG.warn( "Something went wrong trying to award a player the absorption book recipe! Aw man :(" );
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Ensure the old absorption attribute value is copied over to the new player entity.
     */
    @SubscribeEvent
    public void onPlayerDeath(PlayerEvent.Clone event) {
        double naturalAbsorption = event.getOriginal().getAttributeValue(NAAttributes.NATURAL_ABSORPTION.get());
        event.getPlayer().getAttribute(NAAttributes.NATURAL_ABSORPTION.get()).setBaseValue(naturalAbsorption);
    }

    /**
     * Update equipment absorption when the
     * player changes armor items.
     */
    @SubscribeEvent
    public void onPlayerEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            this.updateEquipmentAbsorption((PlayerEntity) event.getEntityLiving());
        }
    }

    private void updateEquipmentAbsorption(PlayerEntity player) {
        Attribute equipmentAbsorption = NAAttributes.EQUIPMENT_ABSORPTION.get();

        // From armor replacement
        if(HeartManager.isArmorReplacementEnabled()) {
            player.getAttribute(equipmentAbsorption).removeModifier(armorReplacementAbsorptionUUID);

            double bonus = 0.0D;

            if(Config.EQUIPMENT.ARMOR.armorMultiplier.get() > 0.0) {
                final double armor = player.getAttributeValue( Attributes.ARMOR_TOUGHNESS );
                if( armor > 0.0F ) {
                    bonus += Config.EQUIPMENT.ARMOR.armorMultiplier.get() * armor;
                }
            }
            if(Config.EQUIPMENT.ARMOR.armorToughnessMultiplier.get() > 0.0) {
                final double toughness = player.getAttributeValue( Attributes.ARMOR_TOUGHNESS );
                if(toughness > 0.0F) {
                    bonus += Config.EQUIPMENT.ARMOR.armorToughnessMultiplier.get() * toughness;
                }
            }
            if (bonus > 0.0D) {
                player.getAttribute(equipmentAbsorption).addTransientModifier(new AttributeModifier(armorReplacementAbsorptionUUID, "Armor replacement absorption bonus", bonus, AttributeModifier.Operation.ADDITION));
            }
        }

        // From enchantments
        if(Config.EQUIPMENT.ENCHANTMENT.enabled.get()) {
            player.getAttribute(equipmentAbsorption).removeModifier(armorAbsorptionUUID);

            double bonus = AbsorptionEnchantment.getMaxAbsorptionBonus(player);
            player.getAttribute(equipmentAbsorption).addTransientModifier(new AttributeModifier(armorAbsorptionUUID, "Armor absorption bonus", bonus, AttributeModifier.Operation.ADDITION));
        }
    }

    /**
     * Adds our absorption attributes to the player entity type.
     */
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        AttributeModifierMap modifierMap = GlobalEntityTypeAttributes.getSupplier(EntityType.PLAYER);
        AttributeModifierMap.MutableAttribute builder = AttributeModifierMap.builder();

        builder.add(NAAttributes.NATURAL_ABSORPTION.get(), 0.0D);
        builder.add(NAAttributes.EQUIPMENT_ABSORPTION.get(), 0.0D);
        AttributeModifierMap newModifierMap = builder.build();
        Map<Attribute, ModifiableAttributeInstance> map = new HashMap<>();

        map.putAll(modifierMap.instances);
        map.putAll(newModifierMap.instances);
        modifierMap.instances = map;
    }

    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onItemTooltip( ItemTooltipEvent event ) {
        final Food food = event.getItemStack().getItem().getFoodProperties();
        if( food != null ) {
            final int hunger = food.getNutrition();
            final float saturation = HeartManager.calculateSaturation( hunger, food.getSaturationModifier() );

            if( Config.MAIN.GENERAL.foodExtraTooltipInfo.get() ) {
                // Food nutrition values could theoretically be zero or negative, make sure we handle that
                if( hunger != 0 ) {
                    event.getToolTip().add( new TranslationTextComponent( (hunger > 0 ? TextFormatting.BLUE : TextFormatting.RED) +
                            References.translate( References.FOOD_HUNGER, String.format( "%+d", -hunger ) ).getString() ) );
                }
                if( saturation != 0.0F ) {
                    event.getToolTip().add( new TranslationTextComponent( (saturation > 0.0F ? TextFormatting.BLUE : TextFormatting.RED) +
                            References.translate( References.FOOD_SATURATION, (saturation > 0.0F ? "+" : "") + References.prettyToString( saturation ) ).getString() ) );
                }
            }
            if( HeartManager.isHealthEnabled() && Config.HEALTH.GENERAL.foodHealingExtraTooltipInfo.get() ) {
                // Calculate as if the food's entire nutritional value is used
                final float maxHealing = Config.HEALTH.GENERAL.foodHealingMax.get() < 0.0 ? Float.POSITIVE_INFINITY :
                        (float) Config.HEALTH.GENERAL.foodHealingMax.get();
                final float healing = Math.min( HeartManager.getFoodHealing( hunger, saturation ), maxHealing );
                if( healing > 0.0F ) {
                    event.getToolTip().add( new TranslationTextComponent( TextFormatting.BLUE + References.translate( References.FOOD_HEALTH, "+" + References.prettyToString( healing ) ).getString() ) );
                }
            }
        }
    }
}