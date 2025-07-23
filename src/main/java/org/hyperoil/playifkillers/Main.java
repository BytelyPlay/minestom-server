package org.hyperoil.playifkillers;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDeathEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.world.DimensionType;
import org.hyperoil.playifkillers.Commands.*;
import org.hyperoil.playifkillers.ControlActions.SpawnEggs;
import org.hyperoil.playifkillers.Listeners.*;
import org.hyperoil.playifkillers.Minestom.CIChunkLoader;
import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.hyperoil.playifkillers.NPCs.RandomItemsLobbyNPC;
import org.hyperoil.playifkillers.Utils.ActionAllowed;
import org.hyperoil.playifkillers.Utils.CommandRegistration;
import org.hyperoil.playifkillers.Utils.Enums.Action;
import org.hyperoil.playifkillers.Utils.Enums.RuleValue;
import org.hyperoil.playifkillers.Utils.SetupControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    // concurrency is the goal... (don't overdo it that is just bad)
    // TODO: make this a recreation of minigamz...

    // half the available cores...
    private static Main instance;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() / 2);

    public static final Pos LOBBY_SPAWN_POINT = new Pos(0.5, 9, 0.5, 90, 0);
    public static final Pos RANDOM_ITEMS_SPAWN_POINT = new Pos(0.814, 3, 0.341, 0, 0);
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
        CommandRegistration.register(new Hub());

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

        EventNode<InstanceEvent> customItems = EventNode.type("custom-items", EventFilter.INSTANCE);

        customItems.addListener(EntityAttackEvent.class, CustomItems::punch);
        customItems.addListener(PlayerEntityInteractEvent.class, CustomItems::entityInteract);
        customItems.addListener(PlayerBlockInteractEvent.class, CustomItems::blockInteract);
        customItems.addListener(PlayerUseItemEvent.class, CustomItems::useItem);

        globalEventHandler.addListener(EntityDeathEvent.class, EntityDeathHandler::death);
        globalEventHandler.addListener(PlayerEntityInteractEvent.class, InteractNPC::playerEntityInteract);
        globalEventHandler.addListener(EntityAttackEvent.class, InteractNPC::entityAttack);
        globalEventHandler.addChild(customItems);
    }

    private void setupLobby() {
        AtomicBoolean spawnedNPCs = new AtomicBoolean(false);

        EventNode<InstanceEvent> lobbyEventNode = lobby.eventNode();

        lobbyEventNode.addListener(PlayerSpawnEvent.class, event -> {
            if (!spawnedNPCs.get()) {
                new RandomItemsLobbyNPC().setInstance(lobby, RAND_ITEMS_NPC_POS);
                spawnedNPCs.set(true);
            }
        });

        lobbyEventNode.addChild(SetupControl.setupControlEvents(new ActionAllowed(RuleValue.DENY,
                new ConcurrentHashMap<>()), lobby.getUuid() + ".control"));
    }

    private void setupRandomItems() {
        EventNode<InstanceEvent> randomItemsEventNode = randomItems.eventNode();

        ConcurrentHashMap<Action, RuleValue> rules = new ConcurrentHashMap<>();
        rules.put(Action.BLOCK_BREAK, RuleValue.ALLOW);
        rules.put(Action.BLOCK_PLACE, RuleValue.ALLOW);
        rules.put(Action.ITEM_PICKUP, RuleValue.ALLOW);
        rules.put(Action.USE_SPAWN_EGGS, RuleValue.ALLOW);

        randomItemsEventNode.addChild(SetupControl.setupControlEvents(new ActionAllowed(RuleValue.DENY, rules),
                randomItems.getUuid() + ".control"));
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                if (player.getInstance() == randomItems) {
                    Material[] materials = Material.values().toArray(Material[]::new);
                    int randomIndex = ThreadLocalRandom.current().nextInt(0, materials.length - 1);
                    ItemStack item = ItemStack.of(materials[randomIndex]);
                    player.getInventory().addItemStack(item);
                }
            }
        }, TaskSchedule.tick(200), TaskSchedule.tick(200));
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