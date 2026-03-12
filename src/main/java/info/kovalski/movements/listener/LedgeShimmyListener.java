package info.kovalski.movements.listener;

import info.kovalski.movements.utils.PlayerUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LedgeShimmyListener implements Listener {

    private final Set<UUID> shimmying = new HashSet<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!PlayerUtil.isMovementAllowed(player)) return;

        Location loc = player.getLocation();
        boolean foundLedge = false;

        // Check for a ledge (a red concrete block)
        for (int yOffset = -1; yOffset <= 0; yOffset++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Block block = loc.clone().add(x, yOffset, z).getBlock();
                    if (block.getType() == Material.RED_CONCRETE) {
                        foundLedge = true;
                        break;
                    }
                }
                if (foundLedge) break;
            }
            if (foundLedge) break;
        }

        if (foundLedge) {
            // Start shimmying if the player is not already shimmying
            if (!shimmying.contains(id)) {
                shimmying.add(id);
                player.sendActionBar("§6[Shimmying]");  // Show action bar
            }

            player.setGravity(false);
            player.setVelocity(player.getVelocity().setY(0));  // Stop vertical movement
            player.setFallDistance(0);
        } else {
            // Stop shimmying if the player is no longer near the ledge
            if (shimmying.remove(id)) {
                player.setGravity(true); // Let gravity take over if the player leaves the ledge
                // XP is awarded when the chain ends, not immediately during skill usage.
            }
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        // If the player is shimmying, cancel flight toggle and apply jump effect
        if (shimmying.contains(id)) {
            event.setCancelled(true);  // Cancel flight toggle while shimmying
            shimmying.remove(id);

            player.setGravity(true);
            player.setAllowFlight(false);

            // Apply a vertical jump boost
            Vector jump = new Vector(0, 0.9, 0);
            player.setVelocity(jump);
            player.setFallDistance(0);

            // Play sound and particle effects
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1f, 1.1f);
            player.spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.2, 0.2, 0.2, 0.01);

            // XP will be awarded after the skill chain ends, not here.
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (shimmying.remove(player.getUniqueId())) {
            player.setGravity(true); // Let gravity take over when the player sneaks off the ledge
            // XP will be awarded after the chain ends, not here.
        }
    }
}
