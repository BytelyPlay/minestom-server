package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockControl {
    public static void onPlayerBlockBreakEvent(PlayerBlockBreakEvent event) {
        event.setCancelled(true);
    }
    public static void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event) {
        event.setCancelled(true);
    }
}
