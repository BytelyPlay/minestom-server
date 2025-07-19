package org.hyperoil.playifkillers.ControlActions;

import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import org.hyperoil.playifkillers.Utils.ActionAllowed;
import org.hyperoil.playifkillers.Utils.Enums.Action;
import org.hyperoil.playifkillers.Utils.Enums.RuleValue;

public class BlockControl {
    public static void onPlayerBlockBreakEvent(PlayerBlockBreakEvent event) {
        if (!(ActionAllowed.getRule(event.getPlayer(), Action.blockBreak) == RuleValue.ALLOW)) {
            event.setCancelled(true);
        }
    }
    public static void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event) {
        if (!(ActionAllowed.getRule(event.getPlayer(), Action.blockPlace) == RuleValue.ALLOW)) {
            event.setCancelled(true);
        }
    }
}
