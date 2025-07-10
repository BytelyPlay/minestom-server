package org.hyperoil.playifkillers.Items;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.Explosion;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import org.hyperoil.playifkillers.Entities.HealthDisplayArmorStand;
import org.hyperoil.playifkillers.Utils.ChatColor;
import org.hyperoil.playifkillers.Utils.Item;
import org.hyperoil.playifkillers.Utils.ParticlesHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Hyperion implements Item {
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
        Pos pos = p.getPosition();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    ParticlesHelper.spawnParticle(pos.add(x, y, z), Particle.EXPLOSION, 1, 0.1f);
                }
            }
        }
        List<Entity> entities = inst.getNearbyEntities(pos, 5)
                .stream()
                .filter(entity -> !entity.equals(p))
                .toList();
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                living.damage(Damage.fromPlayer(p, 10f));
            } else {
                if (!(entity instanceof HealthDisplayArmorStand)) {
                    entity.remove();
                }
            }
        }
    }
}
