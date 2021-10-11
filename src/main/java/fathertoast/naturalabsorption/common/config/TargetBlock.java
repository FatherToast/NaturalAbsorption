package fathertoast.naturalabsorption.common.config;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashSet;


/**
 *  TODO
 *
 *  This class is likely redundant now that
 *  block metadata is no longer a thing, and
 *  is not used to create block variants.
 *
 *  - Sarinsa
 */
@SuppressWarnings( { "WeakerAccess", "unused" } )
public class TargetBlock {
	// Returns a new target block set from the string property.
	public static HashSet< TargetBlock > newBlockSet( String line ) {
		return TargetBlock.newBlockSet( line.split( "," ) );
	}
	
	public static HashSet< TargetBlock > newBlockSet( String[] targetableBlocks ) {
		HashSet< TargetBlock > blockSet = new HashSet< TargetBlock >( );
		String[] pair, modCheck;
		TargetBlock targetableBlock;

		for( String target : targetableBlocks ) {
			pair = target.split( " ", 2 );
			if( pair.length > 1 ) {
				targetableBlock = new TargetBlock( TargetBlock.getStringAsBlock( pair[ 0 ] ), Integer.parseInt( pair[ 1 ].trim( ) ) );
			}
			else {
				if( pair[ 0 ].endsWith( "*" ) ) {
					TargetBlock.addAllModBlocks( blockSet, pair[ 0 ].substring( pair[ 0 ].length( ) - 1 ) );
					continue;
				}
				
				targetableBlock = new TargetBlock( TargetBlock.getStringAsBlock( pair[ 0 ] ), -1 );
			}
			
			if( targetableBlock.BLOCK == null || targetableBlock.BLOCK == Blocks.AIR ) {
				continue;
			}
			
			if( targetableBlock.BLOCK_DATA < 0 ) {
				while( blockSet.contains( targetableBlock ) ) {
					blockSet.remove( targetableBlock );
				}
			}
			blockSet.add( targetableBlock );
		}
		return blockSet;
	}

	@Nullable
	static BlockState getStringAsBlock( ResourceLocation id ) {
		if ( ForgeRegistries.BLOCKS.containsKey( id ) ) {
			return ForgeRegistries.BLOCKS.getValue( id ).defaultBlockState();
		}
		else {
			Config.log.warn( "Could not find any blocks matching the id \"" + id.toString() + "\"" );
		}
		return null;
	}
	
	private static void addAllModBlocks( HashSet< TargetBlock > blockSet, String namespace ) {
		try {
			TargetBlock targetableBlock;
			for( ResourceLocation blockId : Block.REGISTRY.getKeys( ) ) {
				if( blockId.toString( ).startsWith( namespace ) ) {
					targetableBlock = new TargetBlock( TargetBlock.getStringAsBlock( blockId.toString( ) ), -1 );
					if( targetableBlock.BLOCK == null || targetableBlock.BLOCK == Blocks.AIR ) {
						continue;
					}
					while( blockSet.contains( targetableBlock ) ) {
						blockSet.remove( targetableBlock );
					}
					blockSet.add( targetableBlock );
				}
			}
		}
		catch( Exception ex ) {
			Config.log.error( "Caught exception while adding namespace! ({}*)", namespace );
		}
	}
	
	// The block to match.
	public final Block BLOCK;
	// The metadata of the block to match (-1 matches all).
	public final int   BLOCK_DATA;
	
	public TargetBlock( Block block, int meta ) {
		this.BLOCK = block;
		this.BLOCK_DATA = meta;
	}
	
	public TargetBlock( Block block ) {
		this( block, -1 );
	}
	
	public TargetBlock( BlockState block, int meta ) {
		this( block.getBlock( ), meta );
	}
	
	public
	TargetBlock( BlockState block ) {
		this( block, block.getBlock( ).getMetaFromState( block ) );
	}
	
	// Used to sort this object in a hash table.
	@Override
	public
	int hashCode( )
	{
		return Block.getIdFromBlock( this.BLOCK );
	}
	
	// Returns true if this object is equal to the given object.
	// Specifically, if the compared object is also a target block with the same block type, it is considered
	// equal if their metadata is the same or if either has 'wildcard' metadata.
	@Override
	public
	boolean equals( Object obj ) {
		if( obj instanceof TargetBlock && this.BLOCK.equals( ((TargetBlock) obj).BLOCK ) )
			return this.BLOCK_DATA < 0 || ((TargetBlock) obj).BLOCK_DATA < 0 || this.BLOCK_DATA == ((TargetBlock) obj).BLOCK_DATA;
		return false;
	}
}