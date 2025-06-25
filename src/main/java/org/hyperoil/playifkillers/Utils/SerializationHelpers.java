package org.hyperoil.playifkillers.Utils;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class SerializationHelpers {
    private static final Logger log = LoggerFactory.getLogger(SerializationHelpers.class);

    public static HashMap<String, String> seralizeBlocksSaved(ConcurrentHashMap<BlockVec, Block> blocksSaved) {
        HashMap<String, String> serializableHashMap = new HashMap<>();

        for (BlockVec vec : blocksSaved.keySet()) {
            int blockX = vec.blockX();
            int blockY = vec.blockY();
            int blockZ = vec.blockZ();

            String blockID = blocksSaved.get(vec).name();
            serializableHashMap.put(blockX + " " + blockY + " " + blockZ, blockID);
        }
        return serializableHashMap;
    }
    public static ConcurrentHashMap<BlockVec, Block> deserializeBlocksSaved(HashMap<String, String> deserialized) {
        ConcurrentHashMap<BlockVec, Block> blocksSaved = new ConcurrentHashMap<>();

        for (String pos : deserialized.keySet()) {
            String[] posSplit = pos.split(" ");
            int x = Integer.parseInt(posSplit[0]);
            int y = Integer.parseInt(posSplit[1]);
            int z = Integer.parseInt(posSplit[2]);

            String type = deserialized.get(pos);

            Block blockType = Block.fromKey(type);

            if (blockType == null) {
                log.warn("a blocktype was null during serialization cannot read this block: {}", type);
                continue;
            }

            blocksSaved.put(new BlockVec(x, y, z), blockType);
        }

        return blocksSaved;
    }
}
