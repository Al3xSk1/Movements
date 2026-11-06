package info.kovalski.movements.utils;

import org.bukkit.entity.Player;

public class StaminaHUD {

    // This method is called to update the stamina bar on the player’s screen (using the XP bar)
    private static void startDisplaying(Player p) {
        double currentStamina = StaminaUtil.getStamina(p);
        double maxStamina = StaminaUtil.getMaxStamina(p);
        float progress = (float) Math.max(0.0, Math.min(1.0, currentStamina / maxStamina));
        p.setExp(progress);
    }

    // This can be updated in other methods as needed
    public static void updateStaminaBar(Player p) {
        startDisplaying(p); // Update the XP bar with the latest stamina value
    }
}
