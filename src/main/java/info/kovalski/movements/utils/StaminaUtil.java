package info.kovalski.movements.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaminaUtil {

    private static final Map<UUID, Double> stamina = new HashMap<>();
    private static final Map<UUID, Double> maxStamina = new HashMap<>();
    private static final Map<UUID, Long> lastSkillTime = new HashMap<>();
    private static final Map<UUID, Integer> skillChainXP = new HashMap<>();

    private static final double DEFAULT_MAX_STAMINA = 100.0;
    private static final double ADRENALINE_RUSH_THRESHOLD = 20.0;  // Stamina threshold for Adrenaline Rush (below 20%)
    private static final double ADRENALINE_RUSH_MULTIPLIER = 1.5;  // Adrenaline Rush multiplier for speed and damage

    private static final long INACTIVITY_TIMEOUT = 10000; // 10 seconds inactivity timeout

    // Get the current stamina of the player
    public static double getStamina(Player p) {
        return stamina.getOrDefault(p.getUniqueId(), DEFAULT_MAX_STAMINA);
    }

    // Set the player's stamina (clamps the value to max stamina)
    public static void setStamina(Player p, double value) {
        stamina.put(p.getUniqueId(), Math.max(0.0, Math.min(getMaxStamina(p), value)));
    }

    // Get the player's max stamina
    public static double getMaxStamina(Player p) {
        return maxStamina.getOrDefault(p.getUniqueId(), DEFAULT_MAX_STAMINA);
    }

    // Set the player's max stamina
    public static void setMaxStamina(Player p, double value) {
        maxStamina.put(p.getUniqueId(), value);
    }

    // Consume stamina (decrease stamina by a given amount)
    public static boolean consume(Player p, double amount) {
        double current = getStamina(p);
        if (current < amount) return false;

        setStamina(p, current - amount);

        // Update the stamina HUD after consumption
        StaminaHUD.updateStaminaBar(p);

        return true;
    }

    // Check if the player has enough stamina to perform an action
    public static boolean has(Player p, double amount) {
        return getStamina(p) >= amount;
    }

    // Regenerate the player's stamina by a given amount
    public static void regenerate(Player p, double amount) {
        double current = getStamina(p);
        double updated = Math.min(getMaxStamina(p), current + amount);

        setStamina(p, updated);

        // Update the stamina HUD after regeneration
        StaminaHUD.updateStaminaBar(p);
    }

    // Reset the player's stamina to the default max value
    public static void reset(Player p) {
        setStamina(p, DEFAULT_MAX_STAMINA);
    }

    public static void applyAdrenalineRushEffects(Player p) {
        if (isInAdrenalineRush(p)) {
            // Apply adrenaline rush effects (speed boost, etc.)
            StaminaHUD.startDisplaying(p);  // Update the HUD with new color
        }
    }

    // Check if the player is in Adrenaline Rush
    public static boolean isInAdrenalineRush(Player p) {
        return getStamina(p) < getMaxStamina(p) * 0.2;  // Threshold for Adrenaline Rush
    }

    // Update skill chain and XP tracking (XP awarded after inactivity timeout)
    public static void updateSkillChainXP(Player p) {
        UUID id = p.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long lastSkillTimeForPlayer = lastSkillTime.getOrDefault(id, currentTime);

        if (currentTime - lastSkillTimeForPlayer > INACTIVITY_TIMEOUT) {
            // XP reward at the end of skill chain due to inactivity timeout
            if (skillChainXP.containsKey(id) && skillChainXP.get(id) > 0) {
                gainXP(p, skillChainXP.get(id)); // Apply XP after chain ends
            }
            skillChainXP.put(id, 0); // Reset chain XP
        }

        // Update skill chain XP (you can add more logic to track skill usage)
        skillChainXP.put(id, skillChainXP.getOrDefault(id, 0) + 10); // Add XP for skill performed
        lastSkillTime.put(id, currentTime); // Update last skill performed time
    }

    // Gain XP for player after skill chain ends
    public static void gainXP(Player p, int xp) {
        LevelingUtil.addXP(p, xp);  // Use LevelingUtil to handle XP award and leveling
    }
}