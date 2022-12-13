package fathertoast.naturalabsorption.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import fathertoast.naturalabsorption.common.hearts.AbsorptionHelper;
import fathertoast.naturalabsorption.common.util.References;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

public class NABaseCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("naturalabsorption")
                .then(AbsorptionBaseCommand.register()));
    }

    /**
     * Base command for all difficulty related subcommands.
     */
    private static class AbsorptionBaseCommand {

        private static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("absorption")
                    .requires((source) -> source.hasPermission(3))
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

        private static int addAbsorption(CommandSource source, Collection<ServerPlayerEntity> players, int absorption) {
            for (ServerPlayerEntity player  : players) {
                double currentMaxAbsorption = AbsorptionHelper.getMaxAbsorption(player);
                AbsorptionHelper.setBaseNaturalAbsorption(player, false, currentMaxAbsorption + absorption);
            }
            TranslationTextComponent message;

            if (players.size() == 1) {
                message = new TranslationTextComponent(References.CMD_CHANGE_ABSORPTION_SINGLE, players.iterator().next().getDisplayName());
            }
            else {
                message = new TranslationTextComponent(References.CMD_CHANGE_ABSORPTION_MULTIPLE, players.size());
            }
            source.sendSuccess(message, true);
            return players.size();
        }

        private static int removeAbsorption(CommandSource source, Collection<ServerPlayerEntity> players, int absorption) {
            for (ServerPlayerEntity player  : players) {
                double currentMaxAbsorption = AbsorptionHelper.getMaxAbsorption(player);
                AbsorptionHelper.setBaseNaturalAbsorption(player, true, currentMaxAbsorption - absorption);
            }
            TranslationTextComponent message;

            if (players.size() == 1) {
                message = new TranslationTextComponent(References.CMD_CHANGE_ABSORPTION_SINGLE, players.iterator().next().getDisplayName());
            }
            else {
                message = new TranslationTextComponent(References.CMD_CHANGE_ABSORPTION_MULTIPLE, players.size());
            }
            source.sendSuccess(message, true);
            return players.size();
        }
    }
}
