package org.hyperoil.playifkillers.Utils.Enums;

public enum Action {
    BLOCK_BREAK(1, "blockBreak", Permission.BLOCK_BREAK_BYPASS),
    BLOCK_PLACE(2, "blockPlace", Permission.BLOCK_PLACE_BYPASS),
    PVP(3, "PVP", Permission.PVP_BYPASS),
    PVE(4, "PVE", Permission.PVE_BYPASS),
    ITEM_PICKUP(5, "itemPickup", Permission.ITEM_PICKUP_BYPASS),
    USE_SPAWN_EGGS(6, "useSpawnEggs", Permission.SPAWN_EGG_BYPASS);

    private final int ID;
    private final String readableID;
    private final Permission bypassPermission;

    Action(int id, String readableIdentifier, Permission bypassPerm) {
        ID = id;
        readableID = readableIdentifier;
        bypassPermission = bypassPerm;

    }

    public int getID() {
        return ID;
    }

    public String getReadableID() {
        return readableID;
    }

    public Permission getBypassPermission() {
        return bypassPermission;
    }
}
