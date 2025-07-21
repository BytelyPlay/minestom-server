package org.hyperoil.playifkillers.Utils;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Section;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

public class ChunkSaving {
    // TODO: Add NBT support.
    private static final String saveFileChunkCoordSeparator = "-";
    private static final Path savesFolder = Paths.get("./save");
    private static final int REGION_VERSION = 1;
    private static final byte[] IDENTIFIER_BYTES = getIdentifierBytes();
    private static final Logger log = LoggerFactory.getLogger(ChunkSaving.class);

    public static void saveChunk(Chunk chunk) {
        String saveFile = getSaveFileForChunk(chunk);
        Path regionFile = Paths.get(saveFile);
        boolean chunkFullyAir = true;
        for (Section section : chunk.getSections()) {
            if (section.blockPalette().count() != 0) {
                chunkFullyAir = false;
                break;
            }
        }
        try {
            if (chunkFullyAir) {
                if (Files.exists(regionFile)) Files.delete(regionFile);
                return;
            }
            if (!Files.exists(savesFolder)) Files.createDirectories(savesFolder);
            if (!Files.exists(regionFile)) Files.createFile(regionFile);
            try (DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(saveFile)))) {
                // Header to make sure it's actually a region file and some data:
                // START HEADER

                // START IDENTIFIER
                outputStream.write(IDENTIFIER_BYTES);
                // END IDENTIFIER

                // START VERSION
                outputStream.write(REGION_VERSION);
                // END VERSION

                // END HEADER

                // Writing the actual data
                // START WRITING DATA
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            Block b = chunk.getBlock(x, y, z);
                            if (b.isAir()) {
                                outputStream.write(0xF5);
                                continue;
                            }
                            if (b.hasNbt()) ChunkSaving.log.warn("NBT Block detected while saving chunks but we do not support NBT (yet)...");

                            outputStream.writeInt(x);
                            outputStream.writeInt(y);
                            outputStream.writeInt(z);

                            outputStream.writeShort(b.id());
                        }
                    }
                }
                // END WRITING DATA
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException caught although checked for file stacktrace:");
            log.error("{}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        } catch (IOException e) {
            log.error("{}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
    }

    private static String getSaveFileForChunk(Chunk chunk) {
        return getSaveFile(chunk.getChunkX(), chunk.getChunkZ());
    }
    private static String getSaveFile(int chunkX, int chunkZ) {
        // replace overworld with some instance name.
        return "./save/" + "overworld" + saveFileChunkCoordSeparator + chunkX + saveFileChunkCoordSeparator + chunkZ + ".r1";
    }
    public static @Nullable HashMap<BlockVec, Block> loadChunk(int chunkX, int chunkZ) {
        HashMap<BlockVec, Block> blocksSaved = new HashMap<>();
        Path savePath = Paths.get(getSaveFile(chunkX, chunkZ));
        try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(savePath.toString())))) {
            if (Arrays.equals(inputStream.readNBytes(8), IDENTIFIER_BYTES)) {
                if (inputStream.read() == REGION_VERSION) {
                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < 16; z++) {
                                inputStream.mark(1);
                                if (inputStream.read() == 0xF5) {
                                    continue;
                                }
                                inputStream.reset();
                                blocksSaved.put(new BlockVec(inputStream.readInt(), inputStream.readInt(), inputStream.readInt()), Block.fromBlockId(inputStream.readUnsignedShort()));
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            log.error("{}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
        return blocksSaved;
    }
    private static byte[] getIdentifierBytes() {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (i * 3 + 2);
        }
        return bytes;
    }
}
