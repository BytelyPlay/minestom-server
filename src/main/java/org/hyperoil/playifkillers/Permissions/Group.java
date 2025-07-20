package org.hyperoil.playifkillers.Permissions;

import org.hyperoil.playifkillers.Utils.Enums.Permission;

import java.util.List;

public enum Group {
    ADMIN(1, List.of(Permission.WILDCARD));

    private final List<Permission> permissions;
    private final int identifier;

    Group(int ID, List<Permission> perms) {
        permissions = perms;
        identifier = ID;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public int getIdentifier() {
        return identifier;
    }

    public boolean hasPermission(Permission perm) {
        return permissions.contains(perm) || permissions.contains(Permission.WILDCARD);
    }
}
