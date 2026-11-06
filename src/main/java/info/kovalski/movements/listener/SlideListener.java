package info.kovalski.movements.listener;

import info.kovalski.movements.utils.CooldownManager;
import info.kovalski.movements.utils.MomentumUtil;
import info.kovalski.movements.utils.PlayerUtil;
import info.kovalski.movements.utils.StaminaUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class SlideListener implements Listener {

    private final CooldownManager slideCooldown = new CooldownManager();

    @EventHandler
    public void onSlide(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!PlayerUtil.isMovementAllowed(player)) return;
        if (!event.isSneaking()) return;
        if (!player.isSprinting()) return;
        if (!player.isOnGround()) return;
        if (slideCooldown.isOnCooldown(id)) return;
        if (MomentumUtil.get(player) < MomentumUtil.getMaxMomentum() * 0.9) return;
        if (!StaminaUtil.consume(player, 3)) return;

        // Apply the slide velocity
        Vector slideVec = player.getLocation().getDirection().multiply(1.0).setY(0.1);
        player.setVelocity(slideVec);

        // Update the player's momentum
        double currentMomentum = MomentumUtil.get(player);
        MomentumUtil.set(player, Math.max(0.0, currentMomentum - 0.1));

        // Spawn particle effects and play sound
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.3, 0.1, 0.3);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1.0f);

        // Set cooldown for the slide action
        slideCooldown.setCooldown(id, 1500);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        slideCooldown.clearCooldown(event.getPlayer().getUniqueId());
    }
}
