package org.hyperoil.playifkillers.Utils;

import net.minestom.server.entity.Player;
import org.hyperoil.playifkillers.Permissions.User;
import org.hyperoil.playifkillers.Utils.Enums.Action;
import org.hyperoil.playifkillers.Utils.Enums.RuleValue;

import java.util.concurrent.ConcurrentHashMap;

public class ActionAllowed {
    // TODO: Fully implement
    private static ConcurrentHashMap<Action, RuleValue> ruleValueHashMap = new ConcurrentHashMap<>();
    public static RuleValue getRule(Player p, Action action) {
        User user = User.getUser(p.getUuid());
        if (user.hasPermission("hyperoil.bypass.control." + action.getReadableID()) || user.hasPermission("hyperoil.bypass.control.*")) {
            return RuleValue.ALLOW;
        } else {
            return getDefaultRuleValue(action);
        }
    }

    private static RuleValue getDefaultRuleValue(Action action) {
        return ruleValueHashMap.computeIfAbsent(action, act -> RuleValue.UNSET);
    }
}
