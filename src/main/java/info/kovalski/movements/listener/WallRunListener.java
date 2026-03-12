package info.kovalski.movements.listener;

import info.kovalski.movements.utils.CooldownManager;
import info.kovalski.movements.utils.PlayerUtil;
import info.kovalski.movements.utils.StaminaUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class WallRunListener implements Listener {

    private final CooldownManager wallRunCooldown = new CooldownManager();
    private static final Material WALL_RUN_BLOCK = Material.NETHER_WART_BLOCK;
    private static final double STAMINA_COST = 15;
    private static final long COOLDOWN_MS = 1500;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!canStartWallRun(p)) return;

        startWallRun(p);

        // Add skill to the chain instead of awarding XP immediately
    }

    private boolean canStartWallRun(Player p) {
        if (!PlayerUtil.isMovementAllowed(p)) return false;
        if (!p.isSprinting()) return false;
        if (p.isOnGround()) return false;
        if (p.getVelocity().getY() < -0.2) return false;
        if (wallRunCooldown.isOnCooldown(p.getUniqueId())) return false;
        if (!isNextToWallRunBlock(p)) return false;
        return StaminaUtil.has(p, STAMINA_COST);
    }

    private void startWallRun(Player p) {
        wallRunCooldown.setCooldown(p.getUniqueId(), COOLDOWN_MS);
        StaminaUtil.consume(p, STAMINA_COST);

        Vector direction = p.getLocation().getDirection().setY(0).normalize();

        // Apply velocity to perform the wall run
        Vector velocity = direction.multiply(1.5).add(new Vector(0, 0.45, 0)); // tweak Y as needed

        p.setVelocity(velocity);

        // Spawn particle effects and play sound for wall run
        p.getWorld().spawnParticle(Particle.CRIT, p.getLocation(), 10, 0.1, 0.1, 0.1, 0);
        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 0.5f, 2f);
    }

    private boolean isNextToWallRunBlock(Player p) {
        Location loc = p.getLocation();
        Block[] nearby = {
                loc.clone().add(0.6, 0, 0).getBlock(),
                loc.clone().add(-0.6, 0, 0).getBlock(),
                loc.clone().add(0, 0, 0.6).getBlock(),
                loc.clone().add(0, 0, -0.6).getBlock()
        };

        for (Block b : nearby) {
            if (b.getType() == WALL_RUN_BLOCK) {
                return true;
            }
        }
        return false;
    }
}
