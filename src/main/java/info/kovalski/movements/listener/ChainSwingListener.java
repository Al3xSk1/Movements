package info.kovalski.movements.listener;

import info.kovalski.movements.utils.PlayerUtil;
import info.kovalski.movements.utils.StaminaUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class ChainSwingListener implements Listener {

    private final Set<UUID> hanging = new HashSet<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        // Skip if stationary
        if (event.getFrom().distanceSquared(event.getTo()) == 0) return;
        if (!PlayerUtil.isMovementAllowed(player)) return;

        // Cooldown check

        // Check for chains above
        for (int yOffset = 1; yOffset <= 3; yOffset++) {
            Block blockAbove = player.getLocation().add(0, yOffset, 0).getBlock();
            if (blockAbove.getType() == Material.CHAIN && !hanging.contains(id)) {

                if (!StaminaUtil.consume(player, 5)) return;

                hanging.add(id);

                player.setGravity(false);
                player.setFallDistance(0);
                player.setAllowFlight(true);
                player.setFlying(false);
                player.setVelocity(new Vector(0, 0, 0));

                player.spawnParticle(Particle.CLOUD, player.getLocation(), 8, 0.2, 0.1, 0.2);

                return;
            }
        }

        // Reset if falling off chain
        if (hanging.contains(id)) {
            hanging.remove(id);

            player.setGravity(true);
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (hanging.contains(id)) {
            if (!StaminaUtil.consume(player, 5)) return;

            hanging.remove(id);

            player.setGravity(true);
            player.setAllowFlight(false);
            player.setFlying(false);

            Vector dir = player.getLocation().getDirection().clone();
            dir.setY(Math.max(0.5, dir.getY()));
            Vector launch = dir.normalize().multiply(1.3);

            player.setVelocity(launch);
            player.setFallDistance(0);
            player.playSound(player.getLocation(), Sound.ENTITY_SLIME_JUMP, 1f, 1.1f);
            player.spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.2, 0.2, 0.2, 0.01);
        }
    }

    @EventHandler
    public void onJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (hanging.contains(id)) {
            event.setCancelled(true);
            hanging.remove(id);

            player.setGravity(true);
            player.setAllowFlight(false);
            player.setFlying(false);

            player.setVelocity(new Vector(0, 0.5, 0));
            player.setFallDistance(0);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1f, 1.2f);
        }
    }
}
