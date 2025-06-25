package org.hyperoil.playifkillers.Listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.UnitModifier;
import org.hyperoil.playifkillers.Main;
import org.hyperoil.playifkillers.Utils.ChunkSaving;
import org.hyperoil.playifkillers.Utils.SerializationHelpers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class WorldGenerators {
    public static void overWorldGenerator(GenerationUnit unit) {
        UnitModifier modifier = unit.modifier();
        Point unitAbsoluteStart = unit.absoluteStart();
        Point unitAbsoluteEnd = unit.absoluteEnd();
        ObjectMapper mapper = new ObjectMapper();
        BlockVec absoluteStartBlock = new BlockVec(unitAbsoluteStart.blockX(),
                unitAbsoluteEnd.blockY(),
                unitAbsoluteStart.blockZ());
        Path saveFile = Paths.get(ChunkSaving.getSaveFileForVec(absoluteStartBlock));
        HashMap<BlockVec, Block> blocksSaved = new HashMap<>();
        try {
            blocksSaved = SerializationHelpers.deserializeBlocksSaved(
                    mapper.readValue(new FileReader(saveFile.toString()),
                            new TypeReference<>() {})
            );
        } catch (FileNotFoundException ignored) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int x = unitAbsoluteStart.blockX(); x < unitAbsoluteEnd.blockX(); x++) {
            for (int y = unitAbsoluteStart.blockY(); y < unitAbsoluteEnd.blockY(); y++) {
                for (int z = unitAbsoluteStart.blockZ(); z < unitAbsoluteEnd.blockZ(); z++) {
                    if (x == Main.SPAWN_POINT.blockX() && y == Main.SPAWN_POINT.blockY()-2 && z == Main.SPAWN_POINT.blockZ()) {
                        modifier.setBlock(x, y, z, Block.STONE);
                        continue;
                    }
                    BlockVec vec = new BlockVec(x, y, z);
                    Block blockType = blocksSaved.get(vec);
                    if (blockType != null) {
                        if (blockType == Block.AIR) continue;
                        modifier.setBlock(vec, blockType);
                    }
                }
            }
        }
    }
}
