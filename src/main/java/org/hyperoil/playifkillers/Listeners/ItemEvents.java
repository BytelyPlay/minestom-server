package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;

public class ItemEvents {
    public static void onPickUpItemEvent(PickupItemEvent event) {
        LivingEntity livingEntity = event.getLivingEntity();
        if (livingEntity instanceof Player p) {
            PlayerInventory playerInventory = p.getInventory();
            ItemEntity itemEntity = event.getItemEntity();
            ItemStack itemStack = itemEntity.getItemStack();
            playerInventory.addItemStack(itemStack);
        }
    }
}
