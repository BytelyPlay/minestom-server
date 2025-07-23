package org.hyperoil.playifkillers.ControlActions;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.instance.Instance;
import org.hyperoil.playifkillers.Main;
import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.hyperoil.playifkillers.Utils.ActionAllowed;
import org.hyperoil.playifkillers.Utils.Enums.Action;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class EntityDamaging {
    private final HashMap<UUID, Long> lastDamaged = new HashMap<>();
    private final ActionAllowed rules;
    public void attack(@NotNull EntityAttackEvent e) {
        Instance lobby = Main.getInstance().getLobby();
        Entity entity = e.getEntity();
        Entity target = e.getTarget();
        if (target instanceof LivingEntity living) {
            if (entity instanceof CPlayer playerAttacker) {
                if (target instanceof CPlayer) {
                    if (rules.getShouldDeny(playerAttacker, Action.PVP)) return;
                } else {
                    if (rules.getShouldDeny(playerAttacker, Action.PVE)) return;
                }
            }
            long lastDamage = lastDamaged.getOrDefault(living.getUuid(), 0L);
            if (lobby.getTime() - lastDamage < 10) return;

            lastDamaged.put(living.getUuid(), lobby.getTime());
            living.damage(Damage.fromEntity(entity, 1f));
        }
    }
    public void clearLastDamaged() {
        lastDamaged.clear();
    }

    public EntityDamaging(ActionAllowed allowed) {
        rules = allowed;
    }
}
