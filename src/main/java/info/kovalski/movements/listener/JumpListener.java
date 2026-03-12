package info.kovalski.movements.listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class JumpListener implements Listener {

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();

        if (!player.isSneaking()) {  // Check if the player is not sneaking
            // Update the skill chain with the "Jump" skill name
        }
    }
}
