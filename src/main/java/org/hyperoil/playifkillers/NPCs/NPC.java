package org.hyperoil.playifkillers.NPCs;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.*;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.network.player.PlayerConnection;
import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public abstract class NPC extends Entity {
    private final String username;
    private final PlayerSkin skin;

    public NPC(@NotNull String usernam, @NotNull PlayerSkin playerSkin) {
        super(EntityType.PLAYER);

        username = usernam;
        skin = playerSkin;
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        CPlayer p = CPlayer.getCPlayer(player);
        ArrayList<PlayerInfoUpdatePacket.Property> properties = new ArrayList<>();
        String skinSignature = skin.signature();
        if (skinSignature == null) {
            properties.add(new PlayerInfoUpdatePacket.Property("textures", skin.textures()));
        } else {
            properties.add(new PlayerInfoUpdatePacket.Property("textures", skin.textures(), skin.signature()));
        }
        PlayerInfoUpdatePacket.Entry entry = new PlayerInfoUpdatePacket.Entry(this.getUuid(), this.username,
                properties, false, -1,
                GameMode.SURVIVAL, null, null, 0);

        p.sendUninterceptedPacket(new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry));

        super.updateNewViewer(p);
    }

    @Override
    public void updateOldViewer(@NotNull Player player) {
        CPlayer p = CPlayer.getCPlayer(player);

        super.updateOldViewer(p);

        p.sendUninterceptedPacket(new PlayerInfoRemovePacket(this.getUuid()));
    }

    public abstract void playerAttack(EntityAttackEvent event);
    public abstract void playerInteract(PlayerEntityInteractEvent event);
}
