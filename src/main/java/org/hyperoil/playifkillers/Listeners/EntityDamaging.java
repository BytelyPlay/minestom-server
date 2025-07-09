package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import org.hyperoil.playifkillers.Entities.HealthDisplayArmorStand;
import org.hyperoil.playifkillers.Main;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class EntityDamaging {
    // Eventually after adding mobs we will deny any attacking between players.
    private static HashMap<UUID, Long> lastDamaged = new HashMap<>();
    public static void attack(@NotNull EntityAttackEvent e) {
        Entity entity = e.getEntity();
        Entity target = e.getTarget();
        if (target instanceof LivingEntity living) {
            long lastDamage = lastDamaged.getOrDefault(living.getUuid(), 0L);
            if (Main.overWorld.getTime() - lastDamage < 10) return;

            lastDamaged.put(living.getUuid(), Main.overWorld.getTime());
            living.damage(Damage.fromEntity(entity, 1f));
        } else {
            if (!(target instanceof HealthDisplayArmorStand)) {
                target.remove();
            }
        }
    }
    public static void clearLastDamaged() {
        lastDamaged.clear();
    }
}
