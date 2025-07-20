package org.hyperoil.playifkillers.Utils.Enums;

public enum Permission {
    WILDCARD(0),
    GAMEMODE_CREATIVE(1),
    GAMEMODE_SURVIVAL(2),
    FILL_COMMAND(3),
    BLOCK_BREAK_BYPASS(4),
    BLOCK_PLACE_BYPASS(5),
    PVP_BYPASS(6),
    PVE_BYPASS(7),
    ITEM_PICKUP_BYPASS(8),
    SETBLOCK_COMMAND(9);

    private final int ID;

    Permission(int identifier) {
        ID = identifier;
    }

    public int getID() {
        return ID;
    }
}
