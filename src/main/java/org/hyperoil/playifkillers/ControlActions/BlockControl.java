package org.hyperoil.playifkillers.ControlActions;

import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.hyperoil.playifkillers.Utils.ActionAllowed;
import org.hyperoil.playifkillers.Utils.Enums.Action;

public class BlockControl {
    public static void onPlayerBlockBreakEvent(PlayerBlockBreakEvent event) {
        CPlayer p = CPlayer.getCPlayer(event.getPlayer());
        event.setCancelled(ActionAllowed.getShouldDeny(p, Action.BLOCK_BREAK));
    }
    public static void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event) {
        CPlayer p = CPlayer.getCPlayer(event.getPlayer());
        event.setCancelled(ActionAllowed.getShouldDeny(p, Action.BLOCK_PLACE));
    }
}
