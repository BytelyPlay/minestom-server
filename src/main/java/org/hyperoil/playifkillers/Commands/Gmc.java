package org.hyperoil.playifkillers.Commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import org.hyperoil.playifkillers.Permissions.User;
import org.hyperoil.playifkillers.Utils.ICommand;
import org.jetbrains.annotations.Nullable;

public class Gmc implements ICommand {
    @Override
    public int execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        if (sender instanceof Player p) {
            User user = User.getUser(p.getUuid());
            if (!user.hasPermission("hyperoil.gmc.self")) {
                p.sendMessage("You are not permitted to do this.");
                return 1;
            }
            p.setGameMode(GameMode.CREATIVE);
            p.sendMessage("Done.");
        } else {
            sender.sendMessage("Only players may do this.");
        }
        return 1;
    }

    @Override
    public String getName() {
        return "gmc";
    }

    @Override
    public @Nullable LiteralArgumentBuilder<CommandSender> changeArguments(LiteralArgumentBuilder<CommandSender> arguments) {
        return null;
    }
}
