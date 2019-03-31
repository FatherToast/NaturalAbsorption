package fathertoast.naturalabsorption;

import fathertoast.naturalabsorption.config.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@SuppressWarnings( "WeakerAccess" )
@Mod( modid = NaturalAbsorptionMod.MOD_ID, name = NaturalAbsorptionMod.NAME, version = NaturalAbsorptionMod.VERSION )
public
class NaturalAbsorptionMod
{
	public static final String MOD_ID  = "naturalabsorption";
	public static final String NAME    = "Natural Absorption";
	public static final String VERSION = "1.0.0_for_mc1.12.2";
	
	/*
	 * TODO:
	 * 	- Make armor toughness recovery apply when the armor multiplier override is enabled
	 */
	
	/** Handles operations when different behaviors are needed between the client and server sides. */
	@SidedProxy( modId = NaturalAbsorptionMod.MOD_ID, clientSide = "fathertoast.naturalabsorption.client.ClientProxy", serverSide = "fathertoast.naturalabsorption.server.ServerProxy" )
	public static SidedModProxy sidedProxy;
	
	/** The translation key used by this mod. */
	public static final String LANG_KEY = NaturalAbsorptionMod.MOD_ID + ".";
	
	private static Logger logger;
	
	/** @return The logger used by this mod. */
	public static
	Logger log( ) { return logger; }
	
	private static SimpleNetworkWrapper networkWrapper;
	
	/** @return The network channel for this mod. */
	public static
	SimpleNetworkWrapper network( ) { return networkWrapper; }
	
	@Mod.EventHandler
	public
	void preInit( FMLPreInitializationEvent event )
	{
		logger = event.getModLog( );
		
		Config.load( log( ), "Natural_Absorption", event.getModConfigurationDirectory( ) );
		
		int id = -1;
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel( "NA|HD" );
		if( event.getSide( ) == Side.CLIENT ) {
			network( ).registerMessage( MessageCapacity.Handler.class, MessageCapacity.class, ++id, Side.CLIENT );
		}
		
		MinecraftForge.EVENT_BUS.register( new ModObjects( ) );
	}
	
	@Mod.EventHandler
	public
	void init( FMLInitializationEvent event )
	{
		NaturalAbsorptionMod.sidedProxy.registerEventHandlers( );
	}
	
	@Mod.EventHandler
	public
	void postInit( FMLPostInitializationEvent event ) { }
}
