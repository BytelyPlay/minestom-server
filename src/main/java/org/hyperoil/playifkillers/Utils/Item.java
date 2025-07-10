package org.hyperoil.playifkillers.Utils;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Item {
    void rightClick(@NotNull PlayerBlockInteractEvent e);
    void rightClick(@NotNull PlayerEntityInteractEvent e);
    void rightClick(@NotNull PlayerUseItemEvent e);
    void punch(@NotNull EntityAttackEvent e);
    ItemStack getItemStack();
}
