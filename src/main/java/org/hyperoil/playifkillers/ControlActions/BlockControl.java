package org.hyperoil.playifkillers.ControlActions;

import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.hyperoil.playifkillers.Utils.ActionAllowed;
import org.hyperoil.playifkillers.Utils.Enums.Action;

public class BlockControl {
    private final ActionAllowed rules;
    public void onPlayerBlockBreakEvent(PlayerBlockBreakEvent event) {
        CPlayer p = CPlayer.getCPlayer(event.getPlayer());
        if (rules.getShouldDeny(p, Action.BLOCK_BREAK)) event.setCancelled(true);
    }
    public void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event) {
        CPlayer p = CPlayer.getCPlayer(event.getPlayer());
        if (rules.getShouldDeny(p, Action.BLOCK_PLACE)) event.setCancelled(true);
    }

    public BlockControl(ActionAllowed rule) {
        rules = rule;
    }
}
