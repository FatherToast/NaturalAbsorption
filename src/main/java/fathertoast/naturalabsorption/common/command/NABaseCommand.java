package fathertoast.naturalabsorption.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import fathertoast.crust.api.lib.CrustCmdHelper;
import fathertoast.naturalabsorption.common.core.NaturalAbsorption;
import fathertoast.naturalabsorption.common.core.hearts.AbsorptionHelper;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;
import java.util.function.Function;

import static fathertoast.crust.api.lib.CrustCmdHelper.*;

public final class NABaseCommand {
    
    public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
        dispatcher.register( literal( NaturalAbsorption.MOD_ID )
                .then( CapacityBaseCommand.register() )
                .then( AbsorptionBaseCommand.register() ) );
    }
    
    /** Base command for absorption capacity modification. */
    private static class CapacityBaseCommand {
        
        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            return literal( "capacity" ).requires( CrustCmdHelper::canCheat )
                    .then( literal( "add" )
                            .then( argumentTargets( "targets" )
                                    .then( argument( "capacity", IntegerArgumentType.integer( 1 ) )
                                            .executes( ( context ) -> addCapacity(
                                                    context.getSource(),
                                                    targets( context, "targets" ),
                                                    IntegerArgumentType.getInteger( context, "capacity" ) ) ) ) ) )
                    .then( literal( "remove" )
                            .then( argumentTargets( "targets" )
                                    .then( argument( "capacity", IntegerArgumentType.integer( 1 ) )
                                            .executes( ( context ) -> removeCapacity(
                                                    context.getSource(),
                                                    targets( context, "targets" ),
                                                    IntegerArgumentType.getInteger( context, "capacity" ) ) ) ) ) )
                    .then( literal( "set" )
                            .then( argumentTargets( "targets" )
                                    .then( argument( "capacity", IntegerArgumentType.integer( 0 ) )
                                            .executes( ( context ) -> setCapacity(
                                                    context.getSource(),
                                                    targets( context, "targets" ),
                                                    IntegerArgumentType.getInteger( context, "capacity" ) ) ) ) ) );
        }
        
        /** Adds the specified amount of capacity to the target entities' max absorption. */
        private static int addCapacity( CommandSourceStack src, Collection<? extends Entity> entities, int absorption ) {
            return livingTargetCmd( src, entities, References.CMD_CHANGE_CAPACITY_SINGLE, References.CMD_CHANGE_CAPACITY_MULTIPLE,
                    entity -> {
                        if( !AbsorptionHelper.hasNaturalAbsorptionAttribute( entity ) ) return false;
                        
                        double currentMaxAbsorption = AbsorptionHelper.getBaseNaturalAbsorption( entity );
                        AbsorptionHelper.setBaseNaturalAbsorption( entity, false, currentMaxAbsorption + absorption );
                        return true;
                    } );
        }
        
        /** Subtracts the specified amount of capacity from the target entities' max absorption. */
        private static int removeCapacity( CommandSourceStack src, Collection<? extends Entity> entities, int absorption ) {
            return livingTargetCmd( src, entities, References.CMD_CHANGE_CAPACITY_SINGLE, References.CMD_CHANGE_CAPACITY_MULTIPLE,
                    entity -> {
                        if( !AbsorptionHelper.hasNaturalAbsorptionAttribute( entity ) ) return false;
                        
                        double currentMaxAbsorption = AbsorptionHelper.getBaseNaturalAbsorption( entity );
                        AbsorptionHelper.setBaseNaturalAbsorption( entity, true, currentMaxAbsorption - absorption );
                        return true;
                    } );
        }
        
        /** Sets the specified amount of max absorption capacity for the target entities. */
        private static int setCapacity( CommandSourceStack src, Collection<? extends Entity> entities, int absorption ) {
            return livingTargetCmd( src, entities, References.CMD_CHANGE_CAPACITY_SINGLE, References.CMD_CHANGE_CAPACITY_MULTIPLE,
                    entity -> {
                        if( !AbsorptionHelper.hasNaturalAbsorptionAttribute( entity ) ) return false;
                        AbsorptionHelper.setBaseNaturalAbsorption( entity, true, absorption );
                        return true;
                    } );
        }
    }
    
    /** Base command for modifying a living entity's current absorption amount. */
    private static class AbsorptionBaseCommand {
        
        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            return literal( "absorption" ).requires( CrustCmdHelper::canCheat )
                    .then( literal( "add" )
                            .then( argumentTargets( "targets" )
                                    .then( argument( "absorption", IntegerArgumentType.integer( 1 ) )
                                            .executes( ( context ) -> addAbsorption(
                                                    context.getSource(),
                                                    targets( context, "targets" ),
                                                    IntegerArgumentType.getInteger( context, "absorption" ) ) ) ) ) )
                    .then( literal( "remove" )
                            .then( argumentTargets( "targets" )
                                    .then( argument( "absorption", IntegerArgumentType.integer( 1 ) )
                                            .executes( ( context ) -> removeAbsorption(
                                                    context.getSource(),
                                                    targets( context, "targets" ),
                                                    IntegerArgumentType.getInteger( context, "absorption" ) ) ) ) ) )
                    .then( literal( "set" )
                            .then( argumentTargets( "targets" )
                                    .then( argument( "absorption", IntegerArgumentType.integer( 0 ) )
                                            .executes( ( context ) -> setAbsorption(
                                                    context.getSource(),
                                                    targets( context, "targets" ),
                                                    IntegerArgumentType.getInteger( context, "absorption" ) ) ) ) ) );
        }
        
        /** Adds the specified amount of absorption to the target entities. */
        private static int addAbsorption( CommandSourceStack src, Collection<? extends Entity> entities, int absorption ) {
            return livingTargetCmd( src, entities, References.CMD_CHANGE_ABSORPTION_SINGLE, References.CMD_CHANGE_ABSORPTION_MULTIPLE,
                    entity -> {
                        float currentAbsorption = entity.getAbsorptionAmount();
                        entity.setAbsorptionAmount( currentAbsorption + Math.min( (float) absorption, Float.MAX_VALUE ) );
                        return true;
                    } );
        }
        
        /** Subtracts the specified amount of absorption from the target entities'. */
        private static int removeAbsorption( CommandSourceStack src, Collection<? extends Entity> entities, int absorption ) {
            return livingTargetCmd( src, entities, References.CMD_CHANGE_ABSORPTION_SINGLE, References.CMD_CHANGE_ABSORPTION_MULTIPLE,
                    entity -> {
                        float currentAbsorption = entity.getAbsorptionAmount();
                        entity.setAbsorptionAmount( Math.max( 0.0F, currentAbsorption - Math.min( (float) absorption, Float.MAX_VALUE ) ) );
                        return true;
                    } );
        }
        
        /** Sets the specified amount of absorption for the target entities. */
        private static int setAbsorption( CommandSourceStack source, Collection<? extends Entity> entities, int absorption ) {
            return livingTargetCmd( source, entities, References.CMD_CHANGE_ABSORPTION_SINGLE, References.CMD_CHANGE_ABSORPTION_MULTIPLE,
                    entity -> {
                        entity.setAbsorptionAmount( Math.min( (float) absorption, Float.MAX_VALUE ) );
                        return true;
                    } );
        }
    }
    
    
    /**
     * Helper method for making commands that operate on living entities only.
     *
     * @param src             The command source stack.
     * @param entities        The list of entities derived from the command's entity target argument.
     *                        Any non-living entities are filtered out before {@code action} is performed for each target.
     * @param singleTargetMsg The translation key of the message to display when only a single target was affected.
     * @param multiTargetMsg  The translation key of the message to display if multiple (or zero) targets were affected.
     * @param function        A function performing an action for each target entity that should return true if successful.
     */
    private static int livingTargetCmd( CommandSourceStack src, Collection<? extends Entity> entities,
                                        String singleTargetMsg, String multiTargetMsg, Function<LivingEntity, Boolean> function ) {
        int entitiesChanged = 0;
        Entity firstChanged = null;
        
        for( Entity entity : entities ) {
            if( entity instanceof LivingEntity livingEntity ) {
                if( function.apply( livingEntity ) ) {
                    if( firstChanged == null ) firstChanged = livingEntity;
                    ++entitiesChanged;
                }
            }
        }
        final Component message;
        
        if( entitiesChanged == 1 ) {
            message = Component.translatable( singleTargetMsg, firstChanged.getDisplayName() );
        }
        else {
            message = Component.translatable( multiTargetMsg, entitiesChanged );
        }
        src.sendSuccess( () -> message, true );
        return entitiesChanged;
    }
    
    
    // Utility class
    private NABaseCommand() { }
}
