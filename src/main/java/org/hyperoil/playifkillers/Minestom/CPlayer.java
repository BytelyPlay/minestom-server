package org.hyperoil.playifkillers.Minestom;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.timer.TaskSchedule;
import org.hyperoil.playifkillers.Utils.ChatColor;
import org.hyperoil.playifkillers.Utils.CustomHealthSystem;
import org.hyperoil.playifkillers.Utils.CustomLivingEntity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CPlayer extends Player implements CustomHealthSystem {
    private static final Logger log = LoggerFactory.getLogger(CPlayer.class);
    private double customHealth = 0;
    private double customMaxHealth = 0;

    public CPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);

        this.setCustomHealth(1024.00);

        MinecraftServer.getSchedulerManager().scheduleTask(this::updateActionBar,
                TaskSchedule.tick(1), TaskSchedule.tick(1));
    }

    private void updateActionBar() {
        if (this.getPlayerConnection().getConnectionState() == ConnectionState.PLAY) {
            this.sendActionBar(Component.text(ChatColor.RED + "" + getCustomHealth() + "/" + getCustomMaxHealth()));
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
        if (amount <= 0 && !isDead) this.kill();
        if (customMaxHealth < amount) {
            customMaxHealth = amount;
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
