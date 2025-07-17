package org.hyperoil.playifkillers.Entities;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.damage.Damage;
import org.hyperoil.playifkillers.Main;
import org.hyperoil.playifkillers.Utils.CustomEntityCreature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Zombie extends CustomEntityCreature {
    private static final Logger log = LoggerFactory.getLogger(Zombie.class);
    private int lastDamagedInTicks = 0;

    public Zombie() {
        super(EntityType.ZOMBIE);

        this.setCustomHealth(20.0f);

        this.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.1f);
    }
    @Override
    public void update(long time) {
        Pos pos = this.getPosition();
        Player p = (Player) this.getInstance().getNearbyEntities(pos, 10)
                .stream()
                .filter(entity -> entity instanceof Player)
                .findFirst()
                .orElse(null);
        if (p == null) {
            this.getNavigator().reset();
            return;
        }
        if (p.getPosition().distance(pos) < 1) {
            if (lastDamagedInTicks > 10) {
                lastDamagedInTicks = 0;
                p.damage(Damage.fromEntity(this, 2.5f));
            } else {
                lastDamagedInTicks++;
            }
            return;
        }
        this.getNavigator().setPathTo(p.getPosition());

        this.getNavigator().tick();
    }

    @Override
    public void spawn() {
        super.spawn();

        Entity healthDisplay = new HealthDisplayArmorStand(this);
        healthDisplay.setInstance(Main.overWorld, this.getPosition());
        healthDisplay.spawn();
    }
}
