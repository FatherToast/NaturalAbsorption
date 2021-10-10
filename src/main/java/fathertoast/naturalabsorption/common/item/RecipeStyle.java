package fathertoast.naturalabsorption.common.item;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.BooleanSupplier;

@SuppressWarnings( "unused" )
public class RecipeStyle implements ICondition {

	public enum Type {
		NONE, SIMPLE, SANDWICH, CROSS, SURROUND
	}

	private static final ResourceLocation ID = new ResourceLocation("");

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test() {
		return false;
	}
	
	@Override
	public BooleanSupplier parse( JsonContext context, JsonObject json ) {
		String styleName = json.get( "name" ).getAsString( );
		return ( ) -> Config.get( ).ABSORPTION_UPGRADES.ENABLED && Config.get( ).ABSORPTION_UPGRADES.RECIPE.name( ).equalsIgnoreCase( styleName );
	}
}
