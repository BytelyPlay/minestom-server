package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerCommandEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import org.hyperoil.playifkillers.Main;
import org.hyperoil.playifkillers.Utils.Box;
import org.jspecify.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class Commands {
    private final ExecutorService service;
    private final InstanceContainer container;

    public Commands(ExecutorService executor, InstanceContainer contain) {
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
        if (!playerUUID.toString().equals("bcbaabb3-f21a-4927-94ad-2979c54f67fc")) return;
        if (command.equals("gmc")) {
            p.setGameMode(GameMode.CREATIVE);
        } else if (command.equals("gms")) {
            p.setGameMode(GameMode.SURVIVAL);
        } else if (command.startsWith("fill")) {
            String[] commandSplit = command.split(" ");
            if (commandSplit.length == 8) {
                Block block = Block.fromKey(commandSplit[7]);
                if (block == null) {
                    p.sendMessage(commandSplit[7] + " is not a valid block.");
                    return;
                }
                int[] coordinatesSplit = new int[6];
                int coordSplitCounter = 0;
                for (int i = 1; i < 7; i++) {
                    String on = commandSplit[i];
                    try {
                        coordinatesSplit[coordSplitCounter] = Integer.parseInt(on);
                        coordSplitCounter++;
                    } catch (NumberFormatException e) {
                        p.sendMessage("Invalid number. on argument " + i + 1);
                        return;
                    }
                }
                service.submit(() -> {
                    fillCommandHandler(new Box(coordinatesSplit[0],
                            coordinatesSplit[1],
                            coordinatesSplit[2],
                            coordinatesSplit[3],
                            coordinatesSplit[4],
                            coordinatesSplit[5]), block);
                });
            } else {
                p.sendMessage("Please supply at least 7 arguments");
            }
        } else {
            p.sendMessage("Unknown Command.");
        }
    }

    private void fillCommandHandler(Box box, Block block) {
        InstanceContainer container = Main.overWorld;
        for (BlockVec vec : box.getAllBlocks(Main.overWorld)) {
            container.setBlock(vec, block);
        }
    }

    private Box getBoxFromCommand(Player p, String[] commandSplit) {
        if (commandSplit.length == 7) {
            int[] coordinatesSplit = new int[6];
            int coordSplitCounter = 0;
            for (int i = 1; i < 7; i++) {
                String on = commandSplit[i];
                try {
                    coordinatesSplit[coordSplitCounter] = Integer.parseInt(on);
                    coordSplitCounter++;
                } catch (NumberFormatException e) {
                    p.sendMessage("Invalid number. on argument " + i + 1);
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
}