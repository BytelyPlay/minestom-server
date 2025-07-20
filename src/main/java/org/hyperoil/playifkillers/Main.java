package org.hyperoil.playifkillers;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDeathEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import org.hyperoil.playifkillers.Commands.Fill;
import org.hyperoil.playifkillers.Commands.Gmc;
import org.hyperoil.playifkillers.Commands.Gms;
import org.hyperoil.playifkillers.ControlActions.BlockControl;
import org.hyperoil.playifkillers.ControlActions.EntityDamaging;
import org.hyperoil.playifkillers.ControlActions.ItemEvents;
import org.hyperoil.playifkillers.Listeners.*;
import org.hyperoil.playifkillers.Minestom.CIChunkLoader;
import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.hyperoil.playifkillers.Utils.CommandRegistration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    // concurrency is the goal... (don't overdo it that is just bad)
    // TODO: REPLACE THIS WITH A INSTANCED MAIN CLASS AND REPLACE ALL INSTANCES WITH GETTERS AND NO SETTERS... maybe also final...
    // TODO: this one takes priority: figure out how to deduplicate the custom health system because right now IT IS A MESS... also without losing clarity...
    // TODO: make this instead a recreation of minigamz...
    // TODO: make or use a permissions system for god's sake

    // half the available cores...
    public static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() / 2);
    public static final Pos SPAWN_POINT = new Pos(new Vec(0, 105, 0));
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static Instance lobby;
    public static final boolean SAVE_WORLD = true;
    private Main() {}
    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();
        MinecraftServer.getConnectionManager().setPlayerProvider(CPlayer::new);
        MojangAuth.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        lobby = instanceManager.createInstanceContainer();

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, JoinPlayerSetup::onAsyncPlayerConfigurationEvent);
        CommandDispatcher<CommandSender> dispatcher = new CommandDispatcher<>();
        CommandRegistration.registerCommands(dispatcher);
        CommandRegistration.register(new Fill(lobby, executorService));
        CommandRegistration.register(new Gmc());
        CommandRegistration.register(new Gms());

        globalEventHandler.addListener(PlayerCommandEvent.class, e -> {
            Player p = e.getPlayer();
            try {
                dispatcher.execute(e.getCommand(), p);
            } catch (CommandSyntaxException ex) {
                p.sendMessage(ex.getMessage());
            }
        });

        EventNode<InstanceEvent> lobbyEventNode = lobby.eventNode();

        EventNode<InstanceEvent> control = EventNode.type("control", EventFilter.INSTANCE);
        EventNode<InstanceEvent> customItems = EventNode.type("custom-items", EventFilter.INSTANCE);

        control.addListener(PlayerBlockBreakEvent.class, BlockControl::onPlayerBlockBreakEvent);
        control.addListener(PlayerBlockPlaceEvent.class, BlockControl::onPlayerBlockPlaceEvent);
        control.addListener(PickupItemEvent.class, ItemEvents::onPickUpItemEvent);
        control.addListener(EntityAttackEvent.class, EntityDamaging::attack);

        customItems.addListener(EntityAttackEvent.class, CustomItems::punch);
        customItems.addListener(PlayerEntityInteractEvent.class, CustomItems::entityInteract);
        customItems.addListener(PlayerBlockInteractEvent.class, CustomItems::blockInteract);
        customItems.addListener(PlayerUseItemEvent.class, CustomItems::useItem);

        lobbyEventNode.addChild(control);
        lobbyEventNode.addChild(customItems);

        globalEventHandler.addListener(EntityDeathEvent.class, EntityDeathHandler::death);

        executorService.scheduleAtFixedRate(() -> {
            if (!SAVE_WORLD) return;
            for (Instance inst : instanceManager.getInstances()) {
                inst.saveChunksToStorage();
            }
        }, 120, 120, TimeUnit.SECONDS);

        instanceManager.getInstances().forEach(instance -> {
            if (instance instanceof @NotNull InstanceContainer container) {
                container.setChunkLoader(new CIChunkLoader());
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Instance inst : instanceManager.getInstances()) {
                inst.saveChunksToStorage();
            }
            executorService.shutdown();
        }));

        executorService.scheduleAtFixedRate(EntityDamaging::clearLastDamaged, 10L, 10L, TimeUnit.SECONDS);

        minecraftServer.start("127.0.0.1", 25565);
    }
}