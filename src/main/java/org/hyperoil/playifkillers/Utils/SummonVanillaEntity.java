package org.hyperoil.playifkillers.Utils;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.Instance;

public class SummonVanillaEntity {
    // TODO: this entire class...
    public static void summonEntity(EntityType type, Pos pos, Instance inst) {
        Entity entity = new Entity(type);
        entity.setInstance(inst, pos);
    }
}
