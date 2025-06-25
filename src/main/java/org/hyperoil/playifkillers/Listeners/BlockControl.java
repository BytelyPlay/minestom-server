package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.block.Block;
import org.hyperoil.playifkillers.Main;

public class BlockControl {
    public static void onPlayerBlockBreakEvent(PlayerBlockBreakEvent event) {
        boolean isBlockBreakAllowed = isBlockBreakAllowed(event);
        if (isBlockBreakAllowed) {
            if (Main.SAVE_WORLD) {
                BlockVec vec = event.getBlockPosition();
                Main.blocksSaved.remove(vec);
            }
            return;
        }
        if (!isBlockBreakAllowed) {
            event.setCancelled(true);
        }
    }
    public static void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event) {
        boolean isBlockPlaceAllowed = isBlockPlaceAllowed(event);
        if (isBlockPlaceAllowed) {
            if (Main.SAVE_WORLD) {
                BlockVec vec = event.getBlockPosition();
                Block block = event.getBlock();
                Main.blocksSaved.put(vec, block);
            }
            return;
        }
        if (!isBlockPlaceAllowed) {
            event.setCancelled(true);
        }
    }
    private static boolean isBlockPlaceAllowed(PlayerBlockPlaceEvent e) {
        BlockVec pos = e.getBlockPosition();
        return !(pos.y() <= 0);
    }
    private static boolean isBlockBreakAllowed(PlayerBlockBreakEvent e) {
        BlockVec pos = e.getBlockPosition();
        return !(pos.y() <= 0);
    }
}
