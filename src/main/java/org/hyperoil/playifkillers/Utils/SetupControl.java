package org.hyperoil.playifkillers.Utils;

import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import org.hyperoil.playifkillers.ControlActions.BlockControl;
import org.hyperoil.playifkillers.ControlActions.EntityDamaging;
import org.hyperoil.playifkillers.ControlActions.ItemEvents;
import org.hyperoil.playifkillers.Main;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SetupControl {
    public static EventNode<InstanceEvent> setupControlEvents(ActionAllowed allowed, String eventNodeName) {
        EventNode<InstanceEvent> event = EventNode.type(eventNodeName, EventFilter.INSTANCE);

        BlockControl blockControl = new BlockControl(allowed);
        EntityDamaging damagingControl = new EntityDamaging(allowed);
        ItemEvents itemControl = new ItemEvents(allowed);

        Main.getInstance().getExecutorService().scheduleAtFixedRate(damagingControl::clearLastDamaged, 10L, 10L, TimeUnit.SECONDS);

        event.addListener(PlayerBlockBreakEvent.class, blockControl::onPlayerBlockBreakEvent);
        event.addListener(PlayerBlockPlaceEvent.class, blockControl::onPlayerBlockPlaceEvent);
        event.addListener(PickupItemEvent.class, itemControl::onPickUpItemEvent);
        event.addListener(EntityAttackEvent.class, damagingControl::attack);

        return event;
    }
}
