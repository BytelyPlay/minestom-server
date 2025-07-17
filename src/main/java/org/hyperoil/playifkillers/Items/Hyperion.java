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
import org.hyperoil.playifkillers.Main;
import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.hyperoil.playifkillers.Utils.ChatColor;
import org.hyperoil.playifkillers.Utils.Item;
import org.hyperoil.playifkillers.Utils.ParticlesHelper;
import org.jetbrains.annotations.NotNull;

public class Hyperion implements Item {
    private static final double baseDamage = 4000;
    private static final int MIN_PARTICLE_RADIUS = -2;
    private static final int MAX_PARTICLE_RADIUS = 3;
    private static final int KILL_RADIUS = 6;
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
    }

    @Override
    public void rightClick(@NotNull PlayerEntityInteractEvent e) {
    }

    @Override
    public void rightClick(@NotNull PlayerUseItemEvent e) {
        useItem(e.getPlayer(), e.getInstance());
    }

    @Override
    public void punch(@NotNull EntityAttackEvent e) {
        Entity target = e.getTarget();
        Entity entity = e.getEntity();
        if (entity instanceof CPlayer cp) {
            if (target instanceof LivingEntity living) {
                living.damage(Damage.fromEntity(entity, (float) getPunchDamage(cp)));
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

            for (int x = MIN_PARTICLE_RADIUS; x < MAX_PARTICLE_RADIUS; x++) {
                for (int y = MIN_PARTICLE_RADIUS; y < MAX_PARTICLE_RADIUS; y++) {
                    for (int z = MIN_PARTICLE_RADIUS; z < MAX_PARTICLE_RADIUS; z++) {
                        ParticlesHelper.spawnParticle(pos.add(x, y, z), Particle.EXPLOSION, 1, 0.1f);
                    }
                }
            }
            p.playSound(Sound.sound(SoundEvent.ENTITY_GENERIC_EXPLODE, Sound.Source.PLAYER, 1, 1));

            final double damage = getAbilityDamage(p);

            inst.getNearbyEntities(pos, KILL_RADIUS)
                    .stream()
                    .filter(entity -> !entity.equals(p))
                    .forEach(entity -> {
                        if (entity instanceof LivingEntity living) {
                            living.damage(Damage.fromPlayer(p, (float) damage));
                        }
                    });
        });
    }

    private double getAbilityDamage(Player p) {
        CPlayer cp = CPlayer.getCPlayer(p);
        double abilityDamage = cp.getTotalAbilityDamage();
        double intelligence = cp.getIntelligence();
        // TODO: still going to have to implement
        double damageMultiplier = 1.7;
        double additiveMultiplier = 1.4;
        double abilityScaling = 4;

        // found on a random forum: BaseAbilityDamage * (1 + Intelligence*AbilityScaling/100) * (1+AbilityDamage/100) * AdditiveMultipliers * MultiplicativeMultipliers
        // tried to implement good also made some adjustments as i felt that was wrong...
        return baseDamage + abilityDamage * (1 + intelligence*abilityScaling/100) * (1+abilityDamage/100) * additiveMultiplier * damageMultiplier;
    }
    private double getPunchDamage(Player p) {
        CPlayer cp = CPlayer.getCPlayer(p);
        double critDamage = cp.getTotalCritDamage();
        double strength = cp.getTotalStrength();
        // TODO: still going to have to implement
        double damageMultiplier = 1.7;
        double additiveMultiplier = 1.4;
        double abilityScaling = 4;

        double damage = baseDamage + (strength * 1.2) * 2;

        if (cp.rollIsCritical()) {
            damage *= 1 + critDamage / 100;
        }
        return damage;
    }
}
