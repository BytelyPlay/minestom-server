package org.hyperoil.playifkillers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import org.hyperoil.playifkillers.Listeners.*;
import org.hyperoil.playifkillers.Utils.SerializationHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static org.hyperoil.playifkillers.Utils.SerializationHelpers.seralizeBlocksSaved;

public class Main {
    public static final Pos SPAWN_POINT = new Pos(new Vec(25, 51, 25));
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static InstanceContainer overWorld;
    public static final boolean SAVE_WORLD = true;
    // The BlockVec is the position and Block is the type
    public static ConcurrentHashMap<BlockVec, Block> blocksSaved = new ConcurrentHashMap<>();
    private Main() {}
    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();
        MojangAuth.init();
        blocksSaved.put(new BlockVec(SPAWN_POINT.sub(0, 3, 0)), Block.STONE);

        Path overWorldSaveJson = Paths.get("overworldsave.json");
        if (Files.exists(overWorldSaveJson)) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                HashMap<String, String> deserialized = mapper.readValue(Files.readString(overWorldSaveJson), new TypeReference<>() {});
                blocksSaved = SerializationHelpers.deserializeBlocksSaved(deserialized);
            } catch (IOException e) {
                Main.log.error("Error while loading .json:");
                e.printStackTrace();
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (!Files.exists(overWorldSaveJson)) Files.createFile(overWorldSaveJson);

                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(new FileWriter(overWorldSaveJson.toString()), seralizeBlocksSaved(blocksSaved));
            } catch (IOException e) {
                log.error("Error while saving .json:");
                e.printStackTrace();
            }
        }));

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        overWorld = instanceManager.createInstanceContainer();

        overWorld.setGenerator(WorldGenerators::overWorldGenerator);

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, JoinPlayerSetup::onAsyncPlayerConfigurationEvent);
        globalEventHandler.addListener(PlayerCommandEvent.class, Commands::onPlayerCommandEvent);
        globalEventHandler.addListener(PlayerBlockBreakEvent.class, BlockControl::onPlayerBlockBreakEvent);
        globalEventHandler.addListener(PlayerBlockPlaceEvent.class, BlockControl::onPlayerBlockPlaceEvent);
        globalEventHandler.addListener(PickupItemEvent.class, ItemEvents::onPickUpItemEvent);

        minecraftServer.start("127.0.0.1", 25565);
    }
}