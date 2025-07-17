package org.hyperoil.playifkillers.Minestom;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.LivingEntity;
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
import org.hyperoil.playifkillers.Utils.StatsHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
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
        if (isDead()) return;
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

    // Stats holder implementation...
    // TODO: call a method in used items and armors combine their stats to return the total or regular stats and accessories

    @Override
    public double getStrength() {
        return 1000;
    }

    @Override
    public void addStrength(double amount) {

    }

    @Override
    public void subtractStrength(double amount) {

    }

    @Override
    public double getCritDamage() {
        return 1000;
    }

    @Override
    public void addCritDamage(double amount) {

    }

    @Override
    public void subtractCritDamage(double amount) {

    }

    @Override
    public double getCritChance() {
        return 1000;
    }

    @Override
    public void addCritChance(double amount) {

    }

    @Override
    public void subtractCritChance(double amount) {

    }

    @Override
    public double getHealthStat() {
        return 1000;
    }

    @Override
    public void addHealth(double amount) {

    }

    @Override
    public void subtractHealth(double amount) {

    }

    @Override
    public double getDefense() {
        return 1000;
    }

    @Override
    public void addDefense(double amount) {

    }

    @Override
    public void subtractDefense(double amount) {

    }

    @Override
    public double getSpeed() {
        return 1000;
    }

    @Override
    public void addSpeed(double amount) {

    }

    @Override
    public void subtractSpeed(double amount) {

    }

    @Override
    public double getIntelligence() {
        return 1000;
    }

    @Override
    public void addIntelligence(double amount) {

    }

    @Override
    public void subtractIntelligence(double amount) {

    }

    @Override
    public double getAttackSpeed() {
        return 1000;
    }

    @Override
    public void addAttackSpeed(double amount) {

    }

    @Override
    public void subtractAttackSpeed(double amount) {

    }

    @Override
    public double getTrueDefense() {
        return 1000;
    }

    @Override
    public void addTrueDefense(double amount) {

    }

    @Override
    public void subtractTrueDefense(double amount) {

    }

    @Override
    public double getFerocity() {
        return 1000;
    }

    @Override
    public void addFerocity(double amount) {

    }

    @Override
    public void subtractFerocity(double amount) {

    }

    @Override
    public double getAbilityDamage() {
        return 1000;
    }

    @Override
    public void addAbilityDamage(double amount) {

    }

    @Override
    public void subtractAbilityDamage(double amount) {

    }

    @Override
    public double getTotalStrength() {
        return 1000;
    }

    @Override
    public double getTotalCritDamage() {
        return 1000;
    }

    @Override
    public double getTotalCritChance() {
        return 1000;
    }

    @Override
    public double getTotalHealth() {
        return 1000;
    }

    @Override
    public double getTotalDefense() {
        return 1000;
    }

    @Override
    public double getTotalSpeed() {
        return 1000;
    }

    @Override
    public double getTotalIntelligence() {
        return 1000;
    }

    @Override
    public double getTotalAttackSpeed() {
        return 1000;
    }

    @Override
    public double getTotalTrueDefense() {
        return 1000;
    }

    @Override
    public double getTotalFerocity() {
        return 1000;
    }

    @Override
    public double getTotalAbilityDamage() {
        return 1000;
    }

    @Override
    public double getPermaStrength() {
        return 1000;
    }

    @Override
    public void addPermaStrength(double amount) {

    }

    @Override
    public void subtractPermaStrength(double amount) {

    }

    @Override
    public double getPermaCritDamage() {
        return 1000;
    }

    @Override
    public void addPermaCritDamage(double amount) {

    }

    @Override
    public void subtractPermaCritDamage(double amount) {

    }

    @Override
    public double getPermaCritChance() {
        return 1000;
    }

    @Override
    public void addPermaCritChance(double amount) {

    }

    @Override
    public void subtractPermaCritChance(double amount) {

    }

    @Override
    public double getPermaHealth() {
        return 1000;
    }

    @Override
    public void addPermaHealth(double amount) {

    }

    @Override
    public void subtractPermaHealth(double amount) {

    }

    @Override
    public double getPermaDefense() {
        return 1000;
    }

    @Override
    public void addPermaDefense(double amount) {

    }

    @Override
    public void subtractPermaDefense(double amount) {

    }

    @Override
    public double getPermaSpeed() {
        return 1000;
    }

    @Override
    public void addPermaSpeed(double amount) {

    }

    @Override
    public void subtractPermaSpeed(double amount) {

    }

    @Override
    public double getPermaIntelligence() {
        return 1000;
    }

    @Override
    public void addPermaIntelligence(double amount) {

    }

    @Override
    public void subtractPermaIntelligence(double amount) {

    }

    @Override
    public double getPermaAttackSpeed() {
        return 1000;
    }

    @Override
    public void addPermaAttackSpeed(double amount) {

    }

    @Override
    public void subtractPermaAttackSpeed(double amount) {

    }

    @Override
    public double getPermaTrueDefense() {
        return 1000;
    }

    @Override
    public void addPermaTrueDefense(double amount) {

    }

    @Override
    public void subtractPermaTrueDefense(double amount) {

    }

    @Override
    public double getPermaFerocity() {
        return 1000;
    }

    @Override
    public void addPermaFerocity(double amount) {

    }

    @Override
    public void subtractPermaFerocity(double amount) {

    }

    @Override
    public double getPermaAbilityDamage() {
        return 1000;
    }

    @Override
    public void addPermaAbilityDamage(double amount) {

    }

    @Override
    public void subtractPermaAbilityDamage(double amount) {

    }

    public boolean rollIsCritical() {
        double randomDouble = ThreadLocalRandom.current().nextDouble(1, 100);
        double critChance = getTotalCritChance();
        return randomDouble > 50 - critChance && randomDouble < critChance + 50;
    }
}
