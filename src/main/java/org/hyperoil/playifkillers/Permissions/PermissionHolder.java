package org.hyperoil.playifkillers.Permissions;

import org.hyperoil.playifkillers.Utils.Enums.Permission;

import java.util.ArrayList;
import java.util.List;

public class PermissionHolder implements Permissible {
    private final ArrayList<Permission> permissions = new ArrayList<>();

    @Override
    public boolean hasPermission(Permission perm) {
        return permissions.contains(perm) || permissions.contains(Permission.WILDCARD);
    }

    @Override
    public void addPermission(Permission perm) {
        permissions.add(perm);
    }

    @Override
    public void removePermission(Permission perm) {
        permissions.remove(perm);
    }

    @Override
    public List<Permission> getPermissions() {
        return List.copyOf(permissions);
    }
}
