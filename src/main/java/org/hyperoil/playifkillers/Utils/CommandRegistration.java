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
        registerCommand(cmd);
    }
    public static void registerCommands(CommandDispatcher<CommandSender> cmdDispatcher) {
        dispatcher=cmdDispatcher;
        for (ICommand cmd : queue) {
            registerCommand(cmd);
        }

        /* MinecraftServer.getPacketListenerManager().setPlayListener(ClientTabCompletePacket.class, ((packet, player) -> {
            CPlayer cp = CPlayer.getCPlayer(player);

            ParseResults<CommandSender> parseResults = dispatcher.parse(packet.text(), cp);

            CompletableFuture<Suggestions> future = dispatcher.getCompletionSuggestions(parseResults);

            try {
                Suggestions suggestions = future.get();
                List<Suggestion> suggestionList = suggestions.getList();

                ArrayList<TabCompletePacket.Match> matches = new ArrayList<>();

                for (Suggestion suggestion : suggestionList) {
                    matches.add(new TabCompletePacket.Match(suggestion.getText(), Component.empty()));
                }

                StringRange range = suggestions.getRange();

                cp.sendUninterceptedPacket(new TabCompletePacket(packet.transactionId(), range.getStart(),range.getLength(), matches));
            } catch (Exception e) {
                e.printStackTrace();
            }
        })); */

    }
    private static void registerCommand(ICommand cmd) {
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
