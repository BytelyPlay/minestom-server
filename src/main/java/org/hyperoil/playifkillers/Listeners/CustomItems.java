package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import org.hyperoil.playifkillers.Utils.Item;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public class CustomItems {
    // TODO: replace with something competent.
    private static ConcurrentHashMap<ItemStack, Item> customItems = new ConcurrentHashMap<>();
    public static void punch(@NotNull EntityAttackEvent event) {
        Entity entity = event.getEntity();
        // only thing with a inventory ATM later we would check if it's a player or something that extends InventoryHolder which we will make...
        if (entity instanceof Player p) {
           Item item = customItems.get(p.getItemInMainHand());
           if (item == null) return;
           item.punch(event);
        }
    }

    public static void entityInteract(@NotNull PlayerEntityInteractEvent e) {
        Player p = e.getEntity();
        // only thing with a inventory ATM later we would check if it's a player or something that extends InventoryHolder which we will make...
        Item item = customItems.get(p.getItemInMainHand());
        if (item == null) return;
        item.rightClick(e);
    }

    public static void blockInteract(@NotNull PlayerBlockInteractEvent e) {
        Player p = e.getEntity();
        // only thing with a inventory ATM later we would check if it's a player or something that extends InventoryHolder which we will make...
        Item item = customItems.get(p.getItemInMainHand());
        if (item == null) return;
        item.rightClick(e);
    }

    public static void registerItem(Item item) {
        customItems.put(item.getItemStack(), item);
    }

    public static void useItem(@NotNull PlayerUseItemEvent e) {
        Player p = e.getEntity();
        // only thing with a inventory ATM later we would check if it's a player or something that extends InventoryHolder which we will make...
        Item item = customItems.get(p.getItemInMainHand());
        if (item == null) return;
        item.rightClick(e);
    }
}
