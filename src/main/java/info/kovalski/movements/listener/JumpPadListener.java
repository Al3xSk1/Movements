package info.kovalski.movements.listener;

import info.kovalski.movements.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class JumpPadListener implements Listener {

    private static final double HORIZONTAL_BOOST = 2.2; // enough to cover ~15 blocks
    private static final double VERTICAL_BOOST = 1.2;
    private static final long COOLDOWN_MS = 10_000;

    private final CooldownManager cooldowns = new CooldownManager();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!player.isOnGround()) return;
        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) return;

        Location loc = player.getLocation();
        Material blockBelow = loc.clone().subtract(0, 1, 0).getBlock().getType();

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
}
