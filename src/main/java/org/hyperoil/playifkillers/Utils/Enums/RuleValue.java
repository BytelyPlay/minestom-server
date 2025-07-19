package org.hyperoil.playifkillers.Utils.Enums;

public enum RuleValue {
    DENY(1),
    ALLOW(2),
    UNSET(3);

    private final int ID;

    RuleValue(int id) {
        ID = id;
    }

    public int getID() {
        return ID;
    }
}
