package org.hyperoil.playifkillers.Minestom;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.Section;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.instance.heightmap.Heightmap;
import net.minestom.server.instance.palette.Palette;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Arrays;
import java.util.List;

public class CChunk extends Chunk {
    private Section[] sections = new Section[16];
    private long chunkTime = 0;
    private Heightmap motionBlockingHeightMap = null;
    public CChunk(@NotNull Instance instance, int chunkX, int chunkZ, boolean shouldGenerate) {
        super(instance, chunkX, chunkZ, shouldGenerate);
    }
    private CChunk(@NotNull Instance instance, int chunkX, int chunkZ, Section[] sects, long time) {
        super(instance, chunkX, chunkZ, false);
        sections = sects;
        chunkTime = time;
    }

    @Override
    protected void setBlock(int x, int y, int z, @NotNull Block block, BlockHandler.@Nullable Placement placement, BlockHandler.@Nullable Destroy destroy) {
        int sectionY = y >> 4;
        int localY = y & 0xF;

        if (sections[sectionY] == null) {
            sections[sectionY] = new Section();
        }
        Palette pal = sections[sectionY].blockPalette();
        pal.set(x, localY, z, block.id());
    }

    @Override
    public @NotNull List<Section> getSections() {
        return List.of(sections);
    }

    @Override
    public @NotNull Section getSection(int section) {
        return sections[section];
    }

    @Override
    public @NotNull Heightmap motionBlockingHeightmap() {
        Object obj = null;
        return (Heightmap) obj;
    }

    @Override
    public @NotNull Heightmap worldSurfaceHeightmap() {
        Object obj = null;
        return (Heightmap) obj;
    }

    @Override
    public void loadHeightmapsFromNBT(CompoundBinaryTag heightmaps) {

    }

    @Override
    public void tick(long time) {
        chunkTime = time;
    }

    @Override
    public long getLastChangeTime() {
        return chunkTime;
    }

    @Override
    public @NotNull SendablePacket getFullDataPacket() {
        Object obj = null;
        return (SendablePacket) obj;
    }

    @Override
    public @NotNull Chunk copy(@NotNull Instance instance, int chunkX, int chunkZ) {
        return new CChunk(instance, chunkX, chunkZ, sections, chunkTime);
    }

    @Override
    public void reset() {
        sections = new Section[16];
        chunkTime = 0;
    }

    @Override
    public void invalidate() {
        // No caches...
    }

    @Override
    public @UnknownNullability Block getBlock(int x, int y, int z, @NotNull Condition condition) {
        int localY = y & 0xF;

        Palette pal = getBlockPalette(y);
        pal.get(x, localY, z);
        Object obj = null;
        return (Block) obj;
    }

    @Override
    public @NotNull RegistryKey<Biome> getBiome(int x, int y, int z) {
        return () -> Key.key("minecraft:plains");
    }

    @Override
    public void setBiome(int x, int y, int z, @NotNull RegistryKey<Biome> biome) {

    }

    private Palette getBlockPalette(int y) {
        int sectionY = y >> 4;

        if (sections[sectionY] == null) {
            sections[sectionY] = new Section();
        }
        return sections[sectionY].blockPalette();
    }
}
