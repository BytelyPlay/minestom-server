package org.hyperoil.playifkillers.Commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minestom.server.command.CommandSender;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.hyperoil.playifkillers.Utils.Box;
import org.hyperoil.playifkillers.Utils.CommandRegistration;
import org.hyperoil.playifkillers.Utils.Enums.ChatColor;
import org.hyperoil.playifkillers.Utils.Enums.Permission;
import org.hyperoil.playifkillers.Utils.ICommand;
import org.jetbrains.annotations.Nullable;

public class SetBlock implements ICommand {
    @Override
    public int execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        if (sender instanceof CPlayer p) {
            if (!p.hasPermission(Permission.SETBLOCK_COMMAND)) {
                p.sendMessage(ChatColor.RED + "No permissions...");
                return 1;
            }
            Instance inst = p.getInstance();
            int x = context.getArgument("x", Integer.class);
            int y = context.getArgument("y", Integer.class);
            int z = context.getArgument("z", Integer.class);
            String blockString = context.getArgument("block", String.class);

            Block block = Block.fromKey(blockString);
            if (block == null) {
                p.sendMessage("Provide a valid block please.");
                return 1;
            }
            inst.setBlock(x, y, z, block);
            p.sendMessage(ChatColor.GREEN + "Done!");
        }
        return 1;
    }

    @Override
    public String getName() {
        return "setblock";
    }

    @Override
    public @Nullable LiteralArgumentBuilder<CommandSender> changeArguments(LiteralArgumentBuilder<CommandSender> arguments) {
        return LiteralArgumentBuilder.<CommandSender>literal("setblock")
                .then(
                        RequiredArgumentBuilder.<CommandSender, Integer>argument("x", IntegerArgumentType.integer())
                                .then(
                                        RequiredArgumentBuilder.<CommandSender, Integer>argument("y", IntegerArgumentType.integer())
                                                .then(
                                                        RequiredArgumentBuilder.<CommandSender, Integer>argument("z", IntegerArgumentType.integer())
                                                                .then(
                                                                        RequiredArgumentBuilder.<CommandSender, String>argument("block", StringArgumentType.string())
                                                                                .executes(this::execute)
                                                                )
                                                )
                                )
                );
    }
}
