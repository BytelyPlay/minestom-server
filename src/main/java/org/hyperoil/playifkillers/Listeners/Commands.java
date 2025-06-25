package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Commands {
    public static void onPlayerCommandEvent(PlayerCommandEvent event) {
        final String command = event.getCommand();
        final Player p = event.getPlayer();
        UUID playerUUID = p.getUuid();
        if (!playerUUID.toString().equals("bcbaabb3-f21a-4927-94ad-2979c54f67fc")) return;
        if (command.equals("gmc")) {
            p.setGameMode(GameMode.CREATIVE);
        } else if (command.equals("gms")) {
            p.setGameMode(GameMode.SURVIVAL);
        }
    }
}
