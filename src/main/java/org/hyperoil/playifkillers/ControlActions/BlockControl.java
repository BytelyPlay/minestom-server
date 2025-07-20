package org.hyperoil.playifkillers.ControlActions;

import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import org.hyperoil.playifkillers.Utils.ActionAllowed;
import org.hyperoil.playifkillers.Utils.Enums.Action;

public class BlockControl {
    public static void onPlayerBlockBreakEvent(PlayerBlockBreakEvent event) {
        event.setCancelled(ActionAllowed.getShouldDeny(event.getPlayer(), Action.BLOCK_BREAK));
    }
    public static void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event) {
        event.setCancelled(ActionAllowed.getShouldDeny(event.getPlayer(), Action.BLOCK_PLACE));
    }
}
