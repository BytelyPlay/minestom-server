package org.hyperoil.playifkillers.Permissions;

import java.util.List;

public class Group implements PermissionHolder {
    @Override
    public boolean checkPermission(String perm) {
        return true;
    }

    @Override
    public void addPermission(String perm) {
        throw new IllegalCallerException("Not implemented.");
    }

    @Override
    public void removePermission(String perm) {
        throw new IllegalCallerException("Not implemented.");
    }

    @Override
    public List<String> getPermissions() {
        throw new IllegalCallerException("Not implemented.");
    }
}
