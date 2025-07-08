package org.hyperoil.playifkillers.Utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minestom.server.command.CommandSender;

import java.util.ArrayList;

public class CommandRegistration {
    private static ArrayList<ICommand> queue = new ArrayList<>();
    private static CommandDispatcher<CommandSender> dispatcher;
    public static void register(ICommand cmd) {
        if (dispatcher == null) {
            queue.add(cmd);
            return;
        }
        registerCommand(dispatcher, cmd);
    }
    public static void registerCommands(CommandDispatcher<CommandSender> cmdDispatcher) {
        dispatcher=cmdDispatcher;
        for (ICommand cmd : queue) {
            registerCommand(cmdDispatcher, cmd);
        }
    }
    private static void registerCommand(CommandDispatcher<CommandSender> cmdDispatcher, ICommand cmd) {
        LiteralArgumentBuilder<CommandSender> beforeArguments = LiteralArgumentBuilder.
                literal(cmd.getName());
        LiteralArgumentBuilder<CommandSender> afterArguments = cmd.changeArguments(beforeArguments);
        if (afterArguments == null) {
            afterArguments = beforeArguments;
            afterArguments = afterArguments.executes(cmd::execute);
        }
        dispatcher.register(afterArguments);
    }
}
