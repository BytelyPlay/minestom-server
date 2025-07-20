package org.hyperoil.playifkillers.Permissions;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class User implements PermissionHolder {
    private static HashMap<UUID, User> userHashMap = new HashMap<>();
    private final UUID player;

    public static User getUser(UUID uuid) {
        return userHashMap.computeIfAbsent(uuid, User::new);
    }
    public User(UUID uuid) {
        this.player = uuid;
    }
    @Override
    public boolean hasPermission(String perm) {
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
