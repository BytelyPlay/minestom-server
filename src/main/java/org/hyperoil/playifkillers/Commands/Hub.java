package org.hyperoil.playifkillers.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minestom.server.command.CommandSender;
import net.minestom.server.instance.Instance;
import org.hyperoil.playifkillers.Main;
import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.hyperoil.playifkillers.Utils.ICommand;
import org.jetbrains.annotations.Nullable;

public class Hub implements ICommand {
    @Override
    public int execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();

        if (sender instanceof CPlayer p) {
            Instance lobby = Main.getInstance().getLobby();
            if (p.getInstance() == lobby) {
                p.teleport(Main.LOBBY_SPAWN_POINT);
            } else {
                p.setInstance(lobby, Main.LOBBY_SPAWN_POINT);
            }
        } else {
            sender.sendMessage("You need to be a player to do this.");
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public String getName() {
        return "hub";
    }

    @Override
    public @Nullable LiteralArgumentBuilder<CommandSender> changeArguments(LiteralArgumentBuilder<CommandSender> arguments) {
        return null;
    }
}
