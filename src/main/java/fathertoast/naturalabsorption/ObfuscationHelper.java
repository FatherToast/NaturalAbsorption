package fathertoast.naturalabsorption;

import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.Logger;

/**
 * Helper class for using the ObfuscationReflectionHelper.
 * <p>
 */
@SuppressWarnings( { "unused", "WeakerAccess" } )
public
class ObfuscationHelper< T, E >
{
	public static final ObfuscationHelper< DamageSource, Boolean > DamageSource_isUnblockable = new ObfuscationHelper<>(
		DamageSource.class, "bypassArmor"
	);
	
	private static Logger logger( ) { return NaturalAbsorption.LOG; }
	
	private final Class< T > classToAccess;
	private final String name;
	
	private
	ObfuscationHelper( Class< T > fieldClass, String fieldName )
	{
		classToAccess = fieldClass;
		name = fieldName;
	}
	
	public void set( T instance, E value ) {
		try {
			ObfuscationReflectionHelper.setPrivateValue( classToAccess, instance, value, name );
		}
		catch( Exception ex ) {
			logger( ).error( "Failed to set private value ({}#{}={})", classToAccess.getSimpleName( ), name, value, ex );
		}
	}
	
	public E get( T instance ) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue( classToAccess, instance, name );
		}
		catch( Exception ex ) {
			logger( ).error( "Failed to get private value ({}#{}==?)", classToAccess.getSimpleName( ), name, ex );
		}
		return null;
	}
	
	public static class Static< T, E > {
		private final Class< T > classToAccess;
		private final String   name;
		
		private Static( Class< T > fieldClass, String fieldName ) {
			classToAccess = fieldClass;
			name = fieldName;
		}
		
		public void set( E value ) {
			try {
				ObfuscationReflectionHelper.setPrivateValue( classToAccess, null, value, name );
			}
			catch( Exception ex ) {
				logger( ).error( "Failed to set private static value ({}#{}={})", classToAccess.getSimpleName( ), name, value, ex );
			}
		}
		
		public E get( ) {
			try {
				return ObfuscationReflectionHelper.getPrivateValue( classToAccess, null, name );
			}
			catch( Exception ex ) {
				logger( ).error( "Failed to get private static value ({}#{}==?)", classToAccess.getSimpleName( ), name, ex );
			}
			return null;
		}
	}
}