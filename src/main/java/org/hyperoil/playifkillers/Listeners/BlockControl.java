package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.block.Block;
import org.hyperoil.playifkillers.Main;

public class BlockControl {
    public static void onPlayerBlockBreakEvent(PlayerBlockBreakEvent event) {
        boolean isBlockBreakAllowed = isBlockBreakAllowed(event);
        if (!isBlockBreakAllowed) {
            event.setCancelled(true);
        }
    }
    public static void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event) {
        boolean isBlockPlaceAllowed = isBlockPlaceAllowed(event);
        if (!isBlockPlaceAllowed) {
            event.setCancelled(true);
        }
    }
    private static boolean isBlockPlaceAllowed(PlayerBlockPlaceEvent e) {
        return true;
    }
    private static boolean isBlockBreakAllowed(PlayerBlockBreakEvent e) {
        return true;
    }
}
