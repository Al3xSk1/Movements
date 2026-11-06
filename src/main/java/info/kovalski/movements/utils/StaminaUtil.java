package info.kovalski.movements.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class StaminaUtil {

    private static final Map<UUID, Double> stamina = new HashMap<>();
    private static final Map<UUID, Double> maxStamina = new HashMap<>();

    private static final double DEFAULT_MAX_STAMINA = 100.0;

    private StaminaUtil() {}

    // Get the player's current stamina.
    public static double getStamina(Player p) {
        return stamina.getOrDefault(p.getUniqueId(), DEFAULT_MAX_STAMINA);
    }

    // Set stamina within the player's configured range.
    public static void setStamina(Player p, double value) {
        if (!Double.isFinite(value)) return;
        stamina.put(p.getUniqueId(), Math.max(0.0, Math.min(getMaxStamina(p), value)));
    }

    // Get the player's maximum stamina.
    public static double getMaxStamina(Player p) {
        return maxStamina.getOrDefault(p.getUniqueId(), DEFAULT_MAX_STAMINA);
    }

    // Set the player's maximum stamina and clamp the current value.
    public static void setMaxStamina(Player p, double value) {
        double newMax = Double.isFinite(value) ? Math.max(1.0, value) : DEFAULT_MAX_STAMINA;
        maxStamina.put(p.getUniqueId(), newMax);
        setStamina(p, getStamina(p));
    }

    // Consume stamina if enough is available.
    public static boolean consume(Player p, double amount) {
        if (!Double.isFinite(amount) || amount < 0.0) return false;
        double current = getStamina(p);
        if (current < amount) return false;

        if (amount == 0.0) return true;
        setStamina(p, current - amount);

        StaminaHUD.updateStaminaBar(p);

        return true;
    }

    // Check whether the player can afford an action.
    public static boolean has(Player p, double amount) {
        return Double.isFinite(amount) && amount >= 0.0 && getStamina(p) >= amount;
    }

    // Regenerate stamina up to the configured maximum.
    public static void regenerate(Player p, double amount) {
        if (!Double.isFinite(amount) || amount <= 0.0) return;
        double current = getStamina(p);
        double updated = Math.min(getMaxStamina(p), current + amount);

        if (updated <= current) return;
        setStamina(p, updated);

        StaminaHUD.updateStaminaBar(p);
    }

    public static void clear(Player p) {
        UUID id = p.getUniqueId();
        stamina.remove(id);
        maxStamina.remove(id);
    }
}
