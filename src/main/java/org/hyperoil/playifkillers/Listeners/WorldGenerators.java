package org.hyperoil.playifkillers.Listeners;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.UnitModifier;

public class WorldGenerators {
    private static final Point START_STONE_POINT = new Vec(20, 39, 20);
    private static final Point END_STONE_POINT = new Vec(40, 50, 40);
    public static void overWorldGenerator(GenerationUnit unit) {
        UnitModifier modifier = unit.modifier();
        Point unitAbsoluteStart = unit.absoluteStart();
        Point unitAbsoluteEnd = unit.absoluteEnd();
        // make this more efficient for god's sake and make it work
        for (double x = START_STONE_POINT.x(); x < unitAbsoluteEnd.x() && x > unitAbsoluteStart.x() && x < END_STONE_POINT.x(); x++) {
            for (double y = START_STONE_POINT.y(); y < unitAbsoluteEnd.y() && y > unitAbsoluteStart.y() && y < END_STONE_POINT.y(); y++) {
                for (double z = START_STONE_POINT.z(); z < unitAbsoluteEnd.z() && z > unitAbsoluteStart.z() && z < END_STONE_POINT.z(); z++) {
                    if (x == START_STONE_POINT.x() || x == END_STONE_POINT.x() ||
                            y == START_STONE_POINT.y() || y == END_STONE_POINT.y() ||
                            z == START_STONE_POINT.z() || z == END_STONE_POINT.z()) {
                        modifier.setBlock((int) x, (int) y, (int) z, Block.STONE);
                    }
                }
            }
        }
    }
}
