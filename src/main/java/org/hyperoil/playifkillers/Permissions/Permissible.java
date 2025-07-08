package org.hyperoil.playifkillers.Permissions;

import java.util.List;

public interface Permissible {
    // * is a wildcard and will just produce true.
    boolean checkPermission(String perm);
    void addPermission(String perm);
    void removePermission(String perm);
    List<String> getPermissions();
}
