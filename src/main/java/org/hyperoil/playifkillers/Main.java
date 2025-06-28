package org.hyperoil.playifkillers;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.EventHandler;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import org.hyperoil.playifkillers.Listeners.*;
import org.hyperoil.playifkillers.Minestom.CIChunkLoader;
import org.hyperoil.playifkillers.Utils.ChunkSaving;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class Main {
    // TODO: AFTER CLEANING you should try and make a binary format for saving chunks.
    public static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
    public static final Pos SPAWN_POINT = new Pos(new Vec(0, 2, 0));
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static InstanceContainer overWorld;
    public static final boolean SAVE_WORLD = true;
    // The BlockVec is the position and Block is the type
    public static ConcurrentHashMap<BlockVec, Block> blocksSaved = new ConcurrentHashMap<>();
    private Main() {}
    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();
        MojangAuth.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        overWorld = instanceManager.createInstanceContainer();

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, JoinPlayerSetup::onAsyncPlayerConfigurationEvent);
        CommandParser commandParser = new CommandParser(executorService, overWorld);
        globalEventHandler.addListener(PlayerCommandEvent.class, commandParser::onPlayerCommandEvent);
        globalEventHandler.addListener(PlayerBlockBreakEvent.class, BlockControl::onPlayerBlockBreakEvent);
        globalEventHandler.addListener(PlayerBlockPlaceEvent.class, BlockControl::onPlayerBlockPlaceEvent);
        globalEventHandler.addListener(PickupItemEvent.class, ItemEvents::onPickUpItemEvent);
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
        }));

        minecraftServer.start("127.0.0.1", 25565);
    }
}