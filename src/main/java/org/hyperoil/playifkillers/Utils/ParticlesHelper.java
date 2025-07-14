package org.hyperoil.playifkillers.Utils;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;

import java.util.List;

public class ParticlesHelper {
    public static void spawnParticle(BlockVec vec, Particle particle, int count, float speed) {
        List<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers()
                .stream()
                .toList();
        for (Player p : players) {
            SendablePacket packet = new ParticlePacket(particle, vec.x(), vec.y(), vec.z(), 0, 0, 0, speed, count);
            p.sendPacket(packet);
        }
    }
    public static void spawnParticle(Pos pos, Particle particle, int count, float speed) {
        List<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers()
                .stream()
                .toList();
        for (Player p : players) {
            SendablePacket packet = new ParticlePacket(particle, pos.x(), pos.y(), pos.z(), 0, 0, 0, speed, count);
            p.sendPacket(packet);
        }
    }
}
