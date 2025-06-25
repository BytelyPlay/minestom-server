package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerCommandEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import org.hyperoil.playifkillers.Main;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Commands {
    public static void onPlayerCommandEvent(PlayerCommandEvent event) {
        parseAdminCommands(event);
    }
    private static void parseAdminCommands(PlayerCommandEvent event) {
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
                for (int i = 1; i<7; i++) {
                    String on = commandSplit[i];
                    try {
                        coordinatesSplit[coordSplitCounter] = Integer.parseInt(on);
                        coordSplitCounter++;
                    } catch (NumberFormatException e) {
                        p.sendMessage("Invalid number. on argument " + i + 1);
                        return;
                    }
                }
                Main.executorService.submit(() -> {
                    for (int x = coordinatesSplit[0]; x <= coordinatesSplit[3]; x++) {
                        for (int y = coordinatesSplit[1]; y <= coordinatesSplit[4]; y++) {
                            for (int z = coordinatesSplit[2]; z <= coordinatesSplit[5]; z++) {
                                InstanceContainer world = Main.overWorld;
                                Chunk chunk = world.getChunk(x >> 4, z >> 4);
                                if (chunk != null && chunk.isLoaded()) {
                                    world.setBlock(x, y, z, block);
                                }
                                if (block != Block.AIR) {
                                    Main.blocksSaved.put(new BlockVec(x, y, z), block);
                                } else {
                                    Main.blocksSaved.remove(new BlockVec(x, y, z));
                                }
                            }
                        }
                    }
                });
            } else {
                p.sendMessage("Please supply at least 7 arguments");
            }
        } else {
            p.sendMessage("Unknown Command.");
        }
    }
}
