package org.hyperoil.playifkillers.Commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import org.hyperoil.playifkillers.Permissions.User;
import org.hyperoil.playifkillers.Utils.Box;
import org.hyperoil.playifkillers.Utils.ChatColor;
import org.hyperoil.playifkillers.Utils.ICommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;

public class Fill implements ICommand {
    private final InstanceContainer container;
    // MIGHT ADD IT SO KEEPING IT.
    private final ExecutorService service;

    public Fill(InstanceContainer contain, ExecutorService executorService) {
        container = contain;
        service = executorService;
    }
    @Override
    public int execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        if (sender instanceof Player p) {
            User user = new User(p.getUuid());
            if (!user.checkPermission("hyperoil.fill")) {
                p.sendMessage(ChatColor.RED + "You are not permitted to do that.");
                return 1;
            }
        }
        Box box = new Box(
                context.getArgument("x1", Integer.class),
                context.getArgument("y1", Integer.class),
                context.getArgument("z1", Integer.class),
                context.getArgument("x2", Integer.class),
                context.getArgument("y2", Integer.class),
                context.getArgument("z2", Integer.class)
        );
        Block block = Block.fromKey(context.getArgument("block", String.class));
        if (block == null) {
            sender.sendMessage("Please provide a valid block.");
            return 1;
        }
        for (BlockVec vec : box.getAllBlocks(container)) {
            container.setBlock(vec, block);
        }
        sender.sendMessage("Done...");
        return 1;
    }

    @Override
    public String getName() {
        return "fill";
    }

    @Override
    public @Nullable LiteralArgumentBuilder<CommandSender> changeArguments(LiteralArgumentBuilder<CommandSender> arguments) {
        return LiteralArgumentBuilder.<CommandSender>literal("fill")
                .then(RequiredArgumentBuilder.<CommandSender, Integer>argument("x1", IntegerArgumentType.integer())
                        .then(RequiredArgumentBuilder.<CommandSender, Integer>argument("y1", IntegerArgumentType.integer())
                                .then(RequiredArgumentBuilder.<CommandSender, Integer>argument("z1", IntegerArgumentType.integer())
                                        .then(RequiredArgumentBuilder.<CommandSender, Integer>argument("x2", IntegerArgumentType.integer())
                                                .then(RequiredArgumentBuilder.<CommandSender, Integer>argument("y2", IntegerArgumentType.integer())
                                                        .then(RequiredArgumentBuilder.<CommandSender, Integer>argument("z2", IntegerArgumentType.integer())
                                                                .then(RequiredArgumentBuilder.<CommandSender, String>argument("block", StringArgumentType.string())
                                                                        .executes(this::execute)
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                );
    }
}
