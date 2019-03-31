package fathertoast.naturalabsorption.item;

import com.google.gson.JsonObject;
import fathertoast.naturalabsorption.config.*;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

@SuppressWarnings( "unused" )
public
class RecipeStyle implements IConditionFactory
{
	public
	enum Type
	{
		NONE, SIMPLE, SANDWICH, CROSS, SURROUND
	}
	
	@Override
	public
	BooleanSupplier parse( JsonContext context, JsonObject json )
	{
		String styleName = json.get( "name" ).getAsString( );
		return ( ) -> Config.get( ).ABSORPTION_UPGRADES.ENABLED && Config.get( ).ABSORPTION_UPGRADES.RECIPE.name( ).equalsIgnoreCase( styleName );
	}
}
