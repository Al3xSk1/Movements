package info.kovalski.movements.listener;

import info.kovalski.movements.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class JumpPadListener implements Listener {

    private static final double HORIZONTAL_BOOST = 2.2; // enough to cover ~15 blocks
    private static final double VERTICAL_BOOST = 1.2;
    private static final long COOLDOWN_MS = 10_000;

    private final CooldownManager cooldowns = new CooldownManager();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;

        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!player.isOnGround()) return;

        Location loc = player.getLocation();
        Material blockBelow = loc.getWorld().getBlockAt(
                loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ()).getType();

        if (blockBelow != Material.REDSTONE_BLOCK) return;
        if (cooldowns.isOnCooldown(id)) return;

        // Apply boost
        Vector dir = player.getLocation().getDirection().setY(0).normalize().multiply(HORIZONTAL_BOOST);
        dir.setY(VERTICAL_BOOST);
        player.setVelocity(dir);

        // Effects
        player.playSound(loc, Sound.ENTITY_SLIME_JUMP, 1f, 1.0f);
        player.spawnParticle(Particle.CLOUD, loc, 15, 0.2, 0.2, 0.2, 0.01);

        // Set cooldown
        cooldowns.setCooldown(id, COOLDOWN_MS);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cooldowns.clearCooldown(event.getPlayer().getUniqueId());
    }
}
