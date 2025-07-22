package org.hyperoil.playifkillers.Utils;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;
import java.util.List;

public class Box {
    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;

    public Box(int givenX1, int givenY1, int givenZ1, int givenX2, int givenY2, int givenZ2) {
        x1 = givenX1;
        y1 = givenY1;
        z1 = givenZ1;
        x2 = givenX2;
        y2 = givenY2;
        z2 = givenZ2;
    }

    public Box(Point start, Point end) {
        this(start.blockX(), start.blockY(), start.blockZ(),
                end.blockX(), end.blockY(), end.blockZ());
    }

    public List<BlockVec> getAllBlocks() {
        ArrayList<BlockVec> blocks = new ArrayList<>();
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    blocks.add(new BlockVec(x, y, z));
                }
            }
        }
        return List.copyOf(blocks);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s - %s %s %s", x1, y1, z1, x2, y2, z2);
    }
}
