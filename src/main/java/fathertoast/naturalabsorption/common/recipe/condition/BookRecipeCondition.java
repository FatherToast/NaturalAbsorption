package fathertoast.naturalabsorption.common.recipe.condition;

import com.google.gson.JsonObject;
import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class BookRecipeCondition implements ICondition {

    public enum Type {
        NONE("none"),
        SIMPLE("simple"),
        SANDWICH("sandwich"),
        CROSS("cross"),
        SURROUND("surround");

        Type(String name) {
            this.name = name;
        }
        private final String name;

        public String getName() {
            return this.name;
        }
    }

    private final ResourceLocation id;
    private final String styleName;

    public BookRecipeCondition(String styleName) {
        this.id = new ResourceLocation(NaturalAbsorption.MOD_ID, "recipe_style");
        this.styleName = styleName;
    }

    @Override
    public ResourceLocation getID() {
        return this.id;
    }

    @Override
    public boolean test() {
        return Config.get( ).ABSORPTION_UPGRADES.ENABLED && Config.get( ).ABSORPTION_UPGRADES.RECIPE.name( ).equalsIgnoreCase( styleName );
    }

    public static class Serializer implements IConditionSerializer<BookRecipeCondition> {

        private final ResourceLocation id;

        public Serializer(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public void write(JsonObject json, BookRecipeCondition value) {
            json.addProperty("style_name", value.styleName);
        }

        @Override
        public BookRecipeCondition read(JsonObject json) {
            return new BookRecipeCondition(json.getAsJsonPrimitive("style_name").getAsString());
        }

        @Override
        public ResourceLocation getID() {
            return this.id;
        }
    }
}
