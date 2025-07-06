package org.hyperoil.playifkillers.Utils;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkSaving {
    // TODO: Add NBT support.
    private static final String saveFileChunkCoordSeparator = "-";
    private static final Path savesFolder = Paths.get("./save");
    private static final int REGION_VERSION = 2;
    private static final byte[] IDENTIFIER_BYTES = getIdentifierBytes();
    private static final Logger log = LoggerFactory.getLogger(ChunkSaving.class);
    private static ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    public static void saveChunk(Chunk chunk) {
        int chunkX = chunk.getChunkX();
        int chunkZ = chunk.getChunkZ();
        int regionX = Math.floorDiv(chunkX, 32);
        int regionZ = Math.floorDiv(chunkZ, 32);
        int relaChunkX = Math.floorMod(chunkX, 32);
        int relaChunkZ = Math.floorMod(chunkZ, 32);
        Path regionFile = Paths.get(getSaveFile(regionX, regionZ));
        final boolean isNew = !Files.exists(regionFile);
        try {
            if (!Files.exists(savesFolder)) Files.createDirectories(savesFolder);
            if (isNew) Files.createFile(regionFile);
            synchronized (getLock(regionFile.toString())) {
                try (RandomAccessFile raf = new RandomAccessFile(regionFile.toString(), "rw")) {
                    if (isNew) {
                        // Header to make sure it's actually a region file and some data:
                        // START HEADER

                        // START IDENTIFIER
                        raf.write(IDENTIFIER_BYTES);
                        // END IDENTIFIER

                        // START VERSION
                        raf.write(REGION_VERSION);
                        // END VERSION

                        // END HEADER
                    } else {
                        raf.seek(9);
                    }

                    // Writing the actual data
                    // START WRITING DATA
                    for (int relChunkX = 0; relChunkX < 32; relChunkX++) {
                        for (int relChunkZ = 0; relChunkZ < 32; relChunkZ++) {
                            if (relChunkX == relaChunkX && relChunkZ == relaChunkZ) {
                                log.info("HEHE2");
                                raf.write(0x91);
                                for (int x = 0; x < 16; x++) {
                                    for (int y = 0; y < 16; y++) {
                                        for (int z = 0; z < 16; z++) {
                                            Block b = chunk.getBlock(x, y, z);
                                            if (b.isAir()) {
                                                raf.write(0xF5);
                                                continue;
                                            } else {
                                                raf.write(0x09);
                                            }
                                            if (b.hasNbt())
                                                ChunkSaving.log.warn("NBT Block detected while saving chunks but we do not support NBT (yet)...");

                                            raf.write(x);
                                            raf.write(y);
                                            raf.write(z);

                                            raf.writeShort(b.id());

                                            log.info("save: {} {} {} {}", x, y, z, b.id());
                                        }
                                    }
                                }
                            } else {
                                log.info("HEHE1");
                                if (isNew) raf.write(0xF1);
                            }
                        }
                    }
                    // END WRITING DATA
                }
            }
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException caught although checked for file stacktrace: ");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getSaveFile(int regionX, int regionZ) {
        // replace overworld with some instance name.
        return "./save/" + "overworld" + saveFileChunkCoordSeparator + regionX + saveFileChunkCoordSeparator + regionZ + ".r32";
    }
    public static @Nullable HashMap<BlockVec, Block> loadChunk(int chunkX, int chunkZ) {
        HashMap<BlockVec, Block> blocksSaved = new HashMap<>();
        int regionX = Math.floorDiv(chunkX, 32);
        int regionZ = Math.floorDiv(chunkZ, 32);
        int relaChunkX = Math.floorMod(chunkX, 32);
        int relaChunkZ = Math.floorMod(chunkZ, 32);
        Path savePath = Paths.get(getSaveFile(regionX, regionZ));
        synchronized (getLock(savePath.toString())) {
            try (RandomAccessFile raf = new RandomAccessFile(savePath.toString(), "r")) {
                byte[] identifier = new byte[8];
                raf.readFully(identifier);
                if (Arrays.equals(identifier, IDENTIFIER_BYTES)) {
                    if (raf.read() == REGION_VERSION) {
                        for (int relChunkX = 0; relChunkX < 32; relChunkX++) {
                            for (int relChunkZ = 0; relChunkZ < 32; relChunkZ++) {
                                if (relChunkX == relaChunkX && relChunkZ == relaChunkZ) {
                                    int marker = raf.read();
                                    if (marker == 0xF1) return null;
                                    if (marker != 0x91) {
                                        log.warn("marker != 0x91 and isn't 0xF1 could be corrupted... returning null... marker: {}", marker);
                                        return null;
                                    }
                                    for (int x = 0; x < 16; x++) {
                                        for (int y = 0; y < 16; y++) {
                                            for (int z = 0; z < 16; z++) {
                                                int nextByte = raf.read();
                                                if (nextByte == 0xF5) continue;
                                                if (nextByte != 0x09) {
                                                    log.warn("nextByte != 0xF5 && nextByte != 0x09 something might be corrupted continuing what the byte is: {}", nextByte);
                                                    continue;
                                                }

                                                int bX = raf.read();
                                                int bY = raf.read();
                                                int bZ = raf.read();

                                                int blockID = raf.readShort();

                                                log.info("load: {} {} {} {}", bX, bY, bZ, blockID);

                                                blocksSaved.put(new BlockVec(bX, bY, bZ), Block.fromBlockId(blockID));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    private static Object getLock(String file) {
        return locks.computeIfAbsent(file, s -> new Object());
    }
}
