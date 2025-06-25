package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.UnitModifier;
import org.hyperoil.playifkillers.Main;

public class WorldGenerators {
    public static void overWorldGenerator(GenerationUnit unit) {
        UnitModifier modifier = unit.modifier();
        Point unitAbsoluteStart = unit.absoluteStart();
        Point unitAbsoluteEnd = unit.absoluteEnd();
        for (int x = unitAbsoluteStart.blockX(); x < unitAbsoluteEnd.blockX(); x++) {
            for (int y = unitAbsoluteStart.blockY(); y < unitAbsoluteEnd.blockY(); y++) {
                for (int z = unitAbsoluteStart.blockZ(); z < unitAbsoluteEnd.blockZ(); z++) {
                    if (y <= 0) {
                        if (y == 0) {
                            modifier.setBlock(x, y, z, Block.GRASS_BLOCK);
                        }
                        continue;
                    }
                    BlockVec vec = new BlockVec(x, y, z);
                    Block blockType = Main.blocksSaved.get(vec);
                    if (blockType != null) {
                        if (blockType == Block.AIR) continue;
                        modifier.setBlock(vec, blockType);
                    }
                }
            }
        }
    }
}
