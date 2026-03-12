package info.kovalski.movements.listener;

import info.kovalski.movements.utils.PlayerUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LadderListener implements Listener {

    private final Set<UUID> climbing = new HashSet<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!PlayerUtil.isMovementAllowed(player)) return;

        boolean touchingLadder = false;

        // Check if the player is touching the ladder (using RED_WOOL as ladder placeholder)
        for (int y = 0; y <= 1; y++) {
            Location loc = player.getLocation().clone().add(0, y, 0);
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (Math.abs(x) + Math.abs(z) != 1) continue;

                    Block block = loc.clone().add(x, 0, z).getBlock();
                    if (block.getType() == Material.RED_WOOL) {
                        touchingLadder = true;
                        break;
                    }
                }
                if (touchingLadder) break;
            }
            if (touchingLadder) break;
        }

        // If the player is touching the ladder, handle skill chain logic
        if (touchingLadder) {
            if (!climbing.contains(id)) {
                climbing.add(id);
                player.sendActionBar("§6[Climbing]");  // Show action bar indicating climbing
            }

            player.setGravity(false);
            player.setFallDistance(0);

            // Adjust the player's velocity based on their pitch (up or down)
            float pitch = player.getLocation().getPitch();

            if (pitch < -30) {
                player.setVelocity(new Vector(0, 0.15, 0));  // Climbing up
            } else if (pitch > 30) {
                player.setVelocity(new Vector(0, -0.3, 0));  // Climbing down
            } else {
                player.setVelocity(new Vector(0, 0, 0));  // No vertical movement if pitch is neutral
            }
        } else if (climbing.remove(id)) {
            player.setGravity(true);  // Stop skill chain if not touching ladder anymore
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        // If the player sneaks off the ladder, we don't end the skill chain. Just allow gravity
        if (climbing.remove(id)) {
            player.setGravity(true);  // Stop skill chain when sneaking off the ladder
        }
    }
}
