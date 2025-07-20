package org.hyperoil.playifkillers.Permissions;

import org.hyperoil.playifkillers.Utils.Enums.Permission;

import java.util.ArrayList;
import java.util.UUID;

public class User extends PermissionHolder {
    private final ArrayList<Group> groups = new ArrayList<>();
    private final UUID player;

    public User(UUID uuid) {
        this.player = uuid;
        // TODO: Make this a database retrieval thing... or better yet make a different class retrieve the thing and this class just being bombarded with addPermission and addGroup calls...
        if (player.toString().equals("bcbaabb3-f21a-4927-94ad-2979c54f67fc")) this.addGroup(Group.ADMIN);
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
