package info.kovalski.movements.listener;

import info.kovalski.movements.utils.PlayerUtil;
import info.kovalski.movements.utils.StaminaUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RollListener implements Listener {

    private final Map<UUID, Long> sneakTimestamps = new HashMap<>();
    private final long rollWindow = 2500; // 2.5 seconds window to detect roll

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!PlayerUtil.isMovementAllowed(player)) return;
        if (event.isSneaking()) {
            sneakTimestamps.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        UUID id = player.getUniqueId();
        long lastSneak = sneakTimestamps.getOrDefault(id, 0L);
        long timeSinceSneak = System.currentTimeMillis() - lastSneak;

        if (timeSinceSneak <= rollWindow && player.getFallDistance() > 3) {
            if (StaminaUtil.consume(player, 5)) {
                // Cancel fall damage and perform the roll
                event.setCancelled(true);

                // Play sound and spawn particle effect for the roll
                player.playSound(player.getLocation(), Sound.ENTITY_SLIME_JUMP, 1f, 1.2f);
                player.spawnParticle(Particle.FLAME, player.getLocation(), 10, 0.3, 0.1, 0.3, 0.01);

                // Remove the sneak timestamp after the roll is complete
                sneakTimestamps.remove(id);
            }
        }
    }
}
