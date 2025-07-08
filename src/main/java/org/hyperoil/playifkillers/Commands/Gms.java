package org.hyperoil.playifkillers.Commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import org.hyperoil.playifkillers.Utils.ICommand;
import org.jetbrains.annotations.Nullable;

public class Gms implements ICommand {
    @Override
    public int execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        if (sender instanceof Player p) {
            if (!p.getUuid().toString().equals("bcbaabb3-f21a-4927-94ad-2979c54f67fc")) {
                p.sendMessage("You are not permitted to do this.");
                return 1;
            }
            p.setGameMode(GameMode.SURVIVAL);
            p.sendMessage("Done.");
        } else {
            sender.sendMessage("Only players may do this.");
        }
        return 1;
    }

    @Override
    public String getName() {
        return "gms";
    }

    @Override
    public @Nullable LiteralArgumentBuilder<CommandSender> changeArguments(LiteralArgumentBuilder<CommandSender> arguments) {
        return null;
    }
}
