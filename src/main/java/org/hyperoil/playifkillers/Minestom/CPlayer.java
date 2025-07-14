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
import org.hyperoil.playifkillers.Utils.CustomHealthSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CPlayer extends Player implements CustomHealthSystem {
    private static final Logger log = LoggerFactory.getLogger(CPlayer.class);
    private double customHealth = 0;
    private double customMaxHealth = 0;

    public CPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);

        this.setCustomHealth(999999999999999999d);
    }

    private @Nullable SendablePacket interceptPacket(@NotNull SendablePacket packet) {
        if (packet instanceof UpdateHealthPacket healthPacket)
            return interceptHealthPacket(healthPacket);
        if (packet instanceof EntityAttributesPacket attributesPacket)
            return interceptEntityAttributesPacket(attributesPacket);
        return packet;
    }

    private UpdateHealthPacket interceptHealthPacket(@NotNull UpdateHealthPacket packet) {
        // TODO: make this scale to 20 instead of always being 20
        return new UpdateHealthPacket(20, packet.food(), packet.foodSaturation());
    }

    private EntityAttributesPacket interceptEntityAttributesPacket(@NotNull EntityAttributesPacket packet) {
        List<EntityAttributesPacket.Property> properties = packet.properties();
        EntityAttributesPacket.Property maxHealthProperty = properties
                .stream()
                .filter(property -> property.attribute() == Attribute.MAX_HEALTH)
                .findFirst()
                .orElse(null);
        if (maxHealthProperty == null) return packet;
        ArrayList<EntityAttributesPacket.Property> modifiedProperties = new ArrayList<>(properties);
        modifiedProperties.remove(maxHealthProperty);
        EntityAttributesPacket.Property modifiedProperty =
                new EntityAttributesPacket.Property(maxHealthProperty.attribute(), 20d, List.of());
        modifiedProperties.add(modifiedProperty);
        return new EntityAttributesPacket(packet.entityId(), List.copyOf(modifiedProperties));
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

    @Override
    public void update(long time) {
        super.update(time);
        regenerateHealth();
        updateActionBar();
    }

    @Override
    public void respawn() {
        super.respawn();
        setCustomHealth(getCustomMaxHealth());
    }

    private void updateActionBar() {
        if (this.getPlayerConnection().getConnectionState() == ConnectionState.PLAY) {
            this.sendActionBar(Component.text(ChatColor.RED + "" + getCustomHealth() + "/" + getCustomMaxHealth()));
        }
    }

    private void regenerateHealth() {
        double currentMaxHealth = this.getCustomMaxHealth();
        double currentHealth = this.getCustomHealth();
        if (currentHealth >= currentMaxHealth) return;
        if (currentHealth + 1 >= currentMaxHealth) {
            setCustomHealth(getCustomMaxHealth());
        } else {
            setCustomHealth(currentHealth + 0.1);
        }
    }

    @Override
    public void setHealth(float health) {
        setCustomHealth(health);
    }

    @Override
    public float getHealth() {
        return (float) this.getCustomHealth();
    }

    @Override
    public double getCustomHealth() {
        return customHealth;
    }

    @Override
    public double getCustomMaxHealth() {
        return customMaxHealth;
    }

    @Override
    public void setCustomHealth(double amount) {
        if (amount <= 0 && !isDead()) this.kill();
        if (getCustomMaxHealth() < amount) {
            setCustomMaxHealth(amount);
        }
        customHealth = amount;
    }
    @Override
    public void setCustomMaxHealth(double amount) {
        AttributeInstance attribute = this.getAttribute(Attribute.MAX_HEALTH);
        if (attribute.getBaseValue() != getCustomMaxHealth()) {
            this.getAttribute(Attribute.MAX_HEALTH).setBaseValue(amount);
        }
        customMaxHealth = amount;
    }

    @Override
    protected void onAttributeChanged(@NotNull AttributeInstance attribute) {
        super.onAttributeChanged(attribute);
        if (attribute.attribute() == Attribute.MAX_HEALTH) {
            customMaxHealth = attribute.getBaseValue();
        }
    }
}
