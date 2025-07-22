package org.hyperoil.playifkillers.NPCs;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.registry.RegistryData;
import net.minestom.server.utils.mojang.MojangUtils;
import org.hyperoil.playifkillers.Main;
import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.hyperoil.playifkillers.Utils.Enums.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Objects;

public class RandomItemsLobbyNPC extends NPC {
    private static final PlayerSkin skin;
    private static final Logger log = LoggerFactory.getLogger(RandomItemsLobbyNPC.class);

    static {
        PlayerSkin skin1 = null;
        try {
            ObjectMapper mapper = new ObjectMapper();

            skin1 = mapper.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream("RandomItemsLobbyNPCSkin.json"), PlayerSkin.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skin = skin1;
    }

    public RandomItemsLobbyNPC() {
        super(ChatColor.GREEN + "Random Items", skin);
    }

    @Override
    public void playerAttack(EntityAttackEvent event) {
        Entity attacker = event.getEntity();

        if (attacker instanceof CPlayer p) {
            p.setInstance(Main.getInstance().getRandomItems(), Main.RANDOM_ITEMS_SPAWN_POINT);
        }
    }

    @Override
    public void playerInteract(PlayerEntityInteractEvent event) {
        CPlayer p = CPlayer.getCPlayer(event.getPlayer());

        p.setInstance(Main.getInstance().getRandomItems(), Main.RANDOM_ITEMS_SPAWN_POINT);
    }
}
