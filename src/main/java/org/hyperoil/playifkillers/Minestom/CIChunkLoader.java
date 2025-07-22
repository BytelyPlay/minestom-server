package org.hyperoil.playifkillers.Minestom;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.hyperoil.playifkillers.Main;
import org.hyperoil.playifkillers.Utils.ChunkSaving;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class CIChunkLoader implements IChunkLoader {
    @Override
    public @Nullable Chunk loadChunk(@NotNull Instance instance, int chunkX, int chunkZ) {
        // TODO: Add NBT support.
        HashMap<BlockVec, Block> blocksSaved = ChunkSaving.loadChunk(instance, chunkX, chunkZ);
        if (blocksSaved == null) return null;
        Chunk chunk = instance.getChunkSupplier().createChunk(instance, chunkX, chunkZ);
        for (BlockVec vec : blocksSaved.keySet()) {
            chunk.setBlock(vec.blockX(), vec.blockY(), vec.blockZ(), blocksSaved.get(vec));
        }
        return chunk;
    }

    @Override
    public void saveChunk(@NotNull Chunk chunk) {
        ChunkSaving.saveChunk(chunk);
    }
}
