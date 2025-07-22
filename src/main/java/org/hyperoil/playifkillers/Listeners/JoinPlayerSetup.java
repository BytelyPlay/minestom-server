package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.Instance;
import org.hyperoil.playifkillers.Main;
import org.hyperoil.playifkillers.Minestom.CPlayer;

import static org.hyperoil.playifkillers.Main.LOBBY_SPAWN_POINT;

public class JoinPlayerSetup {
    public static void onAsyncPlayerConfigurationEvent(AsyncPlayerConfigurationEvent event) {
        Instance lobby = Main.getInstance().getLobby();
        final CPlayer p = CPlayer.getCPlayer(event.getPlayer());
        event.setSpawningInstance(lobby);
        p.setRespawnPoint(LOBBY_SPAWN_POINT);
    }
}
