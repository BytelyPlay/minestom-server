package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerCommandEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import org.hyperoil.playifkillers.Utils.Box;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class CommandParser {
    private final ExecutorService service;
    private final InstanceContainer container;

    public CommandParser(ExecutorService executor, InstanceContainer contain) {
        service = executor;
        container = contain;
    }

    public void onPlayerCommandEvent(PlayerCommandEvent event) {
        parseAdminCommands(event);
    }

    private void parseAdminCommands(PlayerCommandEvent event) {
        final String command = event.getCommand();
        final Player p = event.getPlayer();
        UUID playerUUID = p.getUuid();
        String[] commandSplit = command.split(" ");
        if (!playerUUID.toString().equals("bcbaabb3-f21a-4927-94ad-2979c54f67fc")) return;
        if (command.equals("gmc")) {
            p.setGameMode(GameMode.CREATIVE);
        } else if (command.equals("gms")) {
            p.setGameMode(GameMode.SURVIVAL);
        } else if (command.startsWith("fill")) {
            if (commandSplit.length == 8) {
                service.submit(() -> fillCommandHandler(commandSplit, p));
            } else {
                p.sendMessage("Please supply at least 7 arguments.");
            }
        } else {
            p.sendMessage("Unknown Command.");
        }
    }

    private void fillCommandHandler(String[] commandSplit, Player p) {
        if (commandSplit.length != 8) p.sendMessage("Please Provide 7 arguments: /fill <x1> <y1> <z1> <x2> <y2> <z2>");
        Box box = getBoxFromFillCommand(p, commandSplit);
        Block block = getBlockFromFillCommand(commandSplit);
        if (box == null) return;
        if (block == null) {
            p.sendMessage("Please provide a valid block.");
            return;
        }
        for (BlockVec vec : box.getAllBlocks(container)) {
            container.setBlock(vec, block);
        }
    }

    private Box getBoxFromFillCommand(Player p, String[] commandSplit) {
        if (commandSplit.length == 8) {
            int[] coordinatesSplit = new int[6];
            int coordSplitCounter = 0;
            for (int i = 1; i < 7; i++) {
                String on = commandSplit[i];
                try {
                    coordinatesSplit[coordSplitCounter] = Integer.parseInt(on);
                    coordSplitCounter++;
                } catch (NumberFormatException e) {
                    p.sendMessage("Invalid number. on argument " + (i + 1));
                    return null;
                }
            }
            return new Box(coordinatesSplit[0],
                    coordinatesSplit[1],
                    coordinatesSplit[2],
                    coordinatesSplit[3],
                    coordinatesSplit[4],
                    coordinatesSplit[5]);
        }
        return null;
    }
    private Block getBlockFromFillCommand(String[] commandSplit) {
        return Block.fromKey(commandSplit[7]);
    }
}