package fathertoast.naturalabsorption.common.recipe.condition;

import com.google.gson.JsonObject;
import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.hearts.HeartManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class BookRecipeCondition implements ICondition {
    
    private static final ResourceLocation ID = NaturalAbsorption.resourceLoc( "recipe_style" );
    
    public enum Type {
        NONE( "none" ),
        SIMPLE( "simple" ),
        SANDWICH( "sandwich" ),
        CROSS( "cross" ),
        SURROUND( "surround" );
        
        private final String NAME;
        
        Type( String name ) { NAME = name; }
    }
    
    private final String styleName;
    
    public BookRecipeCondition( String name ) {
        styleName = name;
    }
    
    @Override
    public ResourceLocation getID() { return ID; }
    
    @Override
    public boolean test(IContext context) {
        return HeartManager.isAbsorptionEnabled() && Config.ABSORPTION.NATURAL.upgradeGain.get() > 0.0 &&
                Config.ABSORPTION.NATURAL.upgradeBookRecipe.get().name().equalsIgnoreCase( this.styleName );
    }

    // Deprecated. Only present to fulfill ICondition implementation
    @SuppressWarnings("all")
    @Override
    public boolean test() {
        return false;
    }

    public static class Serializer implements IConditionSerializer<BookRecipeCondition> {
        
        public Serializer() { }
        
        @Override
        public void write( JsonObject json, BookRecipeCondition value ) { json.addProperty( "name", value.styleName ); }
        
        @Override
        public BookRecipeCondition read( JsonObject json ) {
            return new BookRecipeCondition( json.getAsJsonPrimitive( "name" ).getAsString() );
        }
        
        @Override
        public ResourceLocation getID() { return ID; }
    }
}