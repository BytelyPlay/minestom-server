package org.hyperoil.playifkillers.Entities;

import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponent;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import org.hyperoil.playifkillers.Utils.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class HealthDisplayArmorStand extends Entity {
    private static final Logger log = LoggerFactory.getLogger(HealthDisplayArmorStand.class);
    private final LivingEntity entity;

    public HealthDisplayArmorStand(LivingEntity target) {
        super(EntityType.ARMOR_STAND);

        entity = target;
        this.setInvisible(true);
        this.setCustomNameVisible(true);
        if (entity instanceof Player p) {
            this.updateViewableRule(player -> !player.equals(p));
        }
        updateHealth();
    }
    @Override
    public void update(long time) {
        updateHealth();
        this.teleport(entity.getPosition().add(0, 0.25, 0));
        if (entity instanceof Player p) {
            if (!p.isOnline()) this.remove();
            return;
        }
        if (entity.isDead()) {
            this.setCustomNameVisible(false);
        }
        if (!entity.isDead() && !this.isCustomNameVisible()) this.setCustomNameVisible(true);
    }
    private void setCustomName(String s) {
        this.set(DataComponents.CUSTOM_NAME, Component.text(s));
    }
    private void updateHealth() {
        this.setCustomName(ChatColor.RED + "HP: " + entity.getHealth() + "/" + entity.getAttribute(Attribute.MAX_HEALTH).getValue());
    }
    @Override
    public void updateCollisions() {}
}
