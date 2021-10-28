package fathertoast.naturalabsorption.common.recipe.condition;

import com.google.gson.JsonObject;
import fathertoast.naturalabsorption.common.config.Config;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.health.HeartManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class BookRecipeCondition implements ICondition {
    
    public enum Type {
        NONE( "none" ),
        SIMPLE( "simple" ),
        SANDWICH( "sandwich" ),
        CROSS( "cross" ),
        SURROUND( "surround" );
        
        private final String NAME;
        
        Type( String name ) { NAME = name; }
        
        public String getNAME() { return NAME; }
    }
    
    private final ResourceLocation id;
    private final String styleName;
    
    public BookRecipeCondition( String name ) {
        id = NaturalAbsorption.resourceLoc( "recipe_style" );
        styleName = name;
    }
    
    @Override
    public ResourceLocation getID() { return id; }
    
    @Override
    public boolean test() {
        return HeartManager.isAbsorptionEnabled() && Config.ABSORPTION.NATURAL.upgradeGain.get() > 0.0 &&
                Config.ABSORPTION.NATURAL.upgradeBookRecipe.get().name().equalsIgnoreCase( styleName );
    }
    
    public static class Serializer implements IConditionSerializer<BookRecipeCondition> {
        
        private final ResourceLocation id;
        
        public Serializer( ResourceLocation resourceLocation ) { id = resourceLocation; }
        
        @Override
        public void write( JsonObject json, BookRecipeCondition value ) { json.addProperty( "style_name", value.styleName ); }
        
        @Override
        public BookRecipeCondition read( JsonObject json ) {
            return new BookRecipeCondition( json.getAsJsonPrimitive( "style_name" ).getAsString() );
        }
        
        @Override
        public ResourceLocation getID() { return id; }
    }
}