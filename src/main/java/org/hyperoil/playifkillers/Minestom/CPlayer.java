package org.hyperoil.playifkillers.Minestom;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.EntityAttributesPacket;
import net.minestom.server.network.packet.server.play.UpdateHealthPacket;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.hyperoil.playifkillers.Utils.ChatColor;
import org.hyperoil.playifkillers.Utils.StatsHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CPlayer extends Player implements CustomHealthSystem, StatsHolder {
    public static CPlayer getCPlayer(Player p) {
        return (CPlayer) p;
    }

    private static final Logger log = LoggerFactory.getLogger(CPlayer.class);
    private double customHealth = 0;
    private double customMaxHealth = 0;

    public CPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);

        this.setCustomHealth(20d);
    }

    private @Nullable SendablePacket interceptPacket(@NotNull SendablePacket packet) {
        return packet;
    }

    private List<SendablePacket> interceptPackets(@NotNull SendablePacket... packets) {
        ArrayList<SendablePacket> packetsToSend = new ArrayList<>();
        Iterator<SendablePacket> it = Arrays.stream(packets).iterator();
        SendablePacket next;
        while (it.hasNext() && (next = it.next()) != null) {
            SendablePacket interceptedNext = interceptPacket(next);
            if (interceptedNext == null) continue;
            packetsToSend.add(interceptedNext);
        }
        return packetsToSend;
    }

    @Override
    public void sendPacket(@NotNull SendablePacket packet) {
        SendablePacket interceptedPacket = interceptPacket(packet);
        if (interceptedPacket == null) return;
        super.sendPacket(interceptedPacket);
    }

    @Override
    public void sendPackets(@NotNull SendablePacket... packets) {
        super.sendPackets(interceptPackets(packets));
    }

    @Override
    public void sendPackets(@NotNull Collection<SendablePacket> packets) {
        super.sendPackets(interceptPackets(packets.toArray(SendablePacket[]::new)));
    }

    public void sendUninterceptedPacket(@NotNull SendablePacket packet) {
        super.sendPacket(packet);
    }
}
