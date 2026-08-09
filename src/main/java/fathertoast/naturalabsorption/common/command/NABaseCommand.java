package fathertoast.naturalabsorption.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import fathertoast.naturalabsorption.common.core.hearts.AbsorptionHelper;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class NABaseCommand {
    
    public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
        dispatcher.register( Commands.literal( "naturalabsorption" )
                .then( CapacityBaseCommand.register() )
                .then( AbsorptionBaseCommand.register() ) );
    }
    
    /** Base command for absorption capacity modification. */
    private static class CapacityBaseCommand {
        
        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal( "capacity" )
                    .requires( ( source ) -> source.hasPermission( 2 ) )
                    .then( Commands.literal( "add" )
                            .then( Commands.argument( "targets", EntityArgument.players() )
                                    .then( Commands.argument( "capacity", IntegerArgumentType.integer( 1 ) )
                                            .executes( ( context ) -> addCapacity(
                                                    context.getSource(),
                                                    EntityArgument.getPlayers( context, "targets" ),
                                                    IntegerArgumentType.getInteger( context, "capacity" )
                                            ) ) ) )
                    )
                    .then( Commands.literal( "remove" )
                            .then( Commands.argument( "targets", EntityArgument.players() )
                                    .then( Commands.argument( "capacity", IntegerArgumentType.integer( 1 ) )
                                            .executes( ( context ) -> removeCapacity(
                                                    context.getSource(),
                                                    EntityArgument.getPlayers( context, "targets" ),
                                                    IntegerArgumentType.getInteger( context, "capacity" )
                                            ) ) ) )
                    )
                    .then( Commands.literal( "set" )
                            .then( Commands.argument( "targets", EntityArgument.players() )
                                    .then( Commands.argument( "capacity", IntegerArgumentType.integer( 0 ) )
                                            .executes( ( context ) -> setCapacity(
                                                    context.getSource(),
                                                    EntityArgument.getPlayers( context, "targets" ),
                                                    IntegerArgumentType.getInteger( context, "capacity" )
                                            ) ) ) )
                    );
        }
        
        /** Adds the specified amount of capacity to the target player(s) max absorption. */
        private static int addCapacity( CommandSourceStack source, Collection<ServerPlayer> players, int absorption ) {
            for( ServerPlayer player : players ) {
                double currentMaxAbsorption = AbsorptionHelper.getBaseNaturalAbsorption( player );
                AbsorptionHelper.setBaseNaturalAbsorption( player, false, currentMaxAbsorption + absorption );
            }
            Component message;
            
            if( players.size() == 1 ) {
                message = Component.translatable( References.CMD_CHANGE_CAPACITY_SINGLE, players.iterator().next().getDisplayName() );
            }
            else {
                message = Component.translatable( References.CMD_CHANGE_CAPACITY_MULTIPLE, players.size() );
            }
            source.sendSuccess( () -> message, true );
            return players.size();
        }
        
        /** Subtracts the specified amount of capacity from the target player(s) max absorption. */
        private static int removeCapacity( CommandSourceStack source, Collection<ServerPlayer> players, int absorption ) {
            for( ServerPlayer player : players ) {
                double currentMaxAbsorption = AbsorptionHelper.getBaseNaturalAbsorption( player );
                AbsorptionHelper.setBaseNaturalAbsorption( player, true, currentMaxAbsorption - absorption );
            }
            Component message;
            
            if( players.size() == 1 ) {
                message = Component.translatable( References.CMD_CHANGE_CAPACITY_SINGLE, players.iterator().next().getDisplayName() );
            }
            else {
                message = Component.translatable( References.CMD_CHANGE_CAPACITY_MULTIPLE, players.size() );
            }
            source.sendSuccess( () -> message, true );
            return players.size();
        }
        
        /** Sets the specified amount of max absorption capacity for the target player(s). */
        private static int setCapacity( CommandSourceStack source, Collection<ServerPlayer> players, int absorption ) {
            for( ServerPlayer player : players ) {
                AbsorptionHelper.setBaseNaturalAbsorption( player, true, absorption );
            }
            Component message;
            
            if( players.size() == 1 ) {
                message = Component.translatable( References.CMD_CHANGE_CAPACITY_SINGLE, players.iterator().next().getDisplayName() );
            }
            else {
                message = Component.translatable( References.CMD_CHANGE_CAPACITY_MULTIPLE, players.size() );
            }
            source.sendSuccess( () -> message, true );
            return players.size();
        }
    }
    
    /** Base command for modifying a player's current absorption amount. */
    private static class AbsorptionBaseCommand {
        
        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal( "absorption" )
                    .requires( ( source ) -> source.hasPermission( 2 ) )
                    .then( Commands.literal( "add" )
                            .then( Commands.argument( "targets", EntityArgument.players() )
                                    .then( Commands.argument( "absorption", IntegerArgumentType.integer( 1 ) )
                                            .executes( ( context ) -> addAbsorption(
                                                    context.getSource(),
                                                    EntityArgument.getPlayers( context, "targets" ),
                                                    IntegerArgumentType.getInteger( context, "absorption" )
                                            ) ) ) )
                    )
                    .then( Commands.literal( "remove" )
                            .then( Commands.argument( "targets", EntityArgument.players() )
                                    .then( Commands.argument( "absorption", IntegerArgumentType.integer( 1 ) )
                                            .executes( ( context ) -> removeAbsorption(
                                                    context.getSource(),
                                                    EntityArgument.getPlayers( context, "targets" ),
                                                    IntegerArgumentType.getInteger( context, "absorption" )
                                            ) ) ) )
                    )
                    .then( Commands.literal( "set" )
                            .then( Commands.argument( "targets", EntityArgument.players() )
                                    .then( Commands.argument( "absorption", IntegerArgumentType.integer( 0 ) )
                                            .executes( ( context ) -> setAbsorption(
                                                    context.getSource(),
                                                    EntityArgument.getPlayers( context, "targets" ),
                                                    IntegerArgumentType.getInteger( context, "absorption" )
                                            ) ) ) )
                    );
        }
        
        /** Adds the specified amount of absorption to the target player(s). */
        private static int addAbsorption( CommandSourceStack source, Collection<ServerPlayer> players, int absorption ) {
            for( ServerPlayer player : players ) {
                float currentAbsorption = player.getAbsorptionAmount();
                player.setAbsorptionAmount( currentAbsorption + Math.min( (float) absorption, Float.MAX_VALUE ) );
            }
            Component message;
            
            if( players.size() == 1 ) {
                message = Component.translatable( References.CMD_CHANGE_ABSORPTION_SINGLE, players.iterator().next().getDisplayName() );
            }
            else {
                message = Component.translatable( References.CMD_CHANGE_ABSORPTION_MULTIPLE, players.size() );
            }
            source.sendSuccess( () -> message, true );
            return players.size();
        }
        
        /** Subtracts the specified amount of absorption from the target player(s). */
        private static int removeAbsorption( CommandSourceStack source, Collection<ServerPlayer> players, int absorption ) {
            for( ServerPlayer player : players ) {
                float currentAbsorption = player.getAbsorptionAmount();
                player.setAbsorptionAmount( Math.max( 0.0F, currentAbsorption - Math.min( (float) absorption, Float.MAX_VALUE ) ) );
            }
            Component message;
            
            if( players.size() == 1 ) {
                message = Component.translatable( References.CMD_CHANGE_ABSORPTION_SINGLE, players.iterator().next().getDisplayName() );
            }
            else {
                message = Component.translatable( References.CMD_CHANGE_ABSORPTION_MULTIPLE, players.size() );
            }
            source.sendSuccess( () -> message, true );
            return players.size();
        }
        
        /** Sets the specified amount of absorption for the target player(s). */
        private static int setAbsorption( CommandSourceStack source, Collection<ServerPlayer> players, int absorption ) {
            for( ServerPlayer player : players ) {
                player.setAbsorptionAmount( Math.min( (float) absorption, Float.MAX_VALUE ) );
            }
            Component message;
            
            if( players.size() == 1 ) {
                message = Component.translatable( References.CMD_CHANGE_ABSORPTION_SINGLE, players.iterator().next().getDisplayName() );
            }
            else {
                message = Component.translatable( References.CMD_CHANGE_ABSORPTION_MULTIPLE, players.size() );
            }
            source.sendSuccess( () -> message, true );
            return players.size();
        }
    }
}
