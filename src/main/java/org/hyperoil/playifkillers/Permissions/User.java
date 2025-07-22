package org.hyperoil.playifkillers.Permissions;

import org.hyperoil.playifkillers.Utils.Enums.Permission;

import java.util.ArrayList;
import java.util.UUID;

public class User extends PermissionHolder {
    private final ArrayList<Group> groups = new ArrayList<>();
    private final UUID player;

    public User(UUID uuid) {
        this.player = uuid;
    }

    @Override
    public boolean hasPermission(Permission perm) {
        for (Group group : groups) {
            if (group.hasPermission(perm)) return true;
        }
        return super.hasPermission(perm);
    }

    public void addGroup(Group group) {
        groups.add(group);
    }
}
