package org.hyperoil.playifkillers.Utils;

import org.hyperoil.playifkillers.Minestom.CPlayer;
import org.hyperoil.playifkillers.Utils.Enums.Action;
import org.hyperoil.playifkillers.Utils.Enums.RuleValue;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public class ActionAllowed {
    public final RuleValue treatUnsetAs;
    private final ConcurrentHashMap<Action, RuleValue> ruleValueHashMap;

    public ActionAllowed(@NotNull RuleValue treatUnset, @NotNull ConcurrentHashMap<Action, RuleValue> rules) {
        if (treatUnset == RuleValue.UNSET) throw new IllegalArgumentException("treatUnset cannot be RuleValue.UNSET");
        treatUnsetAs = treatUnset;
        ruleValueHashMap = rules;
    }

    public RuleValue getRule(CPlayer p, Action action) {
        if (p.hasPermission(action.getBypassPermission())) {
            return RuleValue.ALLOW;
        } else {
            return getDefaultRuleValue(action);
        }
    }

    private RuleValue getDefaultRuleValue(Action action) {
        return ruleValueHashMap.computeIfAbsent(action, act -> RuleValue.UNSET);
    }

    public boolean getShouldDeny(CPlayer p, Action action) {
        RuleValue rule = getRule(p, action);
        if (rule == RuleValue.UNSET) {
            if (treatUnsetAs == RuleValue.UNSET) {
                throw new IllegalArgumentException("treatUnsetAs Cannot be RuleValue.UNSET, acting as if it was RuleValue.DENY");
            } else {
                return treatUnsetAs != RuleValue.ALLOW;
            }
        }
        return rule != RuleValue.ALLOW;
    }
    public boolean getShouldAllow(RuleValue rule) {
        if (rule == RuleValue.UNSET) {
            if (treatUnsetAs == RuleValue.UNSET) {
                throw new IllegalArgumentException("treatUnsetAs Cannot be RuleValue.UNSET.");
            } else {
                return treatUnsetAs == RuleValue.ALLOW;
            }
        }
        return rule == RuleValue.ALLOW;
    }
}
