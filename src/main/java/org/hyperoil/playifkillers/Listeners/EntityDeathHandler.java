package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.entity.Entity;
import net.minestom.server.event.entity.EntityDeathEvent;
import org.hyperoil.playifkillers.Main;
import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EntityDeathHandler {
    public static void death(@NotNull EntityDeathEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof CPlayer) return;
        ScheduledExecutorService service = Main.getInstance().getExecutorService();
        service.schedule(entity::remove, 1, TimeUnit.SECONDS);
    }
}
