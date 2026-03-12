package info.kovalski.movements.utils;

import org.bukkit.entity.Player;

public class StaminaHUD {

    // This method is called to update the stamina bar on the player’s screen (using the XP bar)
    public static void startDisplaying(Player p) {
        double currentStamina = StaminaUtil.getStamina(p);
        double maxStamina = StaminaUtil.getMaxStamina(p);

        // Calculate stamina percentage (between 0 and 1)
        double staminaPercentage = currentStamina / maxStamina;

        // Check if the player is in Adrenaline Rush (stamina < 20%)
        // You can change the color or effects for Adrenaline Rush as needed here
        if (currentStamina < (maxStamina * 0.2)) {
            // Apply any additional effects when in Adrenaline Rush if needed (such as a visual or sound effect)
            // p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1f, 1.1f); // Example sound effect
        }

        // Set the XP bar to represent the stamina percentage
        p.setLevel(0);
        p.setTotalExperience(0);
        p.setExp((float) staminaPercentage);  // Use the XP bar as a stamina bar
    }

    // This can be updated in other methods as needed
    public static void updateStaminaBar(Player p) {
        startDisplaying(p); // Update the XP bar with the latest stamina value
    }
}
