package org.hyperoil.playifkillers.Utils.Enums;

public enum Action {
    BLOCK_BREAK(1, "blockBreak"),
    BLOCK_PLACE(2, "blockPlace"),
    PVP(3, "PVP"),
    PVE(4, "PVE"),
    ITEM_PICKUP(5, "ItemPickup");

    private final int ID;
    private final String readableID;

    Action(int id, String readableIdentifier) {
        ID = id;
        readableID = readableIdentifier;
    }

    public int getID() {
        return ID;
    }

    public String getReadableID() {
        return readableID;
    }
}
