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

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("naturalabsorption")
                .then(AbsorptionBaseCommand.register()));
    }

    /**
     * Base command for all difficulty related subcommands.
     */
    private static class AbsorptionBaseCommand {

        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("absorption")
                    .requires((source) -> source.hasPermission(2))
                    .then(Commands.literal("add")
                            .then(Commands.argument("targets", EntityArgument.players())
                                    .then(Commands.argument("absorption", IntegerArgumentType.integer(1))
                                            .executes((context) -> addAbsorption(context.getSource(), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "absorption")))))
                    )
                    .then(Commands.literal("remove")
                            .then(Commands.argument("targets", EntityArgument.players())
                                    .then(Commands.argument("absorption", IntegerArgumentType.integer(1))
                                            .executes((context) -> removeAbsorption(context.getSource(), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "absorption")))))
                    );
        }

        private static int addAbsorption(CommandSourceStack source, Collection<ServerPlayer> players, int absorption) {
            for (ServerPlayer player  : players) {
                double currentMaxAbsorption = AbsorptionHelper.getBaseNaturalAbsorption(player);
                AbsorptionHelper.setBaseNaturalAbsorption(player, false, currentMaxAbsorption + absorption);
            }
            Component message;

            if (players.size() == 1) {
                message = Component.translatable(References.CMD_CHANGE_ABSORPTION_SINGLE, players.iterator().next().getDisplayName());
            }
            else {
                message = Component.translatable(References.CMD_CHANGE_ABSORPTION_MULTIPLE, players.size());
            }
            source.sendSuccess(() -> message, true);
            return players.size();
        }

        private static int removeAbsorption(CommandSourceStack source, Collection<ServerPlayer> players, int absorption) {
            for (ServerPlayer player  : players) {
                double currentMaxAbsorption = AbsorptionHelper.getBaseNaturalAbsorption(player);
                AbsorptionHelper.setBaseNaturalAbsorption(player, true, currentMaxAbsorption - absorption);
            }
            Component message;

            if (players.size() == 1) {
                message = Component.translatable(References.CMD_CHANGE_ABSORPTION_SINGLE, players.iterator().next().getDisplayName());
            }
            else {
                message = Component.translatable(References.CMD_CHANGE_ABSORPTION_MULTIPLE, players.size());
            }
            source.sendSuccess(() -> message, true);
            return players.size();
        }
    }
}
