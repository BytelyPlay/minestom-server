package org.hyperoil.playifkillers.ControlActions;

import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import org.hyperoil.playifkillers.Utils.ActionAllowed;
import org.hyperoil.playifkillers.Utils.Enums.Action;
import org.hyperoil.playifkillers.Utils.Enums.RuleValue;

public class BlockControl {
    public static void onPlayerBlockBreakEvent(PlayerBlockBreakEvent event) {
        event.setCancelled(!ActionAllowed.getShouldAllow(event.getPlayer(), Action.BLOCK_BREAK));
    }
    public static void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event) {
        event.setCancelled(!ActionAllowed.getShouldAllow(event.getPlayer(), Action.BLOCK_PLACE));
    }
}
