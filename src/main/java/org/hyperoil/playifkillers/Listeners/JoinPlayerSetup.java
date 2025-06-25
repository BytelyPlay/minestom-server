package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import org.jetbrains.annotations.NotNull;

import static org.hyperoil.playifkillers.Main.SPAWN_POINT;
import static org.hyperoil.playifkillers.Main.overWorld;

public class JoinPlayerSetup {
    private static final Point SPAWN_POINT = new Vec(25, 51, 25);
    public static void onAsyncPlayerConfigurationEvent(AsyncPlayerConfigurationEvent event) {
        final Player p = event.getPlayer();
        event.setSpawningInstance(overWorld);
        p.setRespawnPoint(new Pos(SPAWN_POINT));
    }
}
