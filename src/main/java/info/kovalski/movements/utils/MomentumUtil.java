package info.kovalski.movements.utils;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MomentumUtil {

    private static final Map<UUID, Double> momentumMap = new HashMap<>();

    private static final double MAX_MOMENTUM = 2.0;
    private static final double MOMENTUM_INCREASE = 0.01;
    private static final double MOMENTUM_DECREASE = 0.05;

    private static final UUID MOMENTUM_SPEED_BOOST = UUID.fromString("c0d979e1-13f4-455f-ae84-b5c3f74d3230");

    public static void add(Player player, double amount) {
        double newValue = Math.min(MAX_MOMENTUM, get(player) + amount);
        set(player, newValue);
    }

    public static void set(Player player, double value) {
        value = Math.max(0.0, Math.min(MAX_MOMENTUM, value));
        momentumMap.put(player.getUniqueId(), value);
        applySpeedBoost(player, value);
    }

    public static double get(Player player) {
        return momentumMap.getOrDefault(player.getUniqueId(), 0.0);
    }

    public static double getMAX_MOMENTUM() {
        return MAX_MOMENTUM;
    }

    public static double getMOMENTUM_INCREASE() {
        return MOMENTUM_INCREASE;
    }

    public static double getMOMENTUM_DECREASE() {
        return MOMENTUM_DECREASE;
    }

    private static void applySpeedBoost(Player player, double momentum) {
        AttributeInstance attr = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (attr == null) return;

        // Remove old modifier
        attr.getModifiers().stream()
                .filter(mod -> mod.getUniqueId().equals(MOMENTUM_SPEED_BOOST))
                .forEach(attr::removeModifier);

        if (momentum > 0.01) {
            // Use ADD_NUMBER to bypass base * scalar limits
            double extra = Math.min(0.1 + momentum * 0.1, 0.3); // +0.2 to +0.3 = noticeably fast

// Base movement speed is 0.1
// Scale boost linearly based on current momentum out of 2.0
            double boostAmount = Math.min(0.07, momentum * 0.035); // Max boost = +0.07 at full momentum

            AttributeModifier modifier = new AttributeModifier(
                    MOMENTUM_SPEED_BOOST,
                    "MomentumSpeedBoost",
                    boostAmount,
                    AttributeModifier.Operation.ADD_NUMBER
            );
            attr.addModifier(modifier);
        }
    }

    public static void reset(Player player) {
        momentumMap.remove(player.getUniqueId());
        AttributeInstance attr = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (attr != null) {
            attr.getModifiers().stream()
                    .filter(mod -> mod.getUniqueId().equals(MOMENTUM_SPEED_BOOST))
                    .forEach(attr::removeModifier);
        }
    }
}
