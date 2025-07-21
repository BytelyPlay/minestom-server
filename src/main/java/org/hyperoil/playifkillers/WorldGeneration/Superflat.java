package org.hyperoil.playifkillers.WorldGeneration;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.instance.generator.UnitModifier;
import org.hyperoil.playifkillers.Utils.Box;
import org.jetbrains.annotations.NotNull;

public class Superflat implements Generator {
    @Override
    public void generate(@NotNull GenerationUnit unit) {
        UnitModifier modifier = unit.modifier();

        Point start = unit.absoluteStart();
        Point end = unit.absoluteEnd();

        for (int x = start.blockX(); x < end.blockX(); x++) {
            for (int z = start.blockZ(); z < end.blockZ(); z++) {
                modifier.setBlock(x, 10, z, Block.GRASS_BLOCK);
                modifier.setBlock(x, 9, z, Block.DIRT);
                modifier.setBlock(x, 8, z, Block.DIRT);
                modifier.setBlock(x, 7, z, Block.DIRT);
                modifier.setBlock(x, 6, z, Block.BEDROCK);
            }
        }
    }
}
