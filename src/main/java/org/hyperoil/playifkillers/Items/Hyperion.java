package org.hyperoil.playifkillers.Items;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;
import org.hyperoil.playifkillers.Entities.HealthDisplayArmorStand;
import org.hyperoil.playifkillers.Main;
import org.hyperoil.playifkillers.Utils.ChatColor;
import org.hyperoil.playifkillers.Utils.Item;
import org.hyperoil.playifkillers.Utils.ParticlesHelper;
import org.jetbrains.annotations.NotNull;

public class Hyperion implements Item {
    private static final int BLOCKS_PER_TELEPORT = 5;
    private final ItemStack item;
    public Hyperion() {
        ItemStack stack = ItemStack.of(Material.DIAMOND_SWORD);
        stack = stack.withCustomName(Component.text(ChatColor.GREEN + "Hyperion"));
        stack = stack.withLore(Component.text(ChatColor.GREEN + "Blasts and kills mobs... " + ChatColor.RED + ChatColor.BOLD + "EXTREMELY FAST"));

        item = stack;
    }

    public ItemStack getItemStack() {
        return item;
    }

    @Override
    public void rightClick(@NotNull PlayerBlockInteractEvent e) {
        useItem(e.getPlayer(), e.getInstance());
    }

    @Override
    public void rightClick(@NotNull PlayerEntityInteractEvent e) {
        useItem(e.getPlayer(), e.getInstance());
    }

    @Override
    public void rightClick(@NotNull PlayerUseItemEvent e) {
        useItem(e.getPlayer(), e.getInstance());
    }

    @Override
    public void punch(@NotNull EntityAttackEvent e) {
        Entity target = e.getTarget();
        Entity entity = e.getEntity();
        if (target instanceof LivingEntity living) {
            living.damage(Damage.fromEntity(entity, 10f));
        } else {
            if (!(target instanceof HealthDisplayArmorStand)) {
                target.remove();
            }
        }
    }

    private void useItem(Player p, Instance inst) {
        Main.executorService.submit(() -> {
            Pos pos = p.getPosition();
            float yaw = pos.yaw();
            float pitch = pos.pitch();

            double yawRad = Math.toRadians(yaw);
            double pitchRad = Math.toRadians(pitch);

            double relX = -Math.sin(yawRad) * Math.cos(pitchRad);
            double relY = -Math.sin(pitchRad);
            double relZ = Math.cos(yawRad) * Math.cos(pitchRad);

            Vec direction = new Vec(relX, relY, relZ).normalize().mul(BLOCKS_PER_TELEPORT);
            Vec teleportVec = pos.asVec().add(direction);
            if (inst.getBlock(teleportVec.blockX(),
                    teleportVec.blockY(),
                    teleportVec.blockZ()).isAir()) {
                p.teleport(teleportVec.asPosition()
                        .withYaw(yaw)
                        .withPitch(pitch));
                pos = p.getPosition();
            } else if (inst.getBlock(teleportVec.blockX(),
                    teleportVec.blockY() + 1,
                    teleportVec.blockZ()).isAir()) {
                teleportVec = teleportVec.add(0, 1, 0);
                p.teleport(teleportVec.asPosition()
                        .withYaw(yaw)
                        .withPitch(pitch));
            }

            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        ParticlesHelper.spawnParticle(pos.add(x, y, z), Particle.EXPLOSION, 1, 0.1f);
                    }
                }
            }
            p.playSound(Sound.sound(SoundEvent.ENTITY_GENERIC_EXPLODE, Sound.Source.PLAYER, 1, 1));

            final float[] damage = {10};
            inst.getNearbyEntities(pos, 5)
                    .stream()
                    .filter(entity -> !entity.equals(p))
                    .forEach(entity -> {
                        damage[0] *= 2;
                        if (entity instanceof LivingEntity living) {
                            living.damage(Damage.fromPlayer(p, damage[0]));
                        } else {
                            if (!(entity instanceof HealthDisplayArmorStand)) {
                                entity.remove();
                            }
                        }
                    });
        });
    }
}
