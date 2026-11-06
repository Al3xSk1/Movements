package info.kovalski.movements.listener;

import info.kovalski.movements.utils.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LedgeShimmyListener implements Listener {

    private final Set<UUID> shimmying = new HashSet<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;

        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!PlayerUtil.isMovementAllowed(player)) {
            release(player);
            return;
        }

        Location loc = player.getLocation();
        World world = loc.getWorld();
        int blockX = loc.getBlockX();
        int blockY = loc.getBlockY();
        int blockZ = loc.getBlockZ();
        boolean foundLedge = false;

        // Check for a ledge (a red concrete block)
        for (int yOffset = -1; yOffset <= 0; yOffset++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Block block = world.getBlockAt(blockX + x, blockY + yOffset, blockZ + z);
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
                player.sendActionBar(Component.text("[Shimmying]", NamedTextColor.GOLD));
            }

            player.setGravity(false);
            player.setVelocity(player.getVelocity().setY(0));  // Stop vertical movement
            player.setFallDistance(0);
        } else if (shimmying.contains(id)) {
            // Stop shimmying if the player is no longer near the ledge.
            release(player);
            // XP is awarded when the chain ends, not immediately during skill usage.
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        // If the player is shimmying, cancel flight toggle and apply jump effect
        if (shimmying.contains(id)) {
            event.setCancelled(true);  // Cancel flight toggle while shimmying
            release(player);

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
        if (shimmying.contains(player.getUniqueId())) {
            release(player);
            // XP will be awarded after the chain ends, not here.
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        release(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        release(event.getEntity());
    }

    private void release(Player player) {
        if (!shimmying.remove(player.getUniqueId())) return;
        player.setGravity(true);
        player.setAllowFlight(false);
    }
}
