package org.hyperoil.playifkillers.NPCs;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.registry.RegistryData;
import net.minestom.server.utils.mojang.MojangUtils;
import org.hyperoil.playifkillers.Utils.Enums.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Objects;

public class RandomItemsLobbyNPC extends NPC {
    // TODO: replace with something more competent...
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
}
