package org.hyperoil.playifkillers.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ChunkSaving {
    private static final String saveFileChunkCoordSeparator = "-";

    public static void saveChunk(Chunk chunk) {
        HashMap<BlockVec, Block> saveHashMap = new HashMap<>();
        String saveFile = getSaveFileForChunk(chunk);
        for (int x = 0; x < 16; x++) {
            for (int y = -64; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    Block block = chunk.getBlock(x, y, z);
                    if (block.isAir()) continue;
                    saveHashMap.put(new BlockVec(x, y, z), block);
                }
            }
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            Path saveFilePath = Paths.get(saveFile);
            if (!Files.exists(saveFilePath)) {
                Path path = Paths.get("./save");
                if (!Files.exists(path)) Files.createDirectories(path);
                Files.createFile(saveFilePath);
            }
            mapper.writeValue(new FileWriter(saveFile), SerializationHelpers.serializeBlocksSaved(saveHashMap));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSaveFileForChunk(Chunk chunk) {
        return getSaveFile(chunk.getChunkX(), chunk.getChunkZ());
    }

    public static String getSaveFileForVec(BlockVec vec) {
        return getSaveFile(vec.chunkX(), vec.chunkZ());
    }
    public static String getSaveFile(int chunkX, int chunkZ) {
        // replace overworld with some instance name.
        return "./save/" + "overworld" + saveFileChunkCoordSeparator + chunkX + saveFileChunkCoordSeparator + chunkZ + ".json";
    }
}
