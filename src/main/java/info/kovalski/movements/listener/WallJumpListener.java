package info.kovalski.movements.listener;

import info.kovalski.movements.utils.CooldownManager;
import info.kovalski.movements.utils.PlayerUtil;
import info.kovalski.movements.utils.StaminaUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class WallJumpListener implements Listener {

    private final CooldownManager climbCooldown = new CooldownManager();
    private static final double STAMINA_COST = 10;

    @EventHandler
    public void onSneakClimb(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        if (!e.isSneaking()) return;
        if (!PlayerUtil.isMovementAllowed(p)) return;
        if (climbCooldown.isOnCooldown(id)) return;

        if (!isTouchingRedTerracottaWall(p)) return;
        if (!StaminaUtil.consume(p, STAMINA_COST)) return;

        // Apply vertical velocity for climbing
        Vector climbVector = new Vector(0, 1.1, 0);
        p.setVelocity(climbVector);

        p.playSound(p.getLocation(), Sound.ENTITY_SPIDER_STEP, 1f, 1.2f);
        climbCooldown.setCooldown(id, 1500);
    }

    private boolean isTouchingRedTerracottaWall(Player p) {
        Location loc = p.getLocation();
        World world = loc.getWorld();

        return isClimbableWall(world, loc.clone().add(0.7, 0, 0)) ||
                isClimbableWall(world, loc.clone().add(-0.7, 0, 0)) ||
                isClimbableWall(world, loc.clone().add(0, 0, 0.7)) ||
                isClimbableWall(world, loc.clone().add(0, 0, -0.7));
    }

    private boolean isClimbableWall(World world, Location base) {
        Block lower = world.getBlockAt(base);
        Block upper = world.getBlockAt(base.clone().add(0, 1, 0));

        return isRedTerracotta(lower) && isRedTerracotta(upper);
    }

    private boolean isRedTerracotta(Block block) {
        return block.getType() == Material.RED_TERRACOTTA;
    }
}
