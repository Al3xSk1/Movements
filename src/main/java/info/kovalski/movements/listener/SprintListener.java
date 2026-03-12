package info.kovalski.movements.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class SprintListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Only update skill chain XP when a specific skill is performed, not during regular sprinting
        // Removed sprinting XP award logic here
    }
}
