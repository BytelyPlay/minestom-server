package org.hyperoil.playifkillers.Utils;

import net.minestom.server.entity.Player;
import org.hyperoil.playifkillers.Permissions.User;
import org.hyperoil.playifkillers.Utils.Enums.Action;
import org.hyperoil.playifkillers.Utils.Enums.RuleValue;

import java.util.concurrent.ConcurrentHashMap;

public class ActionAllowed {
    public static final RuleValue TREAT_UNSET_AS = RuleValue.DENY;
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

    public static boolean getShouldAllow(Player p, Action action) {
        RuleValue rule = getRule(p, action);
        if (rule == RuleValue.UNSET) {
            if (TREAT_UNSET_AS == RuleValue.UNSET) {
                throw new IllegalArgumentException("TREAT_UNSET_AS Cannot be RuleValue.UNSET, acting as if it was RuleValue.DENY");
            } else {
                return TREAT_UNSET_AS == RuleValue.ALLOW;
            }
        }
        return rule == RuleValue.ALLOW;
    }
    public static boolean getShouldAllow(RuleValue rule) {
        if (rule == RuleValue.UNSET) {
            if (TREAT_UNSET_AS == RuleValue.UNSET) {
                throw new IllegalArgumentException("TREAT_UNSET_AS Cannot be RuleValue.UNSET, acting as if it was RuleValue.DENY");
            } else {
                return TREAT_UNSET_AS == RuleValue.ALLOW;
            }
        }
        return rule == RuleValue.ALLOW;
    }
}
