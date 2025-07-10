package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.Instance;
import org.hyperoil.playifkillers.Entities.HealthDisplayArmorStand;
import org.hyperoil.playifkillers.Items.Hyperion;
import org.hyperoil.playifkillers.Main;

import static org.hyperoil.playifkillers.Main.SPAWN_POINT;
import static org.hyperoil.playifkillers.Main.overWorld;

public class JoinPlayerSetup {
    public static void onAsyncPlayerConfigurationEvent(AsyncPlayerConfigurationEvent event) {
        final Player p = event.getPlayer();
        event.setSpawningInstance(overWorld);
        p.setRespawnPoint(SPAWN_POINT);

        Entity e = new HealthDisplayArmorStand(p);
        e.setInstance(overWorld);
        e.spawn();

        Hyperion hyperion = new Hyperion();
        p.getInventory().addItemStack(hyperion.getItemStack());
        CustomItems.registerItem(hyperion);
    }
}
