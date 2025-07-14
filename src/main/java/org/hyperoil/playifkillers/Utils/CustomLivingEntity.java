package org.hyperoil.playifkillers.Utils;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import org.jetbrains.annotations.NotNull;

public class CustomLivingEntity extends LivingEntity implements CustomHealthSystem {
    private double customHealth = 0;
    private double customMaxHealth = 0;

    public CustomLivingEntity(@NotNull EntityType entityType) {
        super(entityType);
    }

    @Override
    public void setHealth(float health) {
        setCustomHealth(health);
    }

    @Override
    public float getHealth() {
        return (float) this.getCustomHealth();
    }

    @Override
    public double getCustomHealth() {
        return customHealth;
    }

    @Override
    public double getCustomMaxHealth() {
        return customMaxHealth;
    }

    @Override
    public void setCustomHealth(double amount) {
        if (amount <= 0 && !isDead) this.kill();
        if (customMaxHealth < amount) {
            customMaxHealth = amount;
        }
        customHealth = amount;
    }
    @Override
    public void setCustomMaxHealth(double amount) {
        AttributeInstance attribute = this.getAttribute(Attribute.MAX_HEALTH);
        if (attribute.getBaseValue() != getCustomMaxHealth()) {
            this.getAttribute(Attribute.MAX_HEALTH).setBaseValue(amount);
        }
        customMaxHealth = amount;
    }

    @Override
    protected void onAttributeChanged(@NotNull AttributeInstance attribute) {
        super.onAttributeChanged(attribute);
        if (attribute.attribute() == Attribute.MAX_HEALTH) {
            customMaxHealth = attribute.getBaseValue();
        }
    }
}
