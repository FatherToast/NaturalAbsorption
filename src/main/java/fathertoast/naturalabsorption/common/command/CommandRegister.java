package fathertoast.naturalabsorption.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;

public class CommandRegister {

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();

        NABaseCommand.register(dispatcher);
    }
}
