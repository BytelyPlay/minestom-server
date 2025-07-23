package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.entity.Entity;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import org.hyperoil.playifkillers.NPCs.NPC;
import org.jetbrains.annotations.NotNull;

public class InteractNPC {

    public static void playerEntityInteract(@NotNull PlayerEntityInteractEvent event) {
        Entity entity = event.getTarget();

        if (entity instanceof NPC npc) {
            npc.playerInteract(event);
        }
    }

    public static void entityAttack(@NotNull EntityAttackEvent event) {
        Entity target = event.getTarget();

        if (target instanceof NPC npc) {
            npc.playerAttack(event);
        }
    }
}
