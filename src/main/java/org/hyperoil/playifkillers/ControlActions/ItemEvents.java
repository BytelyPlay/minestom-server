package org.hyperoil.playifkillers.ControlActions;

import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.hyperoil.playifkillers.Utils.ActionAllowed;
import org.hyperoil.playifkillers.Utils.Enums.Action;

public class ItemEvents {
    public static void onPickUpItemEvent(PickupItemEvent event) {
        LivingEntity livingEntity = event.getLivingEntity();
        if (livingEntity instanceof CPlayer p) {
            if (ActionAllowed.getShouldDeny(p, Action.ITEM_PICKUP)) return;
            PlayerInventory playerInventory = p.getInventory();
            ItemEntity itemEntity = event.getItemEntity();
            ItemStack itemStack = itemEntity.getItemStack();
            playerInventory.addItemStack(itemStack);
        }
    }
}
