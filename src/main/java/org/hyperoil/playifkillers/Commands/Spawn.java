package org.hyperoil.playifkillers.Commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;
import org.hyperoil.playifkillers.Utils.ICommand;
import org.jetbrains.annotations.Nullable;

public class Spawn implements ICommand {
    @Override
    public int execute(CommandContext<CommandSender> context) {
        Integer entityCount = context.getArgument("amount", Integer.class);
        CommandSender sender = context.getSource();
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may execute this command.");
            return 1;
        }
        Player p = (Player) sender;
        String entityName = context.getArgument("entity", String.class);

        /* if (entityName.equals("hyperoil:zombie")) {
            for (int i = 0; i < entityCount; i++) {
                LivingEntity zombie = new Zombie();
                zombie.setInstance(lobby, p.getPosition());
            }
        } else {
            p.sendMessage("Please supply a VALID entity type...");
        } */
        return 1;
    }
    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public @Nullable LiteralArgumentBuilder<CommandSender> changeArguments(LiteralArgumentBuilder<CommandSender> arguments) {
        return arguments
                .then(RequiredArgumentBuilder.<CommandSender, String>argument("entity", StringArgumentType.string())
                        .then(RequiredArgumentBuilder.<CommandSender, Integer>argument("amount", IntegerArgumentType.integer(1))
                                .executes(this::execute)));
    }
}
