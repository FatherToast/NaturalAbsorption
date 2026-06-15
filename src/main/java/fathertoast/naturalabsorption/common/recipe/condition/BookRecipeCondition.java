package fathertoast.naturalabsorption.common.recipe.condition;

import com.google.gson.JsonObject;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.config.Config;
import fathertoast.naturalabsorption.common.core.hearts.HeartManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class BookRecipeCondition implements ICondition {
    
    private static final ResourceLocation ID = NaturalAbsorption.rl( "recipe_style" );
    
    private final String styleName;
    
    public BookRecipeCondition( String name ) {
        styleName = name;
    }
    
    @Override
    public ResourceLocation getID() { return ID; }
    
    @Override
    public boolean test( IContext context ) {
        return HeartManager.isAbsorptionEnabled() && Config.ABSORPTION.NATURAL.upgradeGain.get() > 0.0 &&
                Config.ABSORPTION.NATURAL.upgradeBookRecipe.get().name().equalsIgnoreCase( styleName );
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
    
    public enum Type implements StringRepresentable {
        NONE( "none" ),
        SIMPLE( "simple" ),
        SANDWICH( "sandwich" ),
        CROSS( "cross" ),
        SURROUND( "surround" );
        
        private final String name;
        
        Type( String name ) { this.name = name; }
        
        @Override
        public String getSerializedName() {
            return name;
        }
    }
}