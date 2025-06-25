package org.hyperoil.playifkillers;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import org.hyperoil.playifkillers.Listeners.*;
import org.hyperoil.playifkillers.Utils.ChunkSaving;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    // TODO: clean the whole codebase up.
    // TODO: AFTER CLEANING you should try and make a binary format for saving chunks. and also try to use your chunk and ichunkloader instance to save the chunks completely without using events. amd also be able to load all chunks from the saves... not just generate
    public static ExecutorService executorService = Executors.newFixedThreadPool(4);
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

        overWorld.setGenerator(WorldGenerators::overWorldGenerator);

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, JoinPlayerSetup::onAsyncPlayerConfigurationEvent);
        globalEventHandler.addListener(PlayerCommandEvent.class, Commands::onPlayerCommandEvent);
        globalEventHandler.addListener(PlayerBlockBreakEvent.class, BlockControl::onPlayerBlockBreakEvent);
        globalEventHandler.addListener(PlayerBlockPlaceEvent.class, BlockControl::onPlayerBlockPlaceEvent);
        globalEventHandler.addListener(PickupItemEvent.class, ItemEvents::onPickUpItemEvent);
        globalEventHandler.addListener(PlayerChunkUnloadEvent.class, event -> {
            Chunk chunk = overWorld.getChunk(event.getChunkX(), event.getChunkZ());
            if (chunk == null) return;
            ChunkSaving.saveChunk(chunk);
            executorService.submit(() -> {
                for (BlockVec vec : blocksSaved.keySet()) {
                    int chunkX = vec.chunkX();
                    int chunkZ = vec.chunkZ();
                    if (chunkX == chunk.getChunkX() && chunkZ == chunk.getChunkZ() && !chunk.isLoaded()) {
                        blocksSaved.remove(vec);
                    }
                }
            });
        });

        minecraftServer.start("127.0.0.1", 25565);
    }
}