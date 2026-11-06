package info.kovalski.movements.listener;

import info.kovalski.movements.utils.CooldownManager;
import info.kovalski.movements.utils.MomentumUtil;
import info.kovalski.movements.utils.PlayerUtil;
import info.kovalski.movements.utils.StaminaUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChargeDashListener implements Listener {

    private final CooldownManager chargeCooldown = new CooldownManager();
    private final CooldownManager dashCooldown = new CooldownManager();
    private final Set<UUID> dashedThisAir = new HashSet<>();

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!PlayerUtil.isMovementAllowed(player)) return;
        if (player.isGliding() || player.isInsideVehicle()) return;

        double momentum = MomentumUtil.get(player);

        // Perform Charge Dash when the player is on the ground
        if (player.isOnGround()) {
            if (chargeCooldown.isOnCooldown(id)) return;
            if (momentum < MomentumUtil.getMaxMomentum()* 0.8) return;
            if (!StaminaUtil.consume(player, 10)) return;

            Vector chargeVector = player.getLocation().getDirection().setY(0.1).multiply(1.2);
            player.setVelocity(chargeVector);
            player.setFallDistance(0f);
            chargeCooldown.setCooldown(id, 1500);

            player.spawnParticle(Particle.CRIT, player.getLocation(), 10, 0.3, 0.1, 0.3);
            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1f, 1.0f);

        } else { // Dash in the air
            if (dashedThisAir.contains(id)) return;
            if (dashCooldown.isOnCooldown(id)) return;
            if (!StaminaUtil.consume(player, 15)) return;

            Vector direction = player.getLocation().getDirection();
            if (direction.getY() < 0.05) direction.setY(0.05); // Ensure the player dashes straight
            Vector dashVector = direction.normalize().multiply(1.0);

            player.setVelocity(dashVector);
            player.setFallDistance(0f);
            dashCooldown.setCooldown(id, 2000);
            dashedThisAir.add(id);

            player.spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 10, 0.3, 0.1, 0.3);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1f, 1.2f);
        }

        event.setCancelled(true); // Prevent item swap
    }

    @EventHandler
    public void onLand(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;
        Player player = event.getPlayer();
        if (player.isOnGround()) {
            dashedThisAir.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        chargeCooldown.clearCooldown(id);
        dashCooldown.clearCooldown(id);
        dashedThisAir.remove(id);
    }
}
