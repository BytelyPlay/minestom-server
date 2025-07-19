package org.hyperoil.playifkillers.Utils.Enums;

public enum Action {
    blockBreak(1, "blockBreak"),
    blockPlace(2, "blockPlace"),
    PVP(3, "PVP"),
    PVE(4, "PVE");

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
