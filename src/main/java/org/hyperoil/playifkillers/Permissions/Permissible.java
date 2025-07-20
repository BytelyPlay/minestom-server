package org.hyperoil.playifkillers.Permissions;

import org.hyperoil.playifkillers.Utils.Enums.Permission;

import java.util.List;

public interface Permissible {
    // * is a wildcard and will just produce true.
    boolean hasPermission(Permission perm);
    void addPermission(Permission perm);
    void removePermission(Permission perm);
    List<Permission> getPermissions();
}
