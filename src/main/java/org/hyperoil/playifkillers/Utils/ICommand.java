package org.hyperoil.playifkillers.Utils;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minestom.server.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public interface ICommand {
    int execute(CommandContext<CommandSender> context);
    String getName();
    @Nullable LiteralArgumentBuilder<CommandSender> changeArguments(LiteralArgumentBuilder<CommandSender> arguments);
}
