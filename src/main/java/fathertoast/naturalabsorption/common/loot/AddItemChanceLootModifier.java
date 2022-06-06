package fathertoast.naturalabsorption.common.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AddItemChanceLootModifier extends LootModifier {

    protected final List<ResourceLocation> lootTables;
    protected final Item item;
    protected final double chance;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    public AddItemChanceLootModifier(List<ResourceLocation> lootTables, Item item, double chance, ILootCondition[] conditionsIn) {
        super(conditionsIn);
        this.lootTables = lootTables;
        this.item = item;
        this.chance = chance;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if (this.lootTables.contains(context.getQueriedLootTableId())) {
            if (context.getRandom().nextDouble() <= this.chance) {
                generatedLoot.add(new ItemStack(this.item));
            }
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<AddItemChanceLootModifier> {

        @Override
        public AddItemChanceLootModifier read(ResourceLocation location, JsonObject jsonObject, ILootCondition[] conditions) {
            if (jsonObject.has("item") && jsonObject.has("lootTables") && jsonObject.has("chance")) {
                final ResourceLocation itemId = ResourceLocation.tryParse(jsonObject.get("item").getAsString());

                // Read the Item
                if (itemId == null) {
                    NaturalAbsorption.LOG.error("Failed to read global loot modifier of type \"{}\" as \"item\" did not contain a valid ResourceLocation", location);
                    return null;
                }
                Item item = ForgeRegistries.ITEMS.getValue(itemId);

                if (item == null) {
                    NaturalAbsorption.LOG.error("Failed to read global loot modifier of type \"{}\" as \"item\" did not contain the ID of an Item present in the Forge registry", location);
                }

                // Read the list of loot table IDs
                final List<ResourceLocation> lootTableIds = new ArrayList<>();
                JsonArray jsonArray = jsonObject.getAsJsonArray("lootTables");

                for (JsonElement element : jsonArray) {
                    final String s = element.getAsString();
                    ResourceLocation lootTableId = ResourceLocation.tryParse(s);

                    if (lootTableId == null) {
                        NaturalAbsorption.LOG.error("Global loot modifier of type \"{}\" contains invalid loot table ID: \"{}\"", location, s);
                        continue;
                    }
                    lootTableIds.add(lootTableId);
                }

                // Read the chance value
                double chance = jsonObject.get("chance").getAsDouble();

                return new AddItemChanceLootModifier(lootTableIds, item, chance, conditions);
            }
            else {
                NaturalAbsorption.LOG.error("Failed to read global loot modifier of type \"{}\" as it is missing \"item\" and \"lootTables\"", location);
                return null;
            }
        }

        @Override
        public JsonObject write(AddItemChanceLootModifier instance) {
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();

            jsonObject.addProperty("item", instance.item.getRegistryName().toString());

            for (ResourceLocation lootTableId : instance.lootTables) {
                jsonArray.add(lootTableId.toString());
            }
            jsonObject.add("lootTables", jsonArray);
            jsonObject.addProperty("chance", instance.chance);

            return jsonObject;
        }
    }
}
