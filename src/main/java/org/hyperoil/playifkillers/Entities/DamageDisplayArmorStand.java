package org.hyperoil.playifkillers.Entities;

import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.Instance;
import org.hyperoil.playifkillers.Utils.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class DamageDisplayArmorStand extends Entity {
    private static final double FALL_PER_TICK = 0.01;
    private long ticksLived = 0L;
    public DamageDisplayArmorStand(@NotNull Pos entityPos, double amount, Instance instance) {
        super(EntityType.ARMOR_STAND);
        this.setCustomNameVisible(true);
        DecimalFormat df = new DecimalFormat("#,###");
        df.setMaximumFractionDigits(0);
        this.setCustomName(ChatColor.RED + "-" + df.format(amount));
        this.setInvisible(true);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        this.setInstance(instance, entityPos.add(random.nextDouble(-2, 2), 2, random.nextDouble(-2, 2)));
    }
    private void setCustomName(String s) {
        this.set(DataComponents.CUSTOM_NAME, Component.text(s));
    }
    @Override
    public void update(long time) {
        if (ticksLived > 20) {
            this.remove();
            return;
        }
        ticksLived++;
        this.teleport(
                this.getPosition().sub(0, FALL_PER_TICK, 0)
        );
    }
}
