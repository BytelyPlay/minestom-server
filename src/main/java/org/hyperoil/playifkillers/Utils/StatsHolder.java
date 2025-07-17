package org.hyperoil.playifkillers.Utils;

public interface StatsHolder {
    // Regular
    double getStrength();
    void addStrength(double amount);
    void subtractStrength(double amount);

    double getCritDamage();
    void addCritDamage(double amount);
    void subtractCritDamage(double amount);

    double getCritChance();
    void addCritChance(double amount);
    void subtractCritChance(double amount);

    double getHealthStat();
    void addHealth(double amount);
    void subtractHealth(double amount);

    double getDefense();
    void addDefense(double amount);
    void subtractDefense(double amount);

    double getSpeed();
    void addSpeed(double amount);
    void subtractSpeed(double amount);

    double getIntelligence();
    void addIntelligence(double amount);
    void subtractIntelligence(double amount);

    double getAttackSpeed();
    void addAttackSpeed(double amount);
    void subtractAttackSpeed(double amount);

    double getTrueDefense();
    void addTrueDefense(double amount);
    void subtractTrueDefense(double amount);

    double getFerocity();
    void addFerocity(double amount);
    void subtractFerocity(double amount);

    double getAbilityDamage();
    void addAbilityDamage(double amount);
    void subtractAbilityDamage(double amount);

    // Total
    double getTotalStrength();
    double getTotalCritDamage();
    double getTotalCritChance();
    double getTotalHealth();
    double getTotalDefense();
    double getTotalSpeed();
    double getTotalIntelligence();
    double getTotalAttackSpeed();
    double getTotalTrueDefense();
    double getTotalFerocity();
    double getTotalAbilityDamage();

    // Perma
    double getPermaStrength();
    void addPermaStrength(double amount);
    void subtractPermaStrength(double amount);

    double getPermaCritDamage();
    void addPermaCritDamage(double amount);
    void subtractPermaCritDamage(double amount);

    double getPermaCritChance();
    void addPermaCritChance(double amount);
    void subtractPermaCritChance(double amount);

    double getPermaHealth();
    void addPermaHealth(double amount);
    void subtractPermaHealth(double amount);

    double getPermaDefense();
    void addPermaDefense(double amount);
    void subtractPermaDefense(double amount);

    double getPermaSpeed();
    void addPermaSpeed(double amount);
    void subtractPermaSpeed(double amount);

    double getPermaIntelligence();
    void addPermaIntelligence(double amount);
    void subtractPermaIntelligence(double amount);

    double getPermaAttackSpeed();
    void addPermaAttackSpeed(double amount);
    void subtractPermaAttackSpeed(double amount);

    double getPermaTrueDefense();
    void addPermaTrueDefense(double amount);
    void subtractPermaTrueDefense(double amount);

    double getPermaFerocity();
    void addPermaFerocity(double amount);
    void subtractPermaFerocity(double amount);

    double getPermaAbilityDamage();
    void addPermaAbilityDamage(double amount);
    void subtractPermaAbilityDamage(double amount);
}
