package org.hyperoil.playifkillers;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.*;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.UnitModifier;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import org.hyperoil.playifkillers.Listeners.*;

import java.util.HashMap;
import java.util.UUID;

public class Main {
    private static HashMap<UUID, PlayerSkin> uuidAndSkin = new HashMap<>();
    public static InstanceContainer overWorld;
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

        minecraftServer.start("127.0.0.1", 25565);
    }
}