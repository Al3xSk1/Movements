package info.kovalski.movements.utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PlayerUtil {
    public static boolean isMovementAllowed(Player player) {
        GameMode mode = player.getGameMode();
        return mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE;
    }

}
