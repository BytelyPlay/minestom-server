package org.hyperoil.playifkillers.ControlActions;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.registry.RegistryData;
import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.hyperoil.playifkillers.Utils.ActionAllowed;
import org.hyperoil.playifkillers.Utils.Enums.Action;
import org.hyperoil.playifkillers.Utils.SummonVanillaEntity;
import org.jetbrains.annotations.NotNull;

public class SpawnEggs {
    private final ActionAllowed rules;
    public void blockInteract(@NotNull PlayerBlockInteractEvent event) {
        CPlayer p = CPlayer.getCPlayer(event.getPlayer());
        if (rules.getShouldDeny(p, Action.USE_SPAWN_EGGS)) return;

        Instance inst = p.getInstance();

        ItemStack holding = p.getItemInMainHand();
        if (holding.isAir()) return;
        Material holdingMaterial = holding.material();
        RegistryData.MaterialEntry entry = holdingMaterial.registry();
        EntityType spawnEntityType = entry.spawnEntityType();
        if (spawnEntityType == null) return;

        BlockVec clickedBlock = event.getBlockPosition();

        SummonVanillaEntity.summonEntity(spawnEntityType, clickedBlock.asPos().add(0, 1, 0), inst);

        ItemStack afterStack = holding.consume(1);
        p.setItemInMainHand(afterStack);
    }

    public SpawnEggs(@NotNull ActionAllowed allowed) {
        rules = allowed;
    }
}
