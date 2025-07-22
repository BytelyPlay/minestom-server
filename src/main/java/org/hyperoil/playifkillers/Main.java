package org.hyperoil.playifkillers;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
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
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;
import org.hyperoil.playifkillers.Commands.Fill;
import org.hyperoil.playifkillers.Commands.Gmc;
import org.hyperoil.playifkillers.Commands.Gms;
import org.hyperoil.playifkillers.Commands.SetBlock;
import org.hyperoil.playifkillers.ControlActions.BlockControl;
import org.hyperoil.playifkillers.ControlActions.EntityDamaging;
import org.hyperoil.playifkillers.ControlActions.ItemEvents;
import org.hyperoil.playifkillers.Listeners.*;
import org.hyperoil.playifkillers.Minestom.CIChunkLoader;
import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.hyperoil.playifkillers.NPCs.RandomItemsLobbyNPC;
import org.hyperoil.playifkillers.Utils.CommandRegistration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    // concurrency is the goal... (don't overdo it that is just bad)
    // TODO: make this a recreation of minigamz...

    // half the available cores...
    private static Main instance;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() / 2);

    public static final Pos LOBBY_SPAWN_POINT = new Pos(new Vec(0.5, 14, 0.5));
    public static final Pos RANDOM_ITEMS_SPAWN_POINT = new Pos(0, 0, 0);
    private static final Pos RAND_ITEMS_NPC_POS = new Pos(-1.5, 9, 0.5, -90, 0);

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private final Instance lobby;
    private final Instance randomItems;

    public static final boolean SAVE_WORLD = true;
    private Main() {
        instance = this;

        MinecraftServer minecraftServer = MinecraftServer.init();
        MinecraftServer.getConnectionManager().setPlayerProvider(CPlayer::new);
        MojangAuth.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        RegistryKey<DimensionType> overworldDimensionType = MinecraftServer.getDimensionTypeRegistry().getKey(DimensionType.OVERWORLD.key());
        if (overworldDimensionType == null) {
            log.error("Cannot get overworld dimension type...");
            lobby = null;
            randomItems = null;
            return;
        }

        lobby = new InstanceContainer(UUID.nameUUIDFromBytes("lobby".getBytes()), overworldDimensionType, new CIChunkLoader());
        randomItems = new InstanceContainer(UUID.nameUUIDFromBytes("randomItems".getBytes()), overworldDimensionType, new CIChunkLoader());

        instanceManager.registerInstance(lobby);
        instanceManager.registerInstance(randomItems);

        setupLobby();
        setupRandomItems();

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, JoinPlayerSetup::onAsyncPlayerConfigurationEvent);

        CommandDispatcher<CommandSender> dispatcher = new CommandDispatcher<>();
        CommandRegistration.registerCommands(dispatcher);
        CommandRegistration.register(new Fill(executorService));
        CommandRegistration.register(new Gmc());
        CommandRegistration.register(new Gms());
        CommandRegistration.register(new SetBlock());

        globalEventHandler.addListener(PlayerCommandEvent.class, e -> {
            Player p = e.getPlayer();
            try {
                dispatcher.execute(e.getCommand(), p);
            } catch (CommandSyntaxException ex) {
                p.sendMessage(ex.getMessage());
            }
        });

        setupEventListeners();

        if (SAVE_WORLD) {

            executorService.scheduleAtFixedRate(() -> {
                for (Instance inst : instanceManager.getInstances()) {
                    inst.saveChunksToStorage();
                }
            }, 120, 120, TimeUnit.SECONDS);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Instance inst : instanceManager.getInstances()) {
                inst.saveChunksToStorage();
            }
            executorService.shutdown();
        }));

        executorService.scheduleAtFixedRate(EntityDamaging::clearLastDamaged, 10L, 10L, TimeUnit.SECONDS);

        minecraftServer.start("127.0.0.1", 25565);
    }

    public static Main getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        new Main();
    }

    private void setupEventListeners() {
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

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

        lobbyEventNode.addListener(PlayerEntityInteractEvent.class, InteractNPC::playerEntityInteract);
        lobbyEventNode.addListener(EntityAttackEvent.class, InteractNPC::entityAttack);

        globalEventHandler.addListener(EntityDeathEvent.class, EntityDeathHandler::death);
    }

    private void setupLobby() {
        AtomicBoolean spawnedNPCs = new AtomicBoolean(false);

        lobby.eventNode().addListener(PlayerSpawnEvent.class, event -> {
            if (!spawnedNPCs.get()) {
                new RandomItemsLobbyNPC().setInstance(lobby, RAND_ITEMS_NPC_POS);
                spawnedNPCs.set(true);
            }
        });
    }

    private void setupRandomItems() {
    }

    public Instance getLobby() {
        return lobby;
    }

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    public Instance getRandomItems() {
        return randomItems;
    }
}