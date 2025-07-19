package org.hyperoil.playifkillers;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDeathEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import org.hyperoil.playifkillers.Commands.Fill;
import org.hyperoil.playifkillers.Commands.Gmc;
import org.hyperoil.playifkillers.Commands.Gms;
import org.hyperoil.playifkillers.Commands.Spawn;
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
    // TODO: this one takes priority: figure out how to deduplicate the custom health system because right now IT IS A MESS... also without losing clarity...
    // TODO: make this instead a recreation of minigamz...
    public static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    public static final Pos SPAWN_POINT = new Pos(new Vec(0, 105, 0));
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static InstanceContainer overWorld;
    public static final boolean SAVE_WORLD = true;
    private Main() {}
    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();
        MinecraftServer.getConnectionManager().setPlayerProvider(CPlayer::new);
        MojangAuth.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        overWorld = instanceManager.createInstanceContainer();

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, JoinPlayerSetup::onAsyncPlayerConfigurationEvent);
        CommandDispatcher<CommandSender> dispatcher = new CommandDispatcher<>();
        CommandRegistration.registerCommands(dispatcher);
        CommandRegistration.register(new Fill(overWorld, executorService));
        CommandRegistration.register(new Gmc());
        CommandRegistration.register(new Gms());
        CommandRegistration.register(new Spawn());

        globalEventHandler.addListener(PlayerCommandEvent.class, e -> {
            Player p = e.getPlayer();
            try {
                dispatcher.execute(e.getCommand(), p);
            } catch (CommandSyntaxException ex) {
                p.sendMessage(ex.getMessage());
            }
        });
        globalEventHandler.addListener(PlayerBlockBreakEvent.class, BlockControl::onPlayerBlockBreakEvent);
        globalEventHandler.addListener(PlayerBlockPlaceEvent.class, BlockControl::onPlayerBlockPlaceEvent);
        globalEventHandler.addListener(PickupItemEvent.class, ItemEvents::onPickUpItemEvent);
        globalEventHandler.addListener(EntityAttackEvent.class, EntityDamaging::attack);
        globalEventHandler.addListener(EntityAttackEvent.class, CustomItems::punch);
        globalEventHandler.addListener(PlayerEntityInteractEvent.class, CustomItems::entityInteract);
        globalEventHandler.addListener(PlayerBlockInteractEvent.class, CustomItems::blockInteract);
        globalEventHandler.addListener(PlayerUseItemEvent.class, CustomItems::useItem);
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