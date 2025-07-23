package org.hyperoil.playifkillers.Minestom;

import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.hyperoil.playifkillers.Permissions.User;
import org.hyperoil.playifkillers.Utils.Enums.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CPlayer extends Player {
    private final User user = new User(this.getUuid());
    public static CPlayer getCPlayer(Player p) {
        return (CPlayer) p;
    }

    public User getUser() {
        return user;
    }

    public boolean hasPermission(Permission perm) {
        return user.hasPermission(perm);
    }

    public CPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);
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
