package info.kovalski.movements.utils;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MomentumUtil {

    private static final Map<UUID, Double> momentumMap = new HashMap<>();

    private static final double MAX_MOMENTUM = 2.0;
    private static final double MOMENTUM_INCREASE = 0.01;
    private static final double MOMENTUM_DECREASE = 0.05;

    private static final NamespacedKey MOMENTUM_SPEED_BOOST = new NamespacedKey("movements", "momentum_speed");

    public static void add(Player player, double amount) {
        if (!Double.isFinite(amount)) return;
        double newValue = Math.min(MAX_MOMENTUM, get(player) + amount);
        set(player, newValue);
    }

    public static void set(Player player, double value) {
        if (!Double.isFinite(value)) value = 0.0;
        value = Math.max(0.0, Math.min(MAX_MOMENTUM, value));
        double current = get(player);
        AttributeInstance attribute = player.getAttribute(Attribute.MOVEMENT_SPEED);
        boolean hasSpeedModifier = attribute != null && attribute.getModifier(MOMENTUM_SPEED_BOOST) != null;
        if (Double.compare(current, value) == 0
                && (attribute == null || (value > 0.01) == hasSpeedModifier)) {
            return;
        }

        momentumMap.put(player.getUniqueId(), value);
        applySpeedBoost(player, value);
    }

    public static double get(Player player) {
        return momentumMap.getOrDefault(player.getUniqueId(), 0.0);
    }

    public static double getMaxMomentum() {
        return MAX_MOMENTUM;
    }

    public static double getMomentumIncrease() {
        return MOMENTUM_INCREASE;
    }

    public static double getMomentumDecrease() {
        return MOMENTUM_DECREASE;
    }

    private static void applySpeedBoost(Player player, double momentum) {
        AttributeInstance attr = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (attr == null) return;

        attr.removeModifier(MOMENTUM_SPEED_BOOST);

        if (momentum > 0.01) {
            double boostAmount = Math.min(0.07, momentum * 0.035); // Max boost = +0.07 at full momentum

            AttributeModifier modifier = new AttributeModifier(
                    MOMENTUM_SPEED_BOOST,
                    boostAmount,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.ANY
            );
            attr.addTransientModifier(modifier);
        }
    }

    public static void reset(Player player) {
        momentumMap.remove(player.getUniqueId());
        AttributeInstance attr = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (attr != null) {
            attr.removeModifier(MOMENTUM_SPEED_BOOST);
        }
    }
}
