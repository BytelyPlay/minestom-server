package org.hyperoil.playifkillers.ControlActions;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.entity.EntityAttackEvent;
import org.hyperoil.playifkillers.Main;
import org.hyperoil.playifkillers.Utils.ActionAllowed;
import org.hyperoil.playifkillers.Utils.Enums.Action;
import org.hyperoil.playifkillers.Utils.Enums.RuleValue;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class EntityDamaging {
    private static HashMap<UUID, Long> lastDamaged = new HashMap<>();
    public static void attack(@NotNull EntityAttackEvent e) {
        Entity entity = e.getEntity();
        Entity target = e.getTarget();
        if (target instanceof LivingEntity living) {
            if (entity instanceof Player playerAttacker) {
                if (target instanceof Player) {
                    if (!ActionAllowed.getShouldAllow(playerAttacker, Action.PVP)) return;
                } else {
                    if (!ActionAllowed.getShouldAllow(playerAttacker, Action.PVE)) return;
                }
            }
            long lastDamage = lastDamaged.getOrDefault(living.getUuid(), 0L);
            if (Main.overWorld.getTime() - lastDamage < 10) return;

            lastDamaged.put(living.getUuid(), Main.overWorld.getTime());
            living.damage(Damage.fromEntity(entity, 1f));
        }
    }
    public static void clearLastDamaged() {
        lastDamaged.clear();
    }
}
