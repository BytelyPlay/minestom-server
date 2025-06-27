package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;

import static org.hyperoil.playifkillers.Main.SPAWN_POINT;
import static org.hyperoil.playifkillers.Main.overWorld;

public class JoinPlayerSetup {
    public static void onAsyncPlayerConfigurationEvent(AsyncPlayerConfigurationEvent event) {
        final Player p = event.getPlayer();
        event.setSpawningInstance(overWorld);
        p.setRespawnPoint(SPAWN_POINT);
    }
}
