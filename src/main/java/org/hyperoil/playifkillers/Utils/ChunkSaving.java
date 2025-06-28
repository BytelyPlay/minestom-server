package org.hyperoil.playifkillers.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ChunkSaving {
    private static final String saveFileChunkCoordSeparator = "-";
    private static final Path savesFolder = Paths.get("./save");
    private static final int REGION_VERSION = 1;
    private static final byte[] INTEGER_COMING_BYTES = getIntegerComingBytes();
    private static final byte[] IDENTIFIER_BYTES = getIdentifierBytes();

    public static void saveChunk(Chunk chunk) {
        int chunkX = chunk.getChunkX();
        int chunkZ = chunk.getChunkZ();
        int regionX = Math.floorDiv(chunkX, 32);
        int regionZ = Math.floorDiv(chunkZ, 32);
        String saveFile = getSaveFileForChunk(chunk);
        Path regionFile = Paths.get(saveFile);
        try {
            if (!Files.exists(savesFolder)) Files.createDirectories(savesFolder);
            if (!Files.exists(regionFile)) Files.createFile(regionFile);
            try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(saveFile))) {
                // Header to make sure it's actually a region file and some data:
                // START HEADER

                // START IDENTIFIER
                outputStream.write(IDENTIFIER_BYTES);
                // END IDENTIFIER

                // START VERSION
                outputStream.write(INTEGER_COMING_BYTES);
                outputStream.write(REGION_VERSION);
                // END VERSION

                // END HEADER

                // Writing the actual data
                // START WRITING DATA
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            Block b = chunk.getBlock(x, y, z);
                            if (b.isAir()) continue;
                            if (b.hasNbt()) System.out.println("NBT Block detected while saving chunks but we do not support NBT (yet)...");

                            outputStream.write(INTEGER_COMING_BYTES);
                            outputStream.write(x);
                            outputStream.write(INTEGER_COMING_BYTES);
                            outputStream.write(y);
                            outputStream.write(INTEGER_COMING_BYTES);
                            outputStream.write(z);

                            outputStream.write(INTEGER_COMING_BYTES);
                            outputStream.write(b.id());
                        }
                    }
                }
                // END WRITING DATA
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException caught although checked for file stacktrace:");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getSaveFileForChunk(Chunk chunk) {
        return getSaveFile(chunk.getChunkX(), chunk.getChunkZ());
    }

    private static String getSaveFileForVec(BlockVec vec) {
        return getSaveFile(vec.chunkX(), vec.chunkZ());
    }
    private static String getSaveFile(int chunkX, int chunkZ) {
        // replace overworld with some instance name.
        return "./save/" + "overworld" + saveFileChunkCoordSeparator + chunkX + saveFileChunkCoordSeparator + chunkZ + ".json";
    }
    public static @Nullable HashMap<BlockVec, Block> loadChunk(int chunkX, int chunkZ) {
        Path savePath = Paths.get(getSaveFile(chunkX, chunkZ));
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(savePath.toString()))) {
            byte[] nextBytes;
            boolean identified = false;
            boolean integerComing = false;
            while (inputStream.available() > 0) {
                // 8 bytes is the amount of bytes per identifier and works well with integers.
                nextBytes = inputStream.readNBytes(8);
                if (!identified) {
                    if (nextBytes != IDENTIFIER_BYTES) {
                        System.out.println("Not a region file.");
                        return null;
                    } else {
                        identified = true;
                    }
                }
                if (integerComing) {
                    integerComing = false;
                    inputStream.read();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static byte[] getIntegerComingBytes() {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (i * 2 + 9 / 2);
        }
        return bytes;
    }
    private static byte[] getIdentifierBytes() {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (i * 3 + 2);
        }
        return bytes;
    }
    // please supply 8 bytes only
    private static boolean isIntegerComing(byte[] bytes) {
        return bytes.length == 8 && INTEGER_COMING_BYTES == bytes;
    }
}
