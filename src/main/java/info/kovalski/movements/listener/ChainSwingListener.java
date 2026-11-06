package info.kovalski.movements.listener;

import info.kovalski.movements.utils.PlayerUtil;
import info.kovalski.movements.utils.StaminaUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class ChainSwingListener implements Listener {

    private final Set<UUID> hanging = new HashSet<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;

        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (hanging.contains(id)) {
            if (!PlayerUtil.isMovementAllowed(player) || !hasChainAbove(player)) release(player);
            return;
        }

        if (!PlayerUtil.isMovementAllowed(player) || !hasChainAbove(player)) return;
        if (!StaminaUtil.consume(player, 5)) return;

        hanging.add(id);
        player.setGravity(false);
        player.setFallDistance(0);
        player.setAllowFlight(true);
        player.setFlying(false);
        player.setVelocity(new Vector(0, 0, 0));
        player.spawnParticle(Particle.CLOUD, player.getLocation(), 8, 0.2, 0.1, 0.2);
    }

    private boolean hasChainAbove(Player player) {
        for (int yOffset = 1; yOffset <= 3; yOffset++) {
            Block blockAbove = player.getLocation().add(0, yOffset, 0).getBlock();
            if (Tag.CHAINS.isTagged(blockAbove.getType())) return true;
        }
        return false;
    }

    private void release(Player player) {
        if (!hanging.remove(player.getUniqueId())) return;
        player.setGravity(true);
        player.setAllowFlight(false);
        player.setFlying(false);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (hanging.contains(id)) {
            if (!StaminaUtil.consume(player, 5)) return;

            release(player);

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
            release(player);

            player.setVelocity(new Vector(0, 0.5, 0));
            player.setFallDistance(0);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1f, 1.2f);
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
}
